package slinky.readwrite

// Scala 3 unions are a pain. Unlike scala.js fake unions, real unions have property of
// A <:< (A | Nothing), and the compiler seems to pick that decomposition whenever you're doing
// something like an inline match. This particular "typeclass" will resolve to a refinement type,
// where Constituents is refined to a concrete type, e.g.:
//   NominalUnion[Int | String | Boolean)] { type Constituents = (Int, String, Boolean) }
// Note that there's no extra simplification done whatsoever, meaning `provide` can give you this:
//   NominalUnion[String | String] { type Constituents = (String, String) }
// even though (String | String) _is_ simply String
trait NominalUnion[T] {
  type Constituents <: Tuple
}

object NominalUnion {
  type Aux[T, C] = NominalUnion[T] { type Constituents = C }
  object Instance extends NominalUnion[Any] // Can't be private since it'll be inlined in user code
  transparent inline implicit def provide[T]: NominalUnion[T] = ${ExoticTypesMacro.makeNominalUnion[T]}
}