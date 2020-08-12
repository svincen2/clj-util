(ns clj-util.config
  (:refer-clojure :rename {get cget
                           get-in cget-in})
  (:require [clojure.java.io :as io]
            [aero.core :as aero]))

(def ^{:private true :dynamic true} *config*)

(def ^:private config-file "config.edn")

(defn read-config
  ([]
   (read-config (keyword (or (System/getenv "CONFIG_PROFILE")
                             (System/getProperty "config.profile")
                             "dev"))))
  ([profile]
   (aero/read-config (io/resource config-file) {:profile profile})))

(defmacro with-config
  [config & body]
  `(binding [*config* ~config]
     ~@body))

(defn get
  ([k]
   (get *config* k))
  ([config k]
   (get config k nil))
  ([config k default]
   (cget config k default)))

(defn get-in
  ([k]
   (get-in *config* k))
  ([config k]
   (get-in config k nil))
  ([config k default]
   (cget-in config k default)))



(comment

  (System/setProperty "test.keyword" "hello")
  (System/setProperty "test.long" "12345")
  (System/setProperty "test.map.some-key" "Hello!")

  (:test/keyword (read-config))

  (get (read-config) :test/keyword)

  (with-config (read-config)
    (get-in [:test/map :some-key])))
