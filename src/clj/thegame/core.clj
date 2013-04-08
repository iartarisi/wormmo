(ns thegame.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.exchange :as le]
            [langohr.consumers :as lc]
            [langohr.basic :as lb])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:use [thegame.snake]))

(def consumer-count 0)

(defn start-consumer
  "Starts a consumer bound to the given topic exchange in a separate thread"
  [ws-ch rch topic-name client-count]
  (defn handler
    [rch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
    (.send ws-ch (json/write-str {:type "upcase"
                                  :message (format "Player %s Time %s" @client-count
                                                   (String. payload "UTF-8"))})))
  (.send ws-ch (json/write-str {:type "refresh"
                                :game (new-snake)}))
  (let [queue-name (format "consumer.%s" @client-count)]
    (lq/declare rch queue-name :exclusive false :auto-delete true)
    (lq/bind rch queue-name topic-name)
    (.start (Thread. #(lc/subscribe rch queue-name handler :auto-ack true)))))

(defn ws-on_message
  [channel message client-count]
  (do
    (println channel message)
    (.send channel (format "{\"type\": \"upcase\",
                             \"message\": \"caca %s\"}" @client-count))))

(defn ws-on_open
  [channel client-count rchan]
  (swap! client-count inc)
  (start-consumer channel rchan "games" client-count)
  (println "opened" channel client-count))

(defn ws-on_close
  [channel client-count]
  (println "closed" channel client-count)
  (swap! client-count dec))

(defn create-websocket
  "rchan - a rabbitmq channel to publish to"
  [rchan]
  (def client-count (atom 0))
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [c] (ws-on_open c client-count rchan))
            (onClose [c] (ws-on_close c client-count))
            (onMessage [c j] (ws-on_message c j client-count))))
    (.add (StaticFileHandler. "."))
    (.start)))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        channel (lch/open conn)
        exchange "games"]
    (le/declare channel exchange "fanout" :durable false :auto-delete true)
    (create-websocket channel)
    (while true
      (lb/publish channel exchange "" (str (new java.util.Date))
                  :content-type "text/plain" :type "hello")
      (Thread/sleep 2000))
    (rmq/close channel)
    (rmq/close conn)))
