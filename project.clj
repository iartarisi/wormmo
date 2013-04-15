(defproject thegame "0.1.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :description "massively multiplayer online browser snake"
  :url "http://github.com/mapleoin"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojurescript "0.0-1586"]
                 [io.netty/netty "3.5.4.Final"]
                 [org.webbitserver/webbit "0.4.14"]
                 [org.clojure/data.json  "0.2.2"]
                 [com.novemberain/langohr "1.0.0-beta12"]
                 [org.flatland/protobuf "0.7.1"]]
  :plugins [[lein-cljsbuild "0.2.7"]
            [lein-protobuf "0.3.1"]]
  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "resources/www/snake.js"
                           :optimizations :whitespace
                           :pretty-print true}}]}
  :main thegame.server)