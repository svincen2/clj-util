(ns clj-util.debug)

(defmacro print-var
  "Prints x's symbol followed by its value.
  Example:
  Given a symbol 'some-var with the value 100,
  (print-var some-var) prints \"some-var 100\",
  followed by a newline.
  Useful for development / debugging."
  [x]
  `(println (quote ~x) ~x))

(defmacro pprint-var
  "Same as print-var, but uses pprint."
  [x]
  `(do
     (println (quote ~x))
     (clojure.pprint/pprint ~x)))

(defmacro time-ms
  "Like Clojure's time fn, but instead of printing,
  returns a map of :time, :result."
  [expr]
  `(let [t0# (System/nanoTime)
         res# ~expr]
     {:time (/ (double (- (System/nanoTime) t0#)) 1000000.0)
      :result res#}))
