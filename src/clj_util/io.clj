(ns clj-util.io
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn json->edn
  "Converts a JSON file to EDN."
  [filename & opts]
  (let [{:keys [pretty]} opts
        data (json/read (io/reader filename))
        new-filename (str (string/replace filename #"\.json$" "") ".edn")]
    (if pretty
      (spit new-filename (with-out-str (pprint data)))
      (spit new-filename data))))
