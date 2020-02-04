package slinky.readwrite

import scala.annotation.compileTimeOnly
import CompatUtil._
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.|
import scala.concurrent.ExecutionContext.Implicits.global

import scala.reflect.ClassTag
import scala.reflect.macros.whitebox

@compileTimeOnly("Deferred readers are used to handle recursive structures")
final class DeferredReader[T, Term] extends Reader[T] {
  override protected def forceRead(o: js.Object): T = throw new Exception
}

trait FallbackReaders {
  def fallback[T]: Reader[T] = v => {
    if (js.isUndefined(v.asInstanceOf[js.Dynamic].__)) {
      throw new IllegalArgumentException("Tried to read opaque Scala.js type that was not written by opaque writer")
    } else {
      v.asInstanceOf[js.Dynamic].__.asInstanceOf[T]
    }
  }
}

trait MacroReaders {
  implicit def deriveReader[T]: Reader[T] = macro MacroReadersImpl.derive[T]
}

class MacroReadersImpl(_c: whitebox.Context) extends GenericDeriveImpl(_c) {
  import c.universe._

  val typeclassType: c.universe.Type = typeOf[Reader[_]]

  def deferredInstance(forType: c.universe.Type, constantType: c.universe.Type) =
    q"new _root_.slinky.readwrite.DeferredReader[$forType, $constantType]"

  def maybeExtractDeferred(tree: c.Tree): Option[c.Tree] =
    tree match {
      case q"new _root_.slinky.readwrite.DeferredReader[$_, $t]()" =>
        Some(t)
      case q"new slinky.readwrite.DeferredReader[$_, $t]()" =>
        Some(t)
      case _ => None
    }

  def createModuleTypeclass(tpe: c.universe.Type, moduleReference: c.Tree): c.Tree =
    q"""new _root_.slinky.readwrite.Reader[$tpe] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $tpe = {
            $moduleReference
          }
        }"""

  def createCaseClassTypeclass(clazz: c.Type, params: Seq[Seq[Param]]): c.Tree = {
    val paramsTrees = params.map(_.map { p =>
      p.transformIfVarArg {
        p.default.map { d =>
          q"if (_root_.scala.scalajs.js.isUndefined(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName})) $d else ${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
        }.getOrElse {
          q"${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
        }
      }
    })

    q"""new _root_.slinky.readwrite.Reader[$clazz] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
            new $clazz(...$paramsTrees)
          }
        }"""
  }

  def createValueClassTypeclass(clazz: c.Type, param: Param): c.Tree =
    q"""new _root_.slinky.readwrite.Reader[$clazz] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
            new $clazz(${getTypeclass(param.tpe)}.read(o))
          }
        }"""

  def createSealedTraitTypeclass(traitType: c.Type, subclasses: Seq[c.Symbol]): c.Tree = {
    val cases = subclasses.map { sub =>
      cq"""${sub.name.toString} => ${getTypeclass(sub.asType.toType)}.read(o)"""
    }

    q"""new _root_.slinky.readwrite.Reader[$traitType] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $traitType = {
            o.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type.asInstanceOf[_root_.java.lang.String] match {
              case ..$cases
              case _ => _root_.slinky.readwrite.Reader.fallback[$traitType].read(o)
            }
          }
        }"""
  }

  def createFallback(forType: c.Type) = q"_root_.slinky.readwrite.Reader.fallback[$forType]"
}

trait CoreReaders extends MacroReaders with FallbackReaders {
  implicit def jsAnyReader[T <: js.Any]: Reader[T] = _.asInstanceOf[T]

  implicit val unitReader: Reader[Unit] = _ => ()

  implicit val stringReader: Reader[String] = v => {
    if (js.typeOf(v) == "string") {
      v.asInstanceOf[String]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a string")
    }
  }

  implicit val charReader: Reader[Char] = v => {
    if (js.typeOf(v) == "string") {
      v.asInstanceOf[String].head
    } else {
      throw new IllegalArgumentException(s"The value $v is not a string (trying to get a char)")
    }
  }

  implicit val byteReader: Reader[Byte] = v => {
    if (js.typeOf(v) == "number") {
      v.asInstanceOf[Byte]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a number")
    }
  }

  implicit val shortReader: Reader[Short] = v => {
    if (js.typeOf(v) == "number") {
      v.asInstanceOf[Short]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a number")
    }
  }

  implicit val intReader: Reader[Int] = v => {
    if (js.typeOf(v) == "number") {
      v.asInstanceOf[Int]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a number")
    }
  }

  implicit val longReader: Reader[Long] = v => {
    if (js.typeOf(v) == "string") {
      v.asInstanceOf[String].toLong
    } else {
      throw new IllegalArgumentException(s"The value $v is not a string (trying to get a long)")
    }
  }

  implicit val booleanReader: Reader[Boolean] = v => {
    if (js.typeOf(v) == "boolean") {
      v.asInstanceOf[Boolean]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a boolean")
    }
  }

  implicit val doubleReader: Reader[Double] = v => {
    if (js.typeOf(v) == "number") {
      v.asInstanceOf[Double]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a number")
    }
  }

  implicit val floatReader: Reader[Float] = v => {
    if (js.typeOf(v) == "number") {
      v.asInstanceOf[Float]
    } else {
      throw new IllegalArgumentException(s"The value $v is not a number")
    }
  }

  implicit def undefOrReader[T](implicit reader: Reader[T]): Reader[js.UndefOr[T]] = s => {
    if (js.isUndefined(s)) {
      js.undefined
    } else {
      reader.read(s)
    }
  }

  implicit def unionReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[A | B] = s => {
    try {
      aReader.read(s)
    } catch {
      case _: Throwable => bReader.read(s)
    }
  }

  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] =
    (s => {
      if (js.isUndefined(s) || s == null) {
        None
      } else {
        Some(reader.read(s))
      }
    }): AlwaysReadReader[Option[T]]

  implicit def eitherReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[Either[A, B]] = o => {
    if (o.asInstanceOf[js.Dynamic].isLeft.asInstanceOf[Boolean]) {
      Left(aReader.read(o.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]))
    } else {
      Right(bReader.read(o.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]))
    }
  }

  implicit def collectionReader[T, C[A] <: Iterable[A]](
    implicit reader: Reader[T],
    bf: Factory[T, C[T]]
  ): Reader[C[T]] =
    c => bf.fromSpecific(c.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)))

  implicit def arrayReader[T](implicit reader: Reader[T], classTag: ClassTag[T]): Reader[Array[T]] = { c =>
    c.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)).toArray
  }

  implicit def mapReader[A, B](implicit abReader: Reader[(A, B)]): Reader[Map[A, B]] = o => {
    collectionReader[(A, B), Iterable].read(o).toMap
  }

  implicit val rangeReader: Reader[Range] = o => {
    val dyn = o.asInstanceOf[js.Dynamic]
    if (dyn.inclusive.asInstanceOf[Boolean]) {
      dyn.start.asInstanceOf[Int] to dyn.end.asInstanceOf[Int] by dyn.step.asInstanceOf[Int]
    } else {
      dyn.start.asInstanceOf[Int] until dyn.end.asInstanceOf[Int] by dyn.step.asInstanceOf[Int]
    }
  }

  implicit val inclusiveRangeReader: Reader[Range.Inclusive] = rangeReader.asInstanceOf[Reader[Range.Inclusive]]

  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] =
    _.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
      oReader.read(v)
    }
}
