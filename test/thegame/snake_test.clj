(ns thegame.snake-test
  (:use clojure.test
        thegame.snake))

(deftest cell-forward-test
  (is (= (cell-forward {:x 0 :y 0} :down)
         {:x 0 :y 1})))

(deftest snake-forward-straight-test
  (is (= (snake-forward {:cells '({:x 0 :y 1} {:x 1 :y 1} {:x 2 :y 1})
                         :head {:x 3 :y 1}
                         :direction :right
                         :turn :right})
         {:cells '({:x 1 :y 1} {:x 2 :y 1} {:x 3 :y 1})
          :head {:x 4 :y 1}
          :direction :right
          :turn :right})))

(deftest snake-forward-head-off
  (is (= (snake-forward {:cells '({:x 0 :y 1} {:x 1 :y 1} {:x 2 :y 1})
                         :head {:x 2 :y 2}
                         :direction :down
                         :turn :down})
         {:cells [{:x 1 :y 1} {:x 2 :y 1} {:x 2 :y 2}]
          :head {:x 2 :y 3}
          :direction :down
          :turn :down})))

(deftest snake-forward-cell-off
  (is (= (snake-forward {:cells '({:x 0 :y 1} {:x 1 :y 1} {:x 1 :y 2})
                         :head {:x 2 :y 2}
                         :direction :right
                         :turn :right})
         {:cells '({:x 1 :y 1} {:x 1 :y 2} {:x 2 :y 2})
          :head {:x 3 :y 2}
          :direction :right
          :turn :right})))

(deftest snake-forward-turn
  (is (= (snake-forward {:cells '({:x 0 :y 1} {:x 1 :y 1} {:x 2 :y 1})
                         :head {:x 2 :y 2}
                         :direction :down
                         :turn :right})
         {:cells '({:x 1 :y 1} {:x 2 :y 1} {:x 2 :y 2})
          :head {:x 3 :y 2}
          :direction :right
          :turn :right})))

(deftest new-player-test
  (let [world (atom {:snakes {}})
        player 42
        X 3
        Y (+ snake-size X 1)]
    (with-redefs [rand-int (fn [x] X)
                  rand-nth (fn [x] :right)]
      (new-player world player))
    (is (= @world
           {:snakes {player {:cells (vector {:y Y :x (+ X 1)}
                                            {:y Y :x (+ X 2)}
                                            {:y Y :x (+ X 3)}
                                            {:y Y :x (+ X 4)}
                                            {:y Y :x (+ X 5)})
                             :head {:x (+ X 6) :y Y}
                             :direction :right
                             :turn :right}}}))))

(deftest snake-collision-happens-test
  (let [world {:snakes {1 {:cells '({:x 0 :y 0} {:x 1 :y 1})}
                        2 {:cells '({:x 2 :y 1})}}}
        snake {:cells '({:x 3 :y 3})
               :head {:x 0 :y 0}
               :direction :right
               :turn :right}]
    (is (true? (snake-collision snake world)))))

(deftest snake-collision-no-collision-test
  (let [world {:snakes {1 {:cells '({:x 0 :y 0} {:x 1 :y 1})}
                        2 {:cells '({:x 2 :y 1})}}}
        snake {:cells '({:x 3 :y 3})
               :head {:x 0 :y 1}
               :direction :right
               :turn :right}]
    (is (false? (snake-collision snake world)))))

(deftest tick-test
  (let [world (atom {:snakes {1 {:cells '({:x 0 :y 0} {:x 0 :y 1})
                                 :direction :right
                                 :turn :right
                                 :head {:x 0 :y 2}}
                              2 {:cells '({:x 2 :y 1})
                                 :direction :right
                                 :turn :right
                                 :head {:x 2 :y 2}}}})]
    (tick world)
    (is (= @world
           {:snakes {1 {:cells '({:x 0 :y 1} {:x 0 :y 2})
                        :head {:x 1 :y 2}
                        :turn :right
                        :direction :right}
                     2 {:cells '({:x 2 :y 2})
                        :head {:x 3 :y 2}
                        :direction :right
                        :turn :right}}}))))

(deftest tick-snake-dies-test
  (let [world (atom {:snakes {1 {:cells '({:x 2 :y 2} {:x 3 :y 2})
                                 :direction :right
                                 :turn :right
                                 :head {:x 4 :y 2}}
                              2 {:cells '({:x 3 :y 0})
                                 :direction :dow
                                 :turn :down
                                 :head {:x 3 :y 1}}}})]
    (tick world)
    (is (= @world
           {:snakes {1 {:cells '({:x 3 :y 2} {:x 4 :y 2})
                        :head {:x 5 :y 2}
                        :turn :right
                        :direction :right}
                     }}))))