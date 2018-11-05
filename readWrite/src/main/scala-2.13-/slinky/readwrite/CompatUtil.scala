package slinky.readwrite

object CompatUtil {
  // originally in scala-collection-compat
  type Factory[-A, +C] = scala.collection.generic.CanBuildFrom[Nothing, A, C]

  implicit class FactoryOps[-A, +C](private val factory: Factory[A, C]) {
      /**
      * @return A collection of type `C` containing the same elements
      *         as the source collection `it`.
      * @param it Source collection
      */
     def fromSpecific(it: TraversableOnce[A]): C = (factory() ++= it).result()
  }
}
