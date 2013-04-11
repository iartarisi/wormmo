(ns thegame.websocket
  (:require [clojure.data.json :as json]
            [langohr.queue :as lq]
            [langohr.consumers :as lc])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler])
  (:use [thegame.serializer :only [deserialize]]
        [thegame.snake]))


(defn queue-name [n] (format "player.%s" n))

(def world (atom {:snakes {}}))

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
  (new-player world @player)
  (.send ws-ch (json/write-str {:type "refresh"
                                :game (see-world @world @player)}))
  (let [qname (queue-name @player)
        handler (create-handler ws-ch @player)]
    (lq/declare rch qname :exclusive false :auto-delete true)
    (lq/bind rch qname topic-name)
    (.start (Thread. #(lc/subscribe rch qname handler :auto-ack true)))))

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
  ;; TODO this should be a call via rabbitmq to the server
  ;; (delete-player world @client-count)
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