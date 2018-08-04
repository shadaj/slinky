package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.collection.generic.CanBuildFrom
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.scalajs.js.|
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.macros.whitebox

import scala.language.experimental.macros
import scala.language.higherKinds

@compileTimeOnly("Deferred writers are used to handle recursive structures")
final class DeferredWriter[T, Term] extends Writer[T] {
  override def write(p: T): js.Object = null
}

trait FallbackWriters {
  def fallback[T]: Writer[T] = s => js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
}

trait MacroWriters {
  implicit def deriveWriter[T]: Writer[T] = macro MacroWritersImpl.derive[T]
}

class MacroWritersImpl(_c: whitebox.Context) extends GenericDeriveImpl(_c) {
  import c.universe._

  val typeclassType: c.universe.Type = typeOf[Writer[_]]

  def deferredInstance(forType: Type, constantType: Type) =
    q"new _root_.slinky.readwrite.DeferredWriter[$forType, $constantType]"

  def maybeExtractDeferred(tree: Tree): Option[Tree] = {
    tree match {
      case q"new _root_.slinky.readwrite.DeferredWriter[$_, $t]()" =>
        Some(t)
      case q"new slinky.readwrite.DeferredWriter[$_, $t]()" =>
        Some(t)
      case _ => None
    }
  }

  def createModuleTypeclass(tpe: Type, moduleReference: Tree): Tree = {
    q"""new _root_.slinky.readwrite.Writer[$tpe] {
          def write(v: $tpe): _root_.scala.scalajs.js.Object = {
            _root_.scala.scalajs.js.Dynamic.literal()
          }
        }"""
  }

  def createCaseClassTypeclass(clazz: Type, params: Seq[Seq[Param]]): Tree = {
    val paramsTrees = params.flatMap(_.map { p =>
      q"""{
         val writtenParam = ${getTypeclass(p.tpe)}.write(v.${p.name.toTermName})
         if (!_root_.scala.scalajs.js.isUndefined(writtenParam)) {
           ret.${TermName(p.name.encodedName.toString)} = writtenParam
         }
       }"""
    })

    q"""new _root_.slinky.readwrite.Writer[$clazz] {
          def write(v: $clazz): _root_.scala.scalajs.js.Object = {
            val ret = _root_.scala.scalajs.js.Dynamic.literal()
            ..$paramsTrees
            ret
          }
        }"""
  }

  def createValueClassTypeclass(clazz: Type, param: Param): Tree = {
    q"""new _root_.slinky.readwrite.Writer[$clazz] {
          def write(v: $clazz): _root_.scala.scalajs.js.Object = {
            ${getTypeclass(param.tpe)}.write(v.${param.name.toTermName})
          }
        }"""
  }

  def createSealedTraitTypeclass(traitType: Type, subclasses: Seq[Symbol]): Tree = {
    val cases = subclasses.map { sub =>
      cq"""(value: $sub) =>
             val ret = ${getTypeclass(sub.asType.toType)}.write(value)
             ret.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type = ${sub.name.toString}
             ret"""
    }

    q"""new _root_.slinky.readwrite.Writer[$traitType] {
          def write(v: $traitType): _root_.scala.scalajs.js.Object = {
            v match {
              case ..$cases
              case _ => _root_.slinky.readwrite.Writer.fallback[$traitType].write(v)
            }
          }
        }"""
  }

  def createFallback(forType: Type) = q"_root_.slinky.readwrite.Writer.fallback[$forType]"
}

trait CoreWriters extends MacroWriters with FallbackWriters {
  implicit def jsAnyWriter[T <: js.Any]: Writer[T] = _.asInstanceOf[js.Object]

  implicit val unitWriter: Writer[Unit] = _ => js.Dynamic.literal()

  implicit val stringWriter: Writer[String] = _.asInstanceOf[js.Object]

  implicit val charWriter: Writer[Char] = _.toString.asInstanceOf[js.Object]

  implicit val byteWriter: Writer[Byte] = _.asInstanceOf[js.Object]

  implicit val shortWriter: Writer[Short] = _.asInstanceOf[js.Object]

  implicit val intWriter: Writer[Int] = _.asInstanceOf[js.Object]

  implicit val longWriter: Writer[Long] = _.toString.asInstanceOf[js.Object]

  implicit val booleanWriter: Writer[Boolean] = _.asInstanceOf[js.Object]

  implicit val doubleWriter: Writer[Double] = _.asInstanceOf[js.Object]

  implicit val floatWriter: Writer[Float] = _.asInstanceOf[js.Object]

  implicit def undefOrWriter[T](implicit writer: Writer[T]): Writer[js.UndefOr[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit def unionWriter[A: ClassTag, B: ClassTag](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[A | B] = { v =>
    try {
      aWriter.write(v.asInstanceOf[A])
    } catch {
      case e: Throwable =>
        try {
          bWriter.write(v.asInstanceOf[B])
        } catch {
          case e2: Throwable =>
            println("Neither writer for the union worked.")
            e.printStackTrace()
            throw e2
        }
    }
  }

  implicit def optionWriter[T](implicit writer: Writer[T]): Writer[Option[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit def eitherWriter[A, B](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[Either[A, B]] = { v =>
    val written = v.fold(aWriter.write, bWriter.write)
    js.Dynamic.literal(
      isLeft = v.isLeft,
      value = written
    )
  }

  implicit def collectionWriter[T, C[_]](implicit writer: Writer[T],
                                         cbf: CanBuildFrom[Nothing, T, Seq[T]],
                                         ev: C[T] <:< Iterable[T]): Writer[C[T]] = s => {
    js.Array(s.to[Seq](cbf).map(v => writer.write(v)): _*).asInstanceOf[js.Object]
  }

  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = s => {
    import scala.scalajs.js.JSConverters._
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }
}
