(ns search.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.response :as resp]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.logger :refer [wrap-with-logger]]
            [ring.middleware.cors :refer [wrap-cors]]
            [environ.core :refer [env]]
            [search.service :as service]
            [clojure.tools.logging :as log]))


(def ^{:private true
       :const true}
  +default-settings+
  {:elastic-host "http://localhost"
   :elastic-port 9200})


(def ^{:private true
       :const true}
  +settings+
  (merge-with #(or %1 %2)
              (select-keys env [:elastic-host
                                :elastic-port])
              +default-settings+))

(defroutes app-routes
  (GET "/health" [] {:body {:health "ok"}})
  (POST "/search" {:keys [body]}
        (let [{:keys [elastic-host elastic-port]} +settings+
              result (service/search (str elastic-host ":" elastic-port "/test/_search")
                                     "text"
                                     (body "q"))]
          (log/info body result)
          {:body result})))

(def app
  (-> app-routes
      (wrap-json-body {:keywords true})
      wrap-json-response
      (wrap-defaults api-defaults)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :put :delete])
      wrap-with-logger))


(log/info "using env params:"
          "received" env
          "effective" +settings+)
