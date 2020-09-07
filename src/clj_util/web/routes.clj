(ns clj-util.web.routes
  (:require [bidi.bidi :as bidi]))

(defn make-router
  [routes]
  (fn
    ([uri]
     (bidi/match-route routes uri))
    ([route route-params]
     (apply (partial bidi/path-for routes) route (mapcat seq route-params)))))

(comment
  "Examples of how to use it"
  (def router (make-router ["/"
                            [["" ::app-root]
                             ["api"
                              [["/ping" ::api-ping]
                               ["/echo" ::api-echo]
                               ["/cards" ::api-cards]
                               ["/decks" ::api-decks]]]
                             [[:resource] ::app-resource]]]))
  (router "/foo.png")
  (router ::app-resource {:resource "foo.png"}))
