package slinky.readwrite

import scala.quoted._

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

trait ValueClass[T] {
  type Repr
  def from(t: T): Repr
  def to(r: Repr): T
}

object ValueClass {
  class Impl[T, R](_from: T => R, _to: R => T) extends ValueClass[T] {
    type Repr = R
    def from(t: T): R = _from(t)
    def to(r: R): T = _to(r)
  }

  transparent inline implicit def provide[T]: ValueClass[T] = ${makeValueClass[T]}

  def makeValueClass[T: Type](using Quotes): Expr[ValueClass[T]] = {
    val q = summon[Quotes]
    import q.reflect._
    val T = TypeRepr.of[T]
    if (!(T <:< TypeRepr.of[AnyVal])) {
      report.throwError(s"Not an AnyVal subtype: $T")
    }
    val cls = T.simplified.classSymbol.getOrElse(report.throwError(s"Could not find a matching class for type $T"))
    val param = cls.primaryConstructor.paramSymss match {
      case List(a) :: _ => a
      case _ => report.throwError(s"Unsupported primary constructor: $T")
    }
    val field = cls.declaredField(param.name) 

    val R = T.memberType(field).simplified.asType
    R match {
      case '[r0] => '{
        new ValueClass.Impl[T, r0](
          (t: T) => ${Select('{t}.asTerm, field).asExprOf[r0]},
          (r: r0) => ${
            Select.overloaded(New(TypeTree.of[T]), "<init>", Nil, List('{r}.asTerm))
            .asExprOf[T]
          },
        )
      }
    }
  }
}