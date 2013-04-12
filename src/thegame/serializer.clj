(ns thegame.serializer
  (:use [flatland.protobuf.core]))

(import Game$World)
(import Game$Turn)

(def World (protodef Game$World))
(def Turn (protodef Game$Turn))

(defn serialize
  [type data]
  (protobuf-dump (protobuf type data)))

(defn deserialize
  [type data]
  (protobuf-load type data))