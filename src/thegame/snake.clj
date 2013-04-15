(ns thegame.snake)

;; these constants are given in cells
(def ^:const snake-size 5)
(def ^:const board-size 40)
(def ^:const directions [:up :down :left :right])

(defn build-snake
  "Create a new snake and return a list of cell hash-maps."
  [snake-size start-x start-y direction]
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
               (repeat :y) (repeat snake-size start-y))))

(defn new-player
  "Place a new snake at a random place on the board."
  [world player]
  (let [safety (inc snake-size) ;; make sure we don't init out of bounds
        start-x (+ (rand-int (- board-size safety)) safety)
        start-y (+ (rand-int (- board-size safety)) safety)
        direction (rand-nth directions)
        snake {:cells (build-snake snake-size start-x start-y direction)
               :head {:x start-x :y start-y}
               :direction direction
               :turn direction}]
    (swap! world update-in [:snakes] assoc player snake)
    (println (format "New player %s." player))))

(defn grow-food
  "Return the random location of a new food-pellet."
  []
  {:x (rand-int board-size)
   :y (rand-int board-size)})

(defn delete-player
  "Delete a snake from the world."
  [world player]
  (swap! world update-in [:snakes] dissoc player))

(defn eat-food
  "Make a food pellet disappear and replace it with a new one."
  [food cell]
  (conj (disj food cell) (grow-food)))

(defn cell-forward
  "Move a cell one step in the given direction."
  [{:keys [x y]} direction]
  (let [edge (dec board-size)]
    (zipmap [:x :y]
            (case direction
              :up (if (= y 0) [x edge] [x (dec y)])
              :right (if (= x edge) [0 y] [(inc x) y])
              :down (if (= y edge) [x 0] [x (inc y)])
              :left (if (= x 0) [edge y] [(dec x) y])))))

(defn snake-forward
  "Move the snake forward one cell. Return a food set and a snake hash."
  [food snake]
  (let [new-head (cell-forward (snake :head) (snake :turn))
        eat? (contains? food new-head)]
    [(if eat? (eat-food food new-head) food)
     {:cells (conj (vec (if eat?
                          (snake :cells)
                          (rest (snake :cells))))
                   (snake :head))
      :head new-head
      :direction (snake :turn)
      :turn (snake :turn)}]))

(defn collision?
  "True if the given snake crashes into something."
  [snake world]
  ;; TODO: head-on-head collision
  (let [all-cells (flatten (map :cells (vals (world :snakes))))]
    (boolean (some #{(snake :head)} all-cells))))

(defn tick
  "Make one iteration in the world

   Every snake moves forward one cell and the effects are triggered: eat
   food, replace eaten food, remove crashed snakes."
  [world]
  (let [[snakes food] (reduce (fn [[snakes food] [player snake]]
                                (let [[food snake] (snake-forward food snake)]
                                  [(conj snakes [player snake]) food]))
                              [{} (world :food)] (world :snakes))]
    {:snakes (into {} (remove (fn [[_ snake]]
                                  (collision? snake world)) snakes))
     :food food}))

(defn turn
  "Turn a snake in the given direction"
  [world player whence]
  (if (get-in @world [:snakes player])
    (swap! world assoc-in [:snakes player :turn] whence)))

(defn see-world
  "Get the world as it's seen by a player"
  [world player]
  {:me ((world :snakes) player)
   :others (dissoc (world :snakes) player)
   :food (world :food)})