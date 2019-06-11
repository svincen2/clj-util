(ns clj-util.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aero.core :as aero]))

;; pnv = env || property
(defmethod aero/reader 'pnv
  [{:keys [profile] :as opts} tag value]
  (or (aero/reader opts 'env value) (aero/reader opts 'prop value)))

;; vec = EDN vector, or string representation of EDN vector
(defmethod aero/reader 'vec
  [{:keys [profile] :as opts} tag value]
  (cond
    (vector? value) value
    (and (string? value) (not (empty? value))) (edn/read-string value)
    ;; nil to use a default, if this is an option of an #or
    :else nil))

(defn pnv
  "Get the Java system property or environment variable k.
  First looks for a system property, then environment variable.
  If k is not set as a prop/env, returns default."
  ([k]
   (pnv k nil))
  ([k default]
   (or (System/getProperty k) (System/getenv k) default)))

(defn read-config
  ([]
   (let [profile (pnv "DEPLOYMENT_TIER" "production")]
     (read-config (keyword profile))))
  ([profile]
   (aero/read-config (io/resource "config.edn") {:profile profile})))
