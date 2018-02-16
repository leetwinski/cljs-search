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
   returns a tuple3 of input requests channel to response channel to errors channel,
   where requests expects the search strings to be queried
   while response contains [query result] vector"
  [host port {:keys [index-name field-name]
              :or {field-name +all-fields+}}]
  
  (let [endpoint (make-search-endpoint host port index-name)
        requests (a/chan (a/sliding-buffer 1))
        results (a/chan (a/sliding-buffer 1))
        [succ err] (a/split (comp :success :result)
                            results
                            (a/sliding-buffer 1)
                            (a/sliding-buffer 10))
        succ (a/map #(update % :result (comp :hits :body)) [succ] 1)
        search-fn (partial search endpoint field-name)]
    (go-loop []
      (when-let [search-text (a/<! requests)]
        (a/pipe (search-fn search-text)
                results
                false))
      (recur))
    [requests succ err]))


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
