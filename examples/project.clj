(defproject lein-garden-examples "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [garden "1.1.2"]]

  :plugins [[lein-garden "0.1.0-SNAPSHOT"]]

  :garden {:source-path "src/garden"
           :output-path "resources/css"})
