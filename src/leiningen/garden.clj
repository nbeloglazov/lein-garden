(ns leiningen.garden
  (:require [clojure.pprint :refer [pprint]]
            [me.raynes.fs :as fs]
            [watchtower.core :as wc]
            [clojure.java.io :as io]
            [clojure.tools.reader :as r]
            [clojure.tools.reader.reader-types :as rt]
            [garden.core :as gc]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px]]
            garden.types
            [clojure.string :as string]))

(defn- get-dir [project type]
  (fs/file (:root project) (get-in project [:garden type])))

(def cssable?
  (some-fn vector? #(instance? garden.types.CSSAtRule %)))

(def date-format (java.text.SimpleDateFormat. "HH:mm:ss"))

(defn time-now []
  (.format date-format (java.util.Date.)))

(defmacro with-temp-ns [& body]
  (let [refers (concat '[(clojure.core/refer 'clojure.core)
                         (clojure.core/refer 'leiningen.garden)]
                       body)]
   `(let [~'base-ns (ns-name *ns*)]
      (try
        (in-ns 'temp-ns#)
        ~@(map (fn [exp] `(eval '~exp)) refers)
        ~@body
        (finally
          (let [~'cur-ns (ns-name *ns*)]
            (in-ns  ~'base-ns)
            ; Just in case if user changed ns in his garden file
            ; delete his custom ns
            (when-not (= ~'cur-ns ~'base-ns)
              (remove-ns ~'cur-ns))
            (remove-ns 'temp-ns#)))))))

(def ^:dynamic *current-file* nil)

(defn- cssable-from-reader [reader]
  (try
    (loop [css []]
      (if-let [exp (r/read reader false nil)]
        (let [obj (eval exp)]
          (if (cssable? obj)
            (recur (conj css obj))
            (recur css)))
        css))
    (catch Exception e
      (println "Error on line" (rt/get-line-number reader)))))

(defn cssable-from-file [file]
  (-> file
      fs/file
      io/input-stream
      rt/input-stream-push-back-reader
      rt/indexing-push-back-reader
      cssable-from-reader))

(defn- cssable-from-file-in-temp-ns [file]
  (binding [*current-file* file]
    (with-temp-ns (leiningen.garden/cssable-from-file leiningen.garden/*current-file*))))

(defn- path-relative-to [file dir]
  (let [dir-path (fs/absolute-path dir)
        file-path (fs/absolute-path file)]
    (assert (.startsWith file-path dir-path))
    (subs file-path (inc (count dir-path)))))

(defn- change-extension [file new-ext]
  (let [path (fs/absolute-path file)]
    (string/replace path #"\.\w+$" new-ext)))

(defn- render-single-file [src-dir out-dir file]
  (println (time-now) "compiling" (fs/base-name file))
  (let [out-file (fs/file out-dir (path-relative-to file src-dir))
        rules (cssable-from-file-in-temp-ns file)]
    (when rules
     (fs/mkdirs (fs/parent out-file))
     (gc/css {:output-to (change-extension out-file ".css")}
            rules)))
  (println))

(defn- render-files [src-dir out-dir files]
  (doseq [file files]
    (render-single-file src-dir out-dir file)))

(defn- once
  "Transform garden files to css once and then exit."
  [src-dir out-dir]
  (render-files src-dir out-dir (fs/find-files* src-dir fs/file?)))

(defn- auto
  "Watch garden files and transform them to css after any changes."
  [src-dir out-dir]
  (wc/watcher [src-dir]
    (wc/rate 1000)
    (wc/on-change #(render-files src-dir out-dir %))
    (wc/watch)))

(defn- valid-project? [project]
  (let [required [[[:garden]
                   ":garden config not found.\ncheck project.clj"]
                  [[:garden :source-path]
                   ":source-path not found in :garden config.\ncheck project.clj"]
                  [[:garden :output-path]
                   ":output-path not found in :garden config.\ncheck project.clj"]]
        errors (for [[keys mess] required
                        :when (not (get-in project keys))]
                 mess)]
    (if (empty? errors)
      true
      (do (println (first errors)) false))))

(defn garden
  "Transform garden files to css."
  {:subtasks [#'once #'auto]}
  ([project] (garden project "once"))
  ([project subtask]
     (when (valid-project? project)
      (let [src-dir (get-dir project :source-path)
            out-dir (get-dir project :output-path)]
        (case subtask
          "once" (once src-dir out-dir)
          "auto" (auto src-dir out-dir))))))
