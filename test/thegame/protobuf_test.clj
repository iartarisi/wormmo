(ns thegame.protobuf_test
  (:use clojure.test
        flatland.protobuf.core)
  (:import Game$World
           Game$World$Snake))

(def World (protodef Game$World))
(def Snake (protodef Game$World$Snake))

(defn proto=
  [class data]
  (= data
     (apply protobuf class (apply concat data))))

(deftest snake-minimum-test
  (is (proto= Snake {:key 1 :head {:x 0 :y 0}})))

(deftest snake-test
  (is (proto= Snake {:key 1
                     :head {:x 0 :y 0}
                     :cells '({:x 1 :y 0} {:x 2 :y 0})
                     :direction :up})))

(deftest world
  (is (proto= World {:snakes {10 {:key 10
                                  :head {:x 0 :y 0}
                                  :cells '({:x 0 :y 1} {:x 0 :y 2})
                                  :direction :up}
                              12 {:key 12
                                  :head {:x 3 :y 3}
                                  :cells '({:x 3 :y 4} {:x 3 :y 5})
                                  :direction :up}}})))