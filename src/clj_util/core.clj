(ns clj-util.core)

(defmacro with-err-str
  "Evaluates exprs in a context in which *err* is bound to a fresh
  StringWriter.  Returns the string created by any nested printing
  calls.
  (note: copied from (source with-out-str) and modified to use *err*
  instead of *out*)"
  {:added "1.0"}
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))
