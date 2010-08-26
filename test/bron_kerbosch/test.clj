(ns bron-kerbosch.test
  (:use [bron-kerbosch] :reload-all)
  (:use [clojure.test])
  (:import [javax.vecmath Point3d]))

(defn- =set
  "Tests if two collections, when interpreted as sets, are the same"
  [coll1 coll2]
  (= (set coll1) (set coll2)))

(defn- =set-of-set
  "Tests if two collections, when interpreted as sets of sets, are the same"
  [coll1 coll2]
  (= (set (map set coll1))
     (set (map set coll2))))

(deftest test-inverse-map
  (is (= {2 1, :bar :foo}
	 (inverse-map {1 2, :foo :bar})))
  (is (= {2 1} (inverse-map {1 2}))))


(deftest test-vector-intersection
  (is (=set []      (vector-intersection [        ] [:c :d   ])))
  (is (=set []      (vector-intersection [:a :b :c] [        ])))
  (is (=set []      (vector-intersection [:a :b :c] [:d      ])))
  (is (=set [:b :c] (vector-intersection [:b :c   ] [:b :c   ])))
  (is (=set [:b :c] (vector-intersection [:a :b :c] [:b :c   ])))
  (is (=set [:b :c] (vector-intersection [:a :b :c] [:b :c :d])))
  (is (=set [:b :c] (vector-intersection [:a :b :c] [:d :b :c])))
  
  (let [a (new Object)
	b (new Object)
	c (new Object)
	d (new Object)]
    (is (=set []    (vector-intersection [     ] [c d  ])))
    (is (=set []    (vector-intersection [a b c] [     ])))
    (is (=set []    (vector-intersection [a b c] [d    ])))
    (is (=set [b c] (vector-intersection [b c  ] [b c  ])))
    (is (=set [b c] (vector-intersection [a b c] [b c  ])))
    (is (=set [b c] (vector-intersection [a b c] [b c d])))
    (is (=set [b c] (vector-intersection [a b c] [d b c]))))

  (let [a (new Point3d 0 0 0)
	b (new Point3d 1 1 1)
	c (new Point3d 2 2 2)
	d (new Point3d 3 3 3)]
    (is (=set [   ] (vector-intersection [     ] [c d  ])))
    (is (=set [   ] (vector-intersection [a b c] [     ])))
    (is (=set [   ] (vector-intersection [a b c] [d    ])))
    (is (=set [b c] (vector-intersection [b c  ] [b c  ])))
    (is (=set [b c] (vector-intersection [a b c] [b c  ])))
    (is (=set [b c] (vector-intersection [a b c] [b c d])))
    (is (=set [b c] (vector-intersection [a b c] [d b c])))))

(deftest test-maximum-cliques
  (is (=set [#{:bar :baz} #{:bar :foo}]
	    (let [example-nodes #{:foo :bar :baz}
		  example-neighbours {:foo #{:bar}, 
				      :bar #{:foo :baz}, 
				      :baz #{:bar}}]
	      (maximum-cliques example-nodes example-neighbours))))
  ; Wikipedia entry example
  (is (=set [#{1 2 5} #{2 3} #{3 4} #{4 5} #{4 6}]
	    (let [wikipedia-nodes [1 2 3 4 5 6]
		  wikipedia-neigh {1 [2 5]
				   2 [1 3 5]
				   3 [2 4]
				   4 [3 5 6]
				   5 [1 2 4]
				   6 [4]}]
	      (maximum-cliques wikipedia-nodes wikipedia-neigh)))))

(deftest test-maximum-cliques-on-uncomparable-items
  (let [a (new Object)
	b (new Object)
	c (new Object)
	d (new Object)
	nodes [a b c d]]
    (is (=set [#{a b c} #{c d}]
	      (maximum-cliques nodes {a [b c], b [a c], c [a b d], d [c]}))))
  (let [a (new Point3d 0 0 0)
	b (new Point3d 1 1 1)
	c (new Point3d 2 2 2)
	d (new Point3d 3 3 3)
	nodes [a b c d]]
    (is (=set [#{a b c} #{c d}]
	      (maximum-cliques nodes {a [b c], b [a c], c [a b d], d [c]})))))

(deftest test-disjoint-sets
  (is (= [[#{3 4}] [] [#{1 2}]]
	 (let [sets [#{1 2} #{2 3} #{3 4}]]
	   [(disjoint-sets sets #{1 2}) 
	    (disjoint-sets sets #{2 3}) 
	    (disjoint-sets sets #{3 4})]))))

(deftest test-maximum-disjoint-sets
  (is (=set-of-set [[#{1 2} #{3 4}] [#{2 3}]]
		   (maximum-disjoint-sets [#{1 2} #{2 3} #{3 4}])))
  (is (=set-of-set [[#{1 2}] [#{2 3}]]
		   (maximum-disjoint-sets [#{1 2} #{2 3}]))))

(deftest test-maximum-cliques-of-maximum-cliques
  (is (=set [[#{2 3} #{4 5}] [#{2 3} #{4 6}] [#{3 4} #{1 2 5}] [#{4 6} #{1 2 5}]]
	    (let [wikipedia-nodes [1 2 3 4 5 6]
		  wikipedia-neigh {1 [2 5]
				   2 [1 3 5]
				   3 [2 4]
				   4 [3 5 6]
				   5 [1 2 4]
				   6 [4]}]
	      (maximum-cliques-of-maximum-cliques wikipedia-nodes wikipedia-neigh))))
  (is (=set [[#{1 2}] [#{2 3}]]
	    (maximum-cliques-of-maximum-cliques [1 2 3] {1 [2], 2 [1 3], 3 [2]}))))