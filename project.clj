(defproject search "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [clj-http "3.7.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.1"]
                 [slingshot "0.12.2"]
                 [environ "1.1.0"]
                 [reagent "0.7.0"]
                 [org.clojure/core.async "0.4.474"]
                 [compojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [cljs-http "0.1.44"]
                 [re-frame "0.10.4"]
                 [ring-logger "0.7.7"]
                 [ring-cors "0.1.11"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-ring "0.12.3"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :ring {:handler search.core/app}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [figwheel-sidecar "0.5.13"]
                   [com.cemerick/piggieback "0.2.2"]
                   ;; java 9 workaround
                   [javax.xml.bind/jaxb-api "2.3.0"]]

    :plugins      [[lein-figwheel "0.5.13"]
                   [lein-environ "1.1.0"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "search.core/mount-root"}
     :compiler     {:main                 search.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            search.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}

  )
