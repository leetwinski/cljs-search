(ns search.views
  (:require [re-frame.core :as re-frame]
            [search.subs :as subs]
            [search.events :as events]
            [reagent.core :as r]))

(defn search-panel []
  (let [input-text (r/atom "")]
    (fn []
      [:div {:style {:width "100%"
                     :text-align "center"}}
       [:input
        {:style {:width "50%"
                 :min-width "300px"}
         :value @input-text
         :on-change (fn [e]
                      (let [text (-> e .-target .-value)]
                        (reset! input-text text)
                        (re-frame/dispatch [::events/send-search text])))}]])))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        result (re-frame/subscribe [::subs/results])]
    (fn []
      [:div
       [search-panel]
       [:ul (for [hit (-> @result :result :hits)]
              ^{:key (:_id hit)} [:li {} hit])]])))
