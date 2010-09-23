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

(defn vector-intersection 
  "Finds the intersection between two vectors.
  Assumes no duplicate elements in either vector."
  [vec-1 vec-2]
  (apply vector (intersection (set vec-1) (set vec-2))))
  
; Bron-Kerbosch algorithm
(defn bk 
  "Performs the Bron-Kerbosch iterative algorithm."
  [r p x neighbours]
  (if (and (empty? p) (empty? x))
    [(set r)]
    (loop [p p, x x, result []]
      (if (empty? p) 
	result
	(let [v (first p)
	      nv (neighbours v)
	      result (into result 
			   (bk (cons v r)
			       (vector-intersection p nv) 
			       (vector-intersection x nv)
			       neighbours))
	      p (rest p)
	      x (cons v x)]
	  (recur p x result))))))

(defn bk-pivot
  "Performs the Bron-Kerbosch iterative algotihm
  using a pivot."
  [r p x neighbours]
  (if (and (empty? p) (empty? x))
    [(set r)]
    (let [u (first (concat p x))
	  p-without-u-neigh (vec (remove #(some (partial = %) (neighbours u)) p))
	  ]
      (loop [p p,
	     p-without-u-neigh p-without-u-neigh, 
	     x x, 
	     result []]
	(if (empty? p-without-u-neigh) 
	  result
	  (let [v (first p-without-u-neigh)
		nv (neighbours v)
		result (into result 
			     (bk (cons v r)
				 (vector-intersection p nv) 
				 (vector-intersection x nv)
				 neighbours))
		p (rest p)
		x (cons v x)]
	    (recur p (rest p-without-u-neigh) x result)))))))

(defn maximum-cliques
  "Yields all maximal individual cliques of the nodes
  given the neighbours function. nodes is a seq and 
  neighbours is a function that maps from a node to
  a seq of its neighbours."
  [nodes neighbours]
  (bk [] nodes [] neighbours))

(defn maximum-cliques-pivot
  [nodes neighbours]
  (bk-pivot [] nodes [] neighbours))

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
  "Yields the maximum cliques of the maximum cliques."
  [nodes neighbours]
  (let [cliques (maximum-cliques nodes neighbours)]
    (maximum-disjoint-sets cliques)))
