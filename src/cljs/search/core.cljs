(ns search.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clojure.core.async :as a]
            [search.events :as events]
            [search.views :as views]
            [search.config :as config :refer [+settings+]]
            [search.effects :as effects]
            [search.service :as service]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (println "app settings: " +settings+)))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/initialize-service +settings+])
  (dev-setup)
  (mount-root))
