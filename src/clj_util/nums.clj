(ns clj-util.nums)

(defn saturate-int
  "Saturate and int (like in signal processing).
  If i is not in range [lower, upper], saturates i to either lower or upper,
  depending on if i is less than lower, or larger than upper, respectively."
  [i [lower upper]]
  (-> i (min upper) (max lower)))

;; (defn pow
;;   [x e]
;;   (apply * (repeat e x)))

;; (defn square
;;   [x]
;;   (pow x 2))

;; (defn cube
;;   [x]
;;   (pow x 3))

;; (defn mean
;;   [nums]
;;   (float (/ (reduce + nums) (count nums))))

;; (defn std-dev
;;   ([nums]
;;    (let [m (mean nums)
;;          ms (mean (map square nums))
;;          n (count nums)]
;;      (std-dev m ms n)))
;;   ([mean mean-squares n]
;;    (Math/sqrt
;;      (/ (* n (- mean-squares (square mean))) (dec n)))))

;; (defn mode
;;   [nums]
;;   (let [sorted (sort nums)
;;         n (count nums)
;;         half-n (/ n 2)]
;;     (cond
;;       (even? n)
;;       (let [left (get nums (- half-n 1))
;;             right (get nums half-n)]
;;         (mean [left right]))
;;       (odd? n)
;;       (get nums (int half-n)))))

;; ; Some of these are computing the same thing multiple times
;; ; (std-dev also computes the mean, for example)
;; ; However, I profiled this against a different version which
;; ; computed all needed values just once, and somehow this is faster (???)
;; (defn stats
;;   [nums]
;;   {:count (count nums)
;;    :sum (reduce + nums)
;;    :mean (mean nums)
;;    :mode (mode nums)
;;    :std-dev (std-dev nums)})
