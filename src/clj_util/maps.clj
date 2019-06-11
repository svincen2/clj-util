(ns clj-util.maps)

(defn update-all
  "Update all top-level keys of m with f."
  [m f]
  (persistent! (reduce (fn [new-m [k v]]
                         (assoc! new-m k (f v)))
                       (transient {})
                       m)))

(defn smart-update
  "Like update, but can handle arrays as well.
  If the value at any point in path is an array, will
  map over the array, continuing the path.
  Example: (smart-update {:a [{:b [{:c 10} {:c 20} {:d 100}]}]} [:a :b :c] inc)
  would result in {:a [{:b [{:c 11} {:c 21} {:d 100}]}]}."
  [m path f]
  (let [fp (first path)
        rp (rest path)]
    (if-not (empty? rp)
      (cond
        (seq? (fp m)) (update m fp #(map (fn [_] (smart-update _ rp f)) %))
        (vector? (fp m)) (update m fp #(mapv (fn [_] (smart-update _ rp f)) %))
        :else (update m fp #(smart-update % rp f)))
      (cond
        (seq? (fp m)) (update m fp #(map f %))
        (vector? (fp m)) (update m fp #(mapv f %))
        :else (update m fp f)))))

(defn rename-key
  [m old new]
  (if-let [v (old m)]
    (-> m
        (assoc new v)
        (dissoc old))
    m))

(defn assoc-if
  [m p k v]
  (if p (assoc m k v) m))

(defn update-existing
  [m k f & args]
  (if (get m k) (apply update m k f args) m))

(defn copy-assoc
  [m k vs]
  "Expects a map (m) and a vec of [key (k) values (vs)], where values is a seq.
  Returns a seq of copies of m, with one of the values associated to k"
  (map (partial assoc m k) vs))
