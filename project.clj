(defproject org.clojars.nbeloglazov/lein-garden "0.1.0-SNAPSHOT"
  :description "Lein plugin for compiling garden files to css."
  :url "https://github.com/nbeloglazov/lein-garden"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[watchtower "0.1.1"]
                 [me.raynes/fs "1.4.4"]
                 [garden "1.1.2"]
                 [org.clojure/tools.reader "0.7.8"]]
  :eval-in-leiningen true)
