(ns bron-kerbosch
  (:use [clojure set pprint]))

; Utilities

(defn map-on-values
  "Applies f to the values in the map m."
  [f m] (apply merge (map (fn [[k v]] {k (f v)}) m)))

(defn map-from-fn
  "Generates a map from a function and its domain."
  [f domain]
  (apply merge (map (fn [x] {x (f x)}) domain)))

(defn inverse-map [m]
  (reduce (fn [result [k v]] (assoc result v k)) {} m))

; Bron-Kerbosch algorithm

(defn consume-sets 
  "Consumes the sets s1 and s2 by generating a sequence
suitable for the Bron-Kerbosch algorithm.
For example, (consume-sets #{1 2 3} #{4 5}) yields:
 ([1 [1 2 3] [4 5]], 
  [2 [2 3]   [1 4 5]],
  [3 [3]     [1 2 4 5]])"
  [s1 s2]
  (rest
   (take-while
    #(seq (second %))
    (iterate 
     (fn [[elm s1 s2]]
       (let [new-s1 (if (not= elm ::undef) (disj s1 elm) s1)
	     new-s2 (if (not= elm ::undef) (conj s2 elm) s2)
	     new-elm (first new-s1)]
	 [new-elm new-s1 new-s2]))
     [::undef s1 s2]))))

(defn bk-feeder
  "Feeds the [r p x] values encounted by the Bron-Kerbosch 
algorithm."
  [r p x neighbours]
  (cons
   [r p x]
   (apply 
    concat
    (for [[v p x] (consume-sets p x)]
      (bk-feeder 
       (conj r v) 
       (intersection p (neighbours v))
       (intersection x (neighbours v))
       neighbours)))))

(defn bk-consumer 
  "Consumes a stream of [r p x] vectors, filtering out those
r where p and x are empty."
  [feed]
  (map first
       (filter 
	(fn [[r p x]] (and (empty? p) (empty? x))) 
	feed)))

(defn bk [nodes neighbours]
  "Yields all maximal individual cliques of the nodes
given the neighbours function. nodes is a seq and 
neighbours is a function that maps from a node to
a seq of its neighbours."
  (let [r (sorted-set)
	p (apply sorted-set nodes)
	x (sorted-set)
	neighbours (map-on-values 
		    (partial apply sorted-set) 
		    neighbours)]
    (bk-consumer (bk-feeder r p x neighbours))))

(def maximum-cliques bk)

; Routines for extracting maximum cliques

(defn disjoint-sets
  "Given a seq of sets and a set returns all those sets 
with are disjoint from that set"
  [sets s]
  (filter #(empty? (intersection s %)) sets))

(defn maximum-disjoint-sets 
  "Extracts maximum disjoint sets.
 [#{1 2} #{2 3} #{3 4}] => ((#{1 2} #{3 4}) (#{2 3}))"
  [sets]
  (let [make-comparable (fn [s] (->> s (apply sorted-set) (apply vector)))
	comparable-sets (map-from-fn make-comparable sets)
	nodes (map comparable-sets sets)
	neighbours (map-from-fn
		    (fn [vector-node]
		      (map
		       comparable-sets
		       (disjoint-sets sets (set vector-node))))
		    nodes)
	comparable-result (maximum-cliques nodes neighbours)
	uncomparable-sets (inverse-map comparable-sets)
	result (map (partial map uncomparable-sets) comparable-result)]
    result))

(defn maximum-cliques-of-maximum-cliques
  [nodes neighbours]
  (let [cliques (maximum-cliques nodes neighbours)]
    (maximum-disjoint-sets cliques)))