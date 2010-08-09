(ns bron-kerbosch.test
  (:use [bron-kerbosch] :reload-all)
  (:use [clojure.test]))

(defn =set
  "Tests if two collections, when interpreted as sets, are the same"
  [coll1 coll2]
  (= (set coll1) (set coll2)))

(deftest test-inverse-map
  (is (= {2 1, :bar :foo}
	 (inverse-map {1 2, :foo :bar})))
  (is (= {2 1} (inverse-map {1 2}))))

(deftest test-consume-sets
  (is (= [ [1 #{1 2 3} #{4 5}   ]
	   [2 #{2 3}   #{1 4 5} ]
	   [3 #{3}     #{1 2 4 5}] ]
	 (consume-sets (sorted-set 1 2 3) (sorted-set 4 5)))))

(deftest test-bk-feeder
  (is (= [[#{} #{1 2 3} #{}]
	  [#{1} #{2 3} #{}]
	  [#{1 2} #{} #{}]
	  [#{1 3} #{} #{}]
	  [#{2} #{} #{1}]
	  [#{3} #{} #{1}]]
	 (bk-feeder (sorted-set)
		    (sorted-set 1 2 3)
		    (sorted-set)
		    {1 #{2 3},
		     2 #{1},
		     3 #{1}}))))

(deftest test-bk-consumer
  (is (= [:r1 :r3]
	 (bk-consumer [[:r1 [] []] 
		       [:r2 [:foo] []] 
		       [:r3 [] []]
		       [:r4 [] [:bar]]]))))
  
(deftest test-bk
  (is (=set [#{:bar :baz} #{:bar :foo}]
	    (let [example-nodes #{:foo :bar :baz}
		  example-neighbours {:foo #{:bar}, 
				      :bar #{:foo :baz}, 
				      :baz #{:bar}}]
	      (bk example-nodes example-neighbours))))
  ; Wikipedia entry example
  (is (=set [#{1 2 5} #{2 3} #{3 4} #{4 5} #{4 6}]
	    (let [wikipedia-nodes [1 2 3 4 5 6]
		  wikipedia-neigh {1 [2 5]
				   2 [1 3 5]
				   3 [2 4]
				   4 [3 5 6]
				   5 [1 2 4]
				   6 [4]}]
	      (bk wikipedia-nodes wikipedia-neigh)))))

(deftest test-disjoint-sets
  (is (= [[#{3 4}] [] [#{1 2}]]
	 (let [sets [#{1 2} #{2 3} #{3 4}]]
	   [(disjoint-sets sets #{1 2}) 
	    (disjoint-sets sets #{2 3}) 
	    (disjoint-sets sets #{3 4})]))))

(deftest test-maximum-disjoint-sets
  (is (=set [[#{1 2} #{3 4}] [#{2 3}]]
	    (maximum-disjoint-sets [#{1 2} #{2 3} #{3 4}])))
  (is (=set [[#{1 2}] [#{2 3}]]
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

