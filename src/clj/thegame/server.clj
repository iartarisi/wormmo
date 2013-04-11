(ns thegame.server
  (:gen-class)
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.basic :as lb])
  (:use [flatland.protobuf.core :only [protobuf protobuf-dump protodef]]
        [thegame.websocket :only [create-websocket world]]
        [thegame.snake :only [tick]]))

(import Game$World)

(defn -main
  [& args]
  (let [conn (rmq/connect)
        channel (lch/open conn)
        exchange "games"
        World (protodef Game$World)]
    (le/declare channel exchange "fanout" :durable false :auto-delete true)
    (create-websocket channel)
    (while true
      (tick world)
      (lb/publish channel exchange "" (str (new java.util.Date))
                  :content-type "text/plain" :type "time")
      (lb/publish channel exchange "" (protobuf-dump (protobuf World @world))
                  :content-type "application/octet-stream" :type "world")
      (Thread/sleep 500))
    (rmq/close channel)
    (rmq/close conn)))