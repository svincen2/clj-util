(ns clj-util.date-range
  (:import [java.time LocalDate]))

(defn days
  "Returns all distinct LocalDate's in range [start, end)."
  [^LocalDate start ^LocalDate end]
  (take-while #(not= end %) (map #(.plusDays start %) (range))))

(defn days-of-year
  "Returns all distinct LocalDate's for the given year."
  [year]
  (let [start (LocalDate/of year 1 1)
        end (.plusYears start 1)]
    (days start end)))

(defn days-of-month
  "Returns all distinct LocalDate's for the given month."
  [year month]
  (let [start (LocalDate/of year month 1)
        end (.plusMonths start 1)]
    (days start end)))
