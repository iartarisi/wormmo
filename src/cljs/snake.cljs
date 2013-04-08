(ns snake)

(def element-id "canvas")
(def ^:const cell-width 10) ;; pixels
(def ^:const snake-length 5) ;; cells

(def ^:const board {:size 400
                    :canvas (. js/document (getElementById element-id))
                    :context (. (. js/document (getElementById element-id))
                                (getContext "2d"))})

(defn draw-cell
  [board fill-color border-color [x y]]
  (doto (board :context)
    (#(set! (. % -fillStyle) fill-color))
    (.fillRect (* x cell-width) (* y cell-width) cell-width cell-width)
    (#(set! (. % -strokeStyle) border-color))
    (.strokeRect (* x cell-width) (* y cell-width) cell-width cell-width)))

(defn draw-snake
  [{:strs [cells head]}]
  (doseq [c cells]
    (draw-cell board "green" "white" c))
  (draw-cell board "white" "green" head))

(defn draw-board
  [board]
  (doto (board :context)
    (#(set! (. % -fillStyle) "white"))
    (.fillRect 0 0 (board :size) (board :size))
    (#(set! (. % -strokeStyle) "black"))
    (.strokeRect 0 0 (board :size) (board :size))))

(draw-board board)

(defn refresh
  [game-hash]
  (let [game (js->clj game-hash)]
    (draw-snake (game "me"))))