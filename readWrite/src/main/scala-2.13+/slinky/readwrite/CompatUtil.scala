package slinky.readwrite

object CompatUtil {
  type Factory[-A, +C] = scala.collection.Factory[A, C]
}
