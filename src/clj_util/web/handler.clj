(ns clj-util.web.handler
  (:require [clj-util.web.routes :as routes]
            [clj-util.web.response :as r]
            [clj-util.web.middleware :as m]
            [shadow.http.push-state :as push-state]))

(defn ^:private ring-handler
  [routes handlers]
  (let [router (routes/make-router routes)]
    (fn [{:keys [uri request-method] :as req}]
      (let [{:keys [handler route-params]} (router uri)
            route-handlers (get handlers handler)
            f (get route-handlers request-method)]
        (if f
          (f (cond-> req
               route-params (assoc :route-params route-params)))
          (r/not-found {:uri uri :method request-method}))))))

(defn dev-handler
  [routes handlers]
  (-> (ring-handler routes handlers)
      m/wrap-reload
      push-state/handle))

(defn prod-handler
  [routes handlers]
  (-> (ring-handler routes handlers)
      m/wrap-parse-edn-body))

(comment
  "Examples of how to use it"
  (def routes ["/api"
               [["/ping" ::api-ping]
                [["/echo/" :foo] ::api-echo]]])
  (def handlers {::api-ping {:get (fn [_] (r/ok "pong"))}
                 ::api-echo {:get (fn [{:keys [route-params]}] (r/ok route-params))}})

  (def handler (prod-handler routes handlers))

  (handler {:uri "/api/ping" :request-method :get})
  (handler {:uri "/api/echo/seanvincent" :request-method :get}))
