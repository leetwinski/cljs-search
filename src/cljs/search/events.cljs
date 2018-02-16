(ns search.events
  (:require [re-frame.core :as re-frame]
            [search.db :as db]
            [search.service :as service]))

;; database events

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(re-frame/reg-event-db
 ::update-history
 (fn [db [_ search-term]]
   (update db :history conj search-term)))


(re-frame/reg-event-db
 ::reset-history
 (fn [db _]
   (assoc db :history nil)))


(re-frame/reg-event-db
 ::update-results
 (fn [db [_ results]]
   (assoc db :results results)))


;; effect triggering events

(re-frame/reg-event-fx
 ::send-search
 (fn [{db :db} [_ search-term]]
   {:send-off {:chan (get-in db [:service :req])
               :payload search-term}}))


(re-frame/reg-event-fx
 ::initialize-service
 (fn [{db :db} [_ {:keys [host port index-name field-name]}]]
   (let [[req res err] (service/make-search-service host port {:index-name index-name
                                                               :field-name field-name})]
     {:db (assoc db :service {:req req
                              :res res
                              :err err})
      :subscribe-to-results [res err]})))
