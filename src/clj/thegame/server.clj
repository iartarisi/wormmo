(ns thegame.server
  (:gen-class)
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb])
  (:use [thegame.serializer :only [serialize]]
        [thegame.websocket :only [create-websocket]]
        [thegame.snake :only [tick new-player]]))

(def world (atom {:snakes {}}))

(defn server-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "Received: %s" (String. payload "UTF-8")))
  (case type
    "new-player" (new-player world (Integer. (String. payload "UTF-8")))))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        channel (lch/open conn)
        worldex "world"]

    (lq/declare channel "server" :exclusive false :auto-delete true)
    (.start (Thread.
             #(lc/subscribe channel "server" server-handler :auto-ack true)))

    (le/declare channel worldex "fanout" :durable false :auto-delete true)
    (create-websocket channel)
    (while true
      (tick world)
      (lb/publish channel worldex "" (str (new java.util.Date))
                  :content-type "text/plain" :type "time")
      (lb/publish channel worldex "" (serialize @world)
                  :content-type "application/octet-stream" :type "world")
      (Thread/sleep 500))
    (rmq/close channel)
    (rmq/close conn)))