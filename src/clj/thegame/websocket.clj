(ns thegame.websocket
  (:require [clojure.data.json :as json]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:use [thegame.serializer :only [deserialize]]
        [thegame.snake :only [see-world]]))


(defn queue-name [n] (format "player.%s" n))

(defn create-handler
  [ws-ch player]
  (fn
    [rch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
    (case type
      "time" (.send ws-ch (json/write-str
                           {:type "upcase"
                            :message (format "Player %s Time %s" player
                                             (String. payload "UTF-8"))}))
      "world" (let [world (deserialize payload)]
                (.send ws-ch (json/write-str
                              {:type "refresh"
                               :game (see-world world player)}))))))

(defn start-consumer
  "Starts a consumer bound to the given topic exchange in a separate thread"
  [ws-ch rch topic-name player]
  (let [qname (queue-name player)
        handler (create-handler ws-ch player)]
    (lq/declare rch qname :exclusive false :auto-delete true)
    (lq/bind rch qname topic-name)
    (.start (Thread. #(lc/subscribe rch qname handler :auto-ack true)))))


(defn conn-id [conn] (System/identityHashCode conn))

(defn ws-on_message
  [conn message]
  (let [mess (json/read-str message)]
    (case (mess "type")
      "turn" (println (mess "data")))))

(defn ws-on_open
  [conn rchan]
  (let [cid (conn-id conn)]
    (lb/publish rchan "" "server" (str cid)
                :content-type "text/plain" :type "new-player")
    (start-consumer conn rchan "world" cid)
    (println "opened" conn cid)))

(defn ws-on_close
  [conn rchan]
  (let [cid (conn-id conn)]
    (lq/delete rchan (queue-name cid))
    (lb/publish rchan "" "server" (str cid)
                :content-type "text/plain" :type "player-quit")
    (println "closed" conn cid)))

(defn create-websocket
  "rchan - a rabbitmq channel to publish to"
  [rchan]
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [c] (ws-on_open c rchan))
            (onClose [c] (ws-on_close c rchan))
            (onMessage [c j] (ws-on_message c j))))
    (.add (StaticFileHandler. "."))
    (.start)))
