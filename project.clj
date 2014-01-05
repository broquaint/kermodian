(defproject kermodian "0.1.0-SNAPSHOT"
  :description "Review git commits"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2127"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.1.4"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
              :builds [{:id "kermodian"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "kermodian.js"
                                   :output-dir "script/out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "dev"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "main.js"
                                   :output-dir "react/out"
                                   :optimizations :none
                                   :source-map true
                                   :foreign-libs [{:file "om/react.js"
                                                   :provides ["React"]}]
                                   :externs ["om/externs/react.js"]}}]})
