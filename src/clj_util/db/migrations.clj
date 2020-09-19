(ns clj-util.db.migrations
  (:require [clojure.string :as string]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(def ^:dynamic *db-spec*)

(defmacro with-db-spec
  [db-spec & body]
  `(binding [*db-spec* ~db-spec]
     ~@body))

(defn config
  ([]
   (config *db-spec*))
  ([db-spec]
   {:datastore (jdbc/sql-database db-spec)
    :migrations (jdbc/load-resources "migrations")}))

(defn migrate
  ([]
   (migrate *db-spec*))
  ([db-spec]
   (repl/migrate (config db-spec))))

(defn rollback
  ([]
   (rollback *db-spec*))
  ([db-spec]
   (repl/rollback (config db-spec))))

(defn create-migration
  [table description]
  (let [format (DateTimeFormatter/ofPattern "yyyyMMddHHmm")
        today (.format (LocalDateTime/now) format)
        name (string/split description #"\s+")
        [up down] (map (fn [type]
                         (str today \_ table \_ (string/join \- name) \. type ".sql"))
                       ["up" "down"])]
    (spit (str "resources/migrations/" up) (str "-- " table ": " description))
    (spit (str "resources/migrations/" down) (str "-- " table ": " description))))
