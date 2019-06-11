(ns clj-util.error)

(defmacro retry
  "Tries to execute body in a try/catch up to max-attempts number of times."
  [max-attempts & body]
  `(loop [attempts# 1]
     (let [result# (try ~@body (catch Exception e# e#))]
       (if-not (instance? Exception result#)
         result#
         (if (>= attempts# ~max-attempts)
           (throw result#)
           (recur (inc attempts#)))))))

(defn try-or-else
  "Try to evaluate f.
  If f throws, returns default."
  [f default]
  (try
    (f)
    (catch Exception e default)))
