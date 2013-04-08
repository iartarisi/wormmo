(ns snake)

(def element-id "canvas")
(def ^:const cell-width 10) ;; pixels
(def ^:const snake-length 5) ;; cells

(def ^:const board {:size 400
                    :canvas (. js/document (getElementById element-id))
                    :context (. (. js/document (getElementById element-id))
                                (getContext "2d"))})

(defn create-snake
  []
  (let [start (Math/round (/ (* (Math/random) (- (board :size) cell-width))
                             cell-width))]))

(defn draw-snake
  [cells]
  (doseq [[x y] cells]
    (draw-cell board "green" x y)))

(defn draw-cell
  [board color x y]
  (doto (board :context)
    (#(set! (. % -fillStyle) color))
    (.fillRect (* x cell-width) (* y cell-width) cell-width cell-width)
    (#(set! (. % -strokeStyle) "white"))
    (.strokeRect (* x cell-width) (* y cell-width) cell-width cell-width)))

(defn draw-board
  [board]
  (doto (board :context)
    (#(set! (. % -fillStyle) "white"))
    (.fillRect 0 0 (board :size) (board :size))
    (#(set! (. % -strokeStyle) "black"))
    (.strokeRect 0 0 (board :size) (board :size))))

(draw-board board)
(draw-cell board "red" 3 4)
(draw-cell board "red" 3 5)
(draw-cell board "red" 4 5)
(draw-cell board "red" 4 6)
(draw-cell board "red" 4 7)
(draw-cell board "red" 4 8)
(draw-cell board "red" 4 9)

(defn refresh
  [game-hash]
  (let [game (js->clj game-hash)]
    (draw-snake (game "me"))))