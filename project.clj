(defproject thegame "0.1.0"
  :description "generic server for simple mmo games"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojurescript "0.0-1586"]
                 [org.webbitserver/webbit "0.4.14"]
                 [org.clojure/data.json  "0.2.2"]
                 [com.novemberain/langohr "1.0.0-beta12"]]
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.2.7"]]
  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "snake.js"
                           :optimizations :whitespace
                           :pretty-print true}}]}
  :main thegame.core)