(ns snake)

(def element-id "canvas")
(def ^:const cell-width 10) ;; pixels
(def ^:const snake-length 5) ;; cells

(def ^:const board {:size 400
                    :canvas (. js/document (getElementById element-id))
                    :context (. (. js/document (getElementById element-id))
                                (getContext "2d"))})

(defn draw-cell
  [board fill-color border-color {:strs [x y]}]
  (doto (board :context)
    (#(set! (. % -fillStyle) fill-color))
    (.fillRect (* x cell-width) (* y cell-width) cell-width cell-width)
    (#(set! (. % -strokeStyle) border-color))
    (.strokeRect (* x cell-width) (* y cell-width) cell-width cell-width)))

(defn draw-snake
  [{:strs [cells head]} color]
  (doseq [c cells]
    (draw-cell board color "white" c))
  (draw-cell board "white" color head))

(defn draw-board
  [board]
  (doto (board :context)
    (#(set! (. % -fillStyle) "white"))
    (.fillRect 0 0 (board :size) (board :size))
    (#(set! (. % -strokeStyle) "black"))
    (.strokeRect 0 0 (board :size) (board :size))))

(defn draw-food
  [cell]
  (draw-cell board "yellow" "gold" cell))

(defn refresh
  [game-hash]
  (draw-board board)
  (let [world (js->clj game-hash)]
    (draw-snake (world "me") "green")
    (doseq [s (vals (world "others"))]
      (draw-snake s "red"))
    (doseq [f (world "food")]
      (draw-food f))))
    ;; (map #(draw-snake % "red") (vals (world "others")))))
