# Clojure implementaion of the Bron-Kerbosch algorithm

A clojure implementation of the Bron-Kerbosch algorithm for finding maximum cliques.
More information about the algorithm can be found on its [wikipedia entry](http://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm).

## Installation

Available as a lein module from clojars.
Simply us

    [bron-kerbosch "1.0.0-SNAPSHOT"]

in you `project.clj` dependencies.
The jar filed is compiled using clojure 1.2 and clojure-contrib 1.2, but it should be possible to compile it under 1.1.

## Usage

The program needs a graph defined by a collection of nodes and a neighbor function which takes a node and returns a collection of nodes.
For example, rhe example graph from the Wikipedia entry on the Bron-Kerbosch algorithm which looks like this

![Example graph from Wikipedia](http://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/6n-graf.svg/240px-6n-graf.svg.png)

can be represented by the node vector `[1 2 3 4 5 6]` and map based neighbor function:

    {1 [2 5]
     2 [1 3 5]
     3 [2 4]
     4 [3 5 6]
     5 [1 2 4]
     6 [4]}

To run the Bron-Kerbosch algorithm call the function `maximum-cliques` on the nodes and the neighbor function

    user> (maximum-cliques [1 2 3 4 5 6]
                           {1 [2 5]
                            2 [1 3 5]
                            3 [2 4]
                            4 [3 5 6]
                            5 [1 2 4]
                            6 [4]})
    (#{1 2 5} #{2 3} #{3 4} #{4 5} #{4 6})

This will generate the maximum cliques in the graph.
To get the maximum cliques of the maximum cliques use the function `maximum-cliques-of-maximum-cliques`

    user> (maximum-cliques-of-maximum-cliques [1 2 3 4 5 6]
                                              {1 [2 5]
                                               2 [1 3 5]
                                               3 [2 4]
                                               4 [3 5 6]
                                               5 [1 2 4]
                                               6 [4]})
    ((#{2 3} #{4 5}) (#{2 3} #{4 6}) (#{3 4} #{1 2 5}) (#{4 6} #{1 2 5})) 

## To dos

* At the moment the Bron-Kerbosch algorithm is implemented without pivoting.