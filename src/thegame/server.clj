(ns thegame.server
  (:gen-class)
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb])
  (:use [thegame.serializer :only [serialize deserialize World Turn]]
        [thegame.websocket :only [create-websocket]]
        [thegame.snake :only [tick turn new-player delete-player]]))

(def world (atom {:snakes {}}))

(defn server-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (case type
    "new-player" (new-player world (Integer. (String. payload "UTF-8")))
    "player-quit" (delete-player world (Integer. (String. payload "UTF-8")))
    "turn" (let [{:keys [player direction]} (deserialize Turn payload)]
             (turn world player direction))))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        channel (lch/open conn)
        worldex "world"]

    (lq/declare channel "server" :exclusive false :auto-delete true)
    (.start (Thread.
             #(lc/subscribe channel "server" server-handler :auto-ack true)))

    (le/declare channel worldex "fanout" :durable false :auto-delete false)
    (create-websocket channel)
    (while true
      (tick world)
      (lb/publish channel worldex "" (str (new java.util.Date))
                  :content-type "text/plain" :type "time")
      (lb/publish channel worldex "" (serialize World @world)
                  :content-type "application/octet-stream" :type "world")
      (Thread/sleep 100))
    (rmq/close channel)
    (rmq/close conn)))