(ns clj-util.coll)

(defn distinct-by
  "Returns a lazy sequence of the elements of coll with duplicates removed.
  Two elements in coll are duplicates if (= (f element1) (f element2)).
  Returns a stateful transducer when no collection is provided.
  NOTE: This was taken from (source distinct) and modified"
  ([f]
   (fn [rf]
     (let [seen (volatile! #{})]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (if (contains? @seen (f input))
            result
            (do (vswap! seen conj (f input))
                (rf result input))))))))
  ([f coll]
   (let [step (fn step [xs seen]
                (lazy-seq
                 ((fn [[x :as xs] seen]
                    (when-let [s (seq xs)]
                      (if (contains? seen (f x))
                        (recur (rest s) seen)
                        (cons x (step (rest s) (conj seen (f x)))))))
                  xs seen)))]
     (step coll #{}))))
