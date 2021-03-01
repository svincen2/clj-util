(ns clj-util.uuids
  (:import [java.util UUID]))

(defn string->uuid
  [s]
  (try
    (UUID/fromString (str s))
    (catch IllegalArgumentException _
      nil)))
