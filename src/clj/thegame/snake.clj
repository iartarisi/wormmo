(ns thegame.snake)

;; these constants are given in cells
(def ^:const snake-size 5)
(def ^:const board-size 40)
(def ^:const directions [:up :down :left :right])

(defn new-player
  "Find a place for the new snake on the board"
  [world player]
  (let [safety (inc snake-size) ;; make sure we don't init out of bounds
        start-x (+ (rand-int (- board-size safety)) safety)
        start-y (+ (rand-int (- board-size safety)) safety)
        direction (rand-nth directions)
        snake {:cells
               (case direction
                 :up (map hash-map
                          (repeat :x) (repeat snake-size start-x)
                          (repeat :y) (range (inc start-y) (+ start-y snake-size 1)))
                 :right (map hash-map
                             (repeat :x) (range (- start-x snake-size) start-x)
                             (repeat :y) (repeat snake-size start-y))
                 :down (map hash-map
                            (repeat :x) (repeat snake-size start-x)
                            (repeat :y) (range (- start-y snake-size) start-y))
                 :left (map hash-map
                            (repeat :x) (range (inc start-x) (+ start-x snake-size 1))
                            (repeat :y) (repeat snake-size start-y)))
               :head {:x start-x :y start-y}
               :direction direction}]
    (swap! world update-in [:snakes] assoc player snake)))

(defn delete-player
  "Delete a snake from the world"
  [world player]
  (swap! world update-in [:snakes] dissoc player))

(defn cell-forward
  "Move a cell one step forward"
  [{:keys [x y]} direction]
  (let [edge (dec board-size)]
    (zipmap [:x :y]
            (case direction
              :up (if (= y 0)
                    [x edge]
                    [x (dec y)])
              :right (if (= x edge)
                       [0 y]
                       [(inc x) y])
              :down (if (= y edge)
                      [x 0]
                      [x (inc y)])
              :left (if (= x 0)
                      [edge y]
                      [(dec x) y])))))

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