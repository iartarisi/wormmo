# wormmo

[![Build Status](https://travis-ci.org/mapleoin/wormmo.png)](https://travis-ci.org/mapleoin/wormmo)

Massively Multiplayer Online Snake game.

## How it works

Communication between the client and the server is done via WebSocket. WebSocket connections talk to the backend server via RabbitMQ + google protocol buffers. The backend servers does all the computation, the browser just paints the world and sends key events. The web frontend is a mix of javascript and clojurescript (compiled to javascript).


## What it looks like

![Screenshot](http://i.imgur.com/kNnp4WQ.png)


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
