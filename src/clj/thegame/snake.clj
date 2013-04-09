(ns thegame.snake)

;; these constants are given in cells
(def ^:const snake-size 5)
(def ^:const board-size 40)
(def ^:const directions [">" "<" "^" "v"])

(defn new-player
  "Find a place for the new snake on the board
  Return list of the form ([x1 y1] [x2 y2] ... [xn yn])"
  [world player]
  (let [safety 5 ;; don't start the snake with its face in the wall
        edge (- board-size snake-size safety)
        start-x (rand-int edge)
        start-y (rand-int edge)
        snake {:cells (map vector
                           (repeat snake-size start-x)
                           (range (+ start-y snake-size) start-y -1))
               :head [start-x start-y]
               :direction "^"}]
    (swap! world update-in [:snakes] assoc player snake)
    (println world)
    {:me snake}))

(defn delete-player
  "Delete a snake from the world"
  [world player]
  (swap! world update-in [:snakes] dissoc player))

(defn cell-forward
  "Move a cell one step forward"
  [[x y] direction]
  (case direction
    "^" (if (= y 0)
          [x (dec board-size)]
          [x (dec y)])))

(defn snake-forward
  "Move the snake forward one cell"
  [snake]
  {:cells (map #(cell-forward % (snake :direction)) (snake :cells))
   :head (cell-forward (snake :head) (snake :direction))
   :direction (snake :direction)})

(defn tick
  "Make one iteration in the world"
  [world]
  (println "tick: " world)
  (doseq [p (keys (@world :snakes))]
    (swap! world update-in [:snakes p] snake-forward))
  (println "after tick: " world))

(defn see-world
  "Get the world as it's seen by a player"
  [world player]
  (let [w {:me ((@world :snakes) player)
           :others (dissoc (@world :snakes) player)}]
    (println w)
    w))