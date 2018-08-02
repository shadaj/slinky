package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
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

object MacroWritersImpl {
  private val derivationMemo = new ThreadLocal[mutable.Map[String, Option[String]]] {
    override def initialValue(): mutable.Map[String, Option[String]] = mutable.Map.empty
  }

  def derive[T](c: whitebox.Context)(implicit tTag: c.WeakTypeTag[T]): c.Tree = {
    import c.universe._
    val currentMemo = derivationMemo.get()
    val symbol = tTag.tpe.typeSymbol

    def withMemoValue[T](symbol: Symbol, value: Option[String])(thunk: => T): T = {
      val orig = currentMemo.get(symbol.fullName)
      currentMemo(symbol.fullName) = value
      try {
        thunk
      } finally {
        if (orig.isDefined) {
          currentMemo(symbol.fullName) = orig.get
        } else {
          currentMemo.remove(symbol.fullName)
        }
      }
    }

    val replaceDeferred = new Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case q"new slinky.readwrite.DeferredWriter[$_, $t]()" =>
          q"${TermName(t.tpe.asInstanceOf[ConstantType].value.value.asInstanceOf[String])}"
        case o =>
          super.transform(o)
      }
    }

    if (currentMemo.get(symbol.fullName).contains(None)) {
      c.abort(c.enclosingPosition, "Skipping derivation macro when getting regular implicit")
    } else if (currentMemo.get(symbol.fullName).flatten.isDefined) {
      q"new _root_.slinky.readwrite.DeferredWriter[${tTag.tpe}, ${c.internal.constantType(Constant(currentMemo(symbol.fullName).get))}]"
    } else if (symbol.isParameter) {
      c.abort(c.enclosingPosition, "")
    } else {
      val regularImplicit = withMemoValue(symbol, None) {
        c.inferImplicitValue(
          c.typecheck(tq"_root_.slinky.readwrite.Writer[${tTag.tpe}]", mode = c.TYPEmode).tpe,
          silent = true
        )
      }

      if (regularImplicit.isEmpty) {
        if (symbol.isClass && symbol.asClass.isCaseClass) {
          val constructor = symbol.asClass.primaryConstructor
          val paramsLists = constructor.asMethod.paramLists
          val retName = c.freshName()
          val substituteMap = symbol.asClass.typeParams.zip(tTag.tpe.typeArgs).toMap

          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: _root_.slinky.readwrite.Writer[${tTag.tpe}] = null
              ${TermName(retName)} = new _root_.slinky.readwrite.Writer[${tTag.tpe}] {
                def write(v: ${tTag.tpe}): _root_.scala.scalajs.js.Object = {
                  val ret = _root_.scala.scalajs.js.Dynamic.literal()

                  ..${
                    paramsLists.flatten.map { s =>
                      val paramWriter = c.untypecheck(replaceDeferred.transform(
                        c.inferImplicitValue(c.typecheck(
                          tq"_root_.slinky.readwrite.Writer[${substituteMap.getOrElse(s.typeSignature.typeSymbol, s.typeSignature)}]", mode = c.TYPEmode
                        ).tpe)
                      ))
                      q"""{
                            val writtenParam = $paramWriter.write(v.${s.name.toTermName})
                            if (!_root_.scala.scalajs.js.isUndefined(writtenParam)) {
                              ret.${TermName(s.name.encodedName.toString)} = writtenParam
                            }
                          }"""
                    }
                  }

                  ret
                }
              }
              ${TermName(retName)}
            }"""
          }
        } else if (symbol.isClass && tTag.tpe <:< typeOf[AnyVal]) {
          val actualValue = symbol.asClass.primaryConstructor.asMethod.paramLists.head.head

          val retName = c.freshName()

          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: _root_.slinky.readwrite.Writer[${tTag.tpe}] = null
              ${TermName(retName)} = new _root_.slinky.readwrite.Writer[${tTag.tpe}] {
                def write(v: ${tTag.tpe}) = {
                  ${c.untypecheck(replaceDeferred.transform(
                    c.inferImplicitValue(c.typecheck(tq"_root_.slinky.readwrite.Writer[${actualValue.typeSignature}]", mode = c.TYPEmode).tpe)
                  ))}.write(
                    v.${actualValue.name.toTermName}
                  )
                }
              }

              ${TermName(retName)}
            }"""
          }
        } else if (symbol.isClass && symbol.asClass.isSealed && symbol.asType.toType.typeArgs.isEmpty) {
          def getSubclasses(clazz: ClassSymbol): Set[Symbol] = {
            // from magnolia
            val children = clazz.knownDirectSubclasses
            val (abstractTypes, concreteTypes) = children.partition(_.isAbstract)

            abstractTypes.map(_.asClass).flatMap(getSubclasses(_)) ++ concreteTypes
          }

          val retName = c.freshName()

          withMemoValue(symbol, Some(retName)) {
            q"""{
               var ${TermName(retName)}: _root_.slinky.readwrite.Writer[${tTag.tpe}] = null
               ${TermName(retName)} = new _root_.slinky.readwrite.Writer[${tTag.tpe}] {
                 def write(v: ${tTag.tpe}) = {
                   v match {
                     case ..${getSubclasses(symbol.asClass).map { sub =>
                       cq"""
                          (value: $sub) =>
                            val ret = ${c.untypecheck(replaceDeferred.transform(
                              c.inferImplicitValue(c.typecheck(tq"_root_.slinky.readwrite.Writer[$sub]", mode = c.TYPEmode).tpe)
                            ))}.write(
                              value
                            )

                            ret.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type = ${sub.name.toString}

                            ret
                        """
                     }}

                     case o => _root_.slinky.readwrite.Writer.fallback[${tTag.tpe}].write(o)
                   }
                 }
               }

               ${TermName(retName)}
             }"""
          }
        } else {
          q"_root_.slinky.readwrite.Writer.fallback[${tTag.tpe}]"
        }
      } else {
        regularImplicit
      }
    }
  }
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
