(ns thegame.core
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))

(defn -main []
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [c] (println "opened" c))
            (onClose [c] (println "closed" c))
            (onMessage [c j] (do
                               (println c j)
                               (.send c "{\"type\": \"upcase\",
                                          \"message\": \"caca\"}")))))
    (.add (StaticFileHandler. "."))
    (.start)))