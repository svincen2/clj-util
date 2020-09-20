(ns clj-util.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [clojure.string :as str]
            [taoensso.timbre :as log])
  (:import [java.sql Timestamp]
           [java.time Instant]
           [java.util UUID]))

(defn ^:private prefixed-column
  [prefix column]
  (keyword (str (name prefix) \. (name column))))

(defn ^:private prefixed-columns
  [prefix columns]
  (map (partial prefixed-column prefix) columns))

(defn columns
  ([schema]
   (columns schema (:columns schema)))
  ([schema columns]
   (prefixed-columns (:table schema) columns)))

(defn aliased-columns
  ([schema]
   (aliased-columns schema (:columns schema)))
  ([schema columns]
   (prefixed-columns (:alias schema) columns)))

(defn column
  [schema column]
  (prefixed-column (:table schema) column))

(defn aliased-column
  [schema column]
  (prefixed-column (:alias schema) column))

(defn table
  [schema]
  (:table schema))

(defn aliased-table
  [schema]
  [(:table schema) (:alias schema)])

(defn ^:private clj->sql
  [x]
  (-> x
      (name)
      (str/replace #"-" "_")))

(defn ^:private sql->clj
  [x]
  (-> x
      (str/lower-case)
      (str/replace #"_" "-")
      (keyword)))

(defn ^:private prepare-insert
  [row]
  (let [created-at (Timestamp/from (Instant/now))]
    (merge {:id (UUID/randomUUID)
            :created-at created-at
            :updated-at created-at}
           (into {} (map (fn [[k v]]
                           {k (if (instance? Instant v)
                                (Timestamp/from v)
                                v)})
                         row)))))

(defn query
  [spec sqlmap]
  (let [sql (sql/format sqlmap)]
    (log/debug "query" sql)
    (->> (jdbc/query spec sql
                     {:identifiers sql->clj
                      :entities clj->sql}))))

(defn fetch!
  [spec table id]
  (log/debug "fetch!" table id)
  (-> (query spec {:select [:*]
                   :from [table]
                   :where [:= :id id]})
      (first)))

(defn insert!
  [spec table row]
  (log/debug "insert!" table row)
  (-> (jdbc/insert! spec table
                    (prepare-insert row)
                    {:identifiers sql->clj
                     :entities clj->sql})
      (first)))

(defn delete!
  [spec table where]
  (log/debug "delete!" table where)
  (let [sql (sql/format {:delete-from table
                         :where where})]
    (jdbc/execute! spec sql
                   {:identifiers sql->clj
                    :entities clj->sql})))
