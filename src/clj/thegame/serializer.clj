(ns thegame.serializer
  (:use [flatland.protobuf.core]))

(import Game$World)
(def World (protodef Game$World))

(defn serialize
  [world]
  (protobuf-dump (protobuf World world)))

(defn deserialize
  [world]
  (protobuf-load World world))