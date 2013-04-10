(ns thegame.protobuf_test
  (:use clojure.test
        flatland.protobuf.core)
  (:import Game$World
           Game$World$Snake
           Game$World$SnakeAttr))

(def World (protodef Game$World))
(def Snake (protodef Game$World$Snake))
(def SnakeAttr (protodef Game$World$SnakeAttr))

(defn proto=
  [class data]
  (= data
     (apply protobuf class (apply concat data))))

(deftest snake-minimum-test
  (is (proto= Snake {:head {:x 0 :y 0}})))

(deftest snake-test
  (is (proto= Snake {:head {:x 0 :y 0}
                     :cells '({:x 1 :y 0} {:x 2 :y 0})
                     :direaction :up})))

(deftest snake-attr-test
  (is (proto= SnakeAttr {:key 30 :value {:head {:x 0 :y 0}}})))

(deftest world
  (is (proto= World {:snakes {30 {:head {:x 0 :y 0}}}})))