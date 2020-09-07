(ns clj-util.web.server
  (:require [ring.adapter.jetty :as jetty]))

(defn start
  [handler port]
  (jetty/run-jetty handler {:port port :join? false}))
