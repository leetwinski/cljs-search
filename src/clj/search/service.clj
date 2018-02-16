(ns search.service
  (:require [clj-http.client :as client]
            [slingshot.slingshot :refer [throw+ try+]]
            [clojure.tools.logging :as log]))

(declare make-query)

(defn search [url field-name query-string]
  (log/info url field-name query-string)
  (let [result (client/post url
                            {:accept :json
                             :content-type :json
                             :body (make-query field-name query-string)})]
    (client/json-decode (:body result))))

(defn- make-query [field-name query-string]
  (try+ 
   (client/json-encode {"query"
                        {"match"
                         {field-name
                          {"query" query-string
                           "operator" "and"}}}})
   (catch Object obj
     (log/warn obj)
     (throw+ obj))))
