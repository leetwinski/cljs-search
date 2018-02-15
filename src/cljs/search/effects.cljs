(ns search.effects
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [re-frame.fx :as fx]
            [re-frame.core :as re-frame]
            [search.events :as events]
            [clojure.string :as cs]
            [cljs.core.async :as a]))

(fx/reg-fx
 :send-off
 (fn [{:keys [chan payload]}]
   (when-not (cs/blank? payload)
     (go (a/>! chan (cs/trim payload))))))


(fx/reg-fx
 :subscribe-to-results
 (fn [chan]
   (go-loop []
     (when-let [results (a/<! chan)]
       (println results)
       (re-frame/dispatch [::events/update-results results])
       (recur)))))
