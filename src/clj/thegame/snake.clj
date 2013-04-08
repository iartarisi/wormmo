(ns thegame.snake)

;; these constants are given in cells
(def ^:const snake-size 5)
(def ^:const board-size 40)

(defn new-player
  "Find a place for the new snake on the board
  Return list of the form ([x1 y1] [x2 y2] ... [xn yn])"
  []
  (let [safety 5 ;; don't start the snake with its face in the wall
        edge (- board-size snake-size safety)
        start-x (rand-int edge)
        start-y (rand-int edge)]
    {:me {:cells (map vector
                      (repeat snake-size start-x)
                      (range start-y (+ start-y snake-size)))
          :head [start-x start-y]
          }}))