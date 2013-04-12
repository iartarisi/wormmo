# wormmo

Massively Multiplayer Online Snake game.

## How it works

WebSockets, RabbitMQ, protobuf

## Installation

First make sure you have [leiningen](https://github.com/technomancy/leiningen) Version 2 (requires java) and RabbitMQ installed and running.

Then do:

```bash
git clone git://github.com/mapleoin/wormmo.git
cd wormmo
lein cljsbuild once
lein run
```

Wait a little until leiningen downloads and installs all the dependencies. Then point your browser and your friends' browsers to `http://localhost:8080` and play.


## Bugs

One of the dependencies (netty) currently has trouble with Chromium. The project has only been tested on Firefox.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
