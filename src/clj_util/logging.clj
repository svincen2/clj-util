(ns clj-util.logging
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]
            [clojure.term.colors :as colors])
  (:import [java.time LocalDateTime]
           ;; TODO - Can we *not* do this??
           [java.util Locale TimeZone]))

(defn ^:private level->str
  [level]
  (let [color-fn (comp colors/bold
                       colors/grey
                       (or ({:trace colors/on-blue
                             :debug colors/on-cyan
                             :info colors/on-green
                             :warn colors/on-yellow
                             :error colors/on-magenta
                             :fatal colors/on-red}
                            level)
                           colors/on-white))]
    (color-fn (str/upper-case (name level)))))

(defn ^:private output-fn
  [data]
  (let [{:keys [timestamp_ level error-level? ?ns-str ?line ?err msg_]} data]
    (format "%s [%s] [%s]: %s"
            (force timestamp_)
            (level->str level)
            ?ns-str
            (force msg_))))

(defn ^:private server-appender
  [server-ns]
  (let [now (LocalDateTime/now)
        server-logs-dir "jetty-server/logs"
        server-log-file (str server-ns "/" (.toLocalDate now) "/" (.toLocalTime now) ".log")
        logfile (str server-logs-dir "/" server-log-file)]
    (io/make-parents logfile)
    {:enabled? true
     :async? true
     :min-level :info
     :ns-whitelist ["org.eclipse.jetty.*"]
     :output-fn :inherit
     :fn (fn [data]
           (let [{:keys [output_]} data]
             (spit logfile
                   (str (force output_) \newline)
                   :append true)))}))

(defn ^:private service-appender
  []
  {:enabled? true
   :async? false
   :min-level :debug
   :ns-blacklist ["org.eclipse.jetty.*"]
   :output-fn :inherit
   :fn (fn [data]
         (let [{:keys [output_]} data]
           (println (force output_))))})

(defn init-cli!
  []
  (let [config {:level :debug
                :ns-whitelist []
                :ns-blacklist []
                :middleware []
                :timestamp-opts {:pattern "yyyy-MM-dd HH:mm:ss.SSS"
                                 :locale (Locale/getDefault)
                                 :timezone (TimeZone/getTimeZone "UTC")}
                :output-fn output-fn
                :appenders {:service (service-appender)}}]
    (timbre/set-config! config)))

(defn init-web!
  [server-ns]
  (let [config {:level :debug
                :ns-whitelist []
                :ns-blacklist []
                :middleware []
                :timestamp-opts {:pattern "yyyy-MM-dd HH:mm:ss.SSS"
                                 :locale (Locale/getDefault)
                                 :timezone (TimeZone/getTimeZone "UTC")}
                :output-fn output-fn
                :appenders {:service (service-appender)
                            :server (server-appender server-ns)}}]
    (timbre/set-config! config)))
