(ns thegame.snake-test
  (:use clojure.test
        thegame.snake))

(deftest cell-forward-test
  (is (= (cell-forward {:x 0 :y 0} :down)
         {:x 0 :y 1})))

(deftest snake-forward-test
  (is (= (snake-forward {:cells '({:x 0 :y 1} {:x 1 :y 1} {:x 2 :y 1})
                         :head {:x 3 :y 1}
                         :direction :right})
         {:cells '({:x 1 :y 1} {:x 2 :y 1} {:x 3 :y 1})
          :head {:x 4 :y 1}
          :direction :right})))