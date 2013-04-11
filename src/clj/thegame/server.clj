(ns thegame.server
  (:gen-class)
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.basic :as lb])
  (:use [thegame.serializer :only [serialize]]
        [thegame.websocket :only [create-websocket world]]
        [thegame.snake :only [tick]]))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        channel (lch/open conn)
        exchange "games"]
    (le/declare channel exchange "fanout" :durable false :auto-delete true)
    (create-websocket channel)
    (while true
      (tick world)
      (lb/publish channel exchange "" (str (new java.util.Date))
                  :content-type "text/plain" :type "time")
      (lb/publish channel exchange "" (serialize @world)
                  :content-type "application/octet-stream" :type "world")
      (Thread/sleep 500))
    (rmq/close channel)
    (rmq/close conn)))