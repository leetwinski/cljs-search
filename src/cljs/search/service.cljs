(ns search.service
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as a]
            [clojure.string :as cs]
            [cljs-http.client :as http]))

(declare search make-request make-search-endpoint)

(def ^{:private true
       :const true}
  +all-fields+ "_all")

(defn make-search-service
  "Creates elastic search service channels
   for passed host and port.n
   returns a tuple2 of input requests channel to responses channel,
   where requests expects the search strings to be queried
   while responses contains [query result] vector"
  [host port {:keys [index-name field-name]
              :or {field-name +all-fields+}}]
  
  (let [endpoint (make-search-endpoint host port index-name)
        requests (a/chan (a/sliding-buffer 1))
        responses (a/chan (a/sliding-buffer 1)
                          (comp (filter (comp :success :result))
                                (map #(update % :result (comp :hits :body)))))
        search-fn (partial search endpoint field-name)]
    (go-loop []
      (when-let [search-text (a/<! requests)]
        (a/pipe (search-fn search-text)
                responses
                false))
      (recur))
    [requests responses]))


(defn- make-search-endpoint [host port index-name]
  (let [base (str host ":" port "/")
        index (when index-name (str index-name "/"))]
    (str base index "_search")))


(defn- search [endpoint field-name search-text]
  (http/post endpoint
             {:json-params (make-request (or field-name +all-fields+) search-text)
              :with-credentials? false
              :channel (a/chan 1 (map (partial hash-map :q search-text :result)))}))

(defn- make-request [field-name search-text]
  {:query {:match {field-name {:query search-text
                               :operator "and"}}}})
