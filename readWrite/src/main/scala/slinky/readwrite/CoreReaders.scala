package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.scalajs.js.|
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.experimental.macros
import scala.reflect.api.Symbols
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

object MacroReadersImpl {
  private val derivationMemo = new ThreadLocal[mutable.Map[reflect.api.Symbols#Symbol, Option[String]]] {
    override def initialValue(): mutable.Map[Symbols#Symbol, Option[String]] = mutable.Map.empty
  }

  def derive[T](c: whitebox.Context)(implicit tTag: c.WeakTypeTag[T]): c.Tree = {
    import c.universe._
    val currentMemo = derivationMemo.get()
    val symbol = tTag.tpe.typeSymbol

    def withMemoValue[T](symbol: Symbol, value: Option[String])(thunk: => T): T = {
      val orig = currentMemo.get(symbol)
      currentMemo(symbol) = value
      val ret = thunk
      if (orig.isDefined) {
        currentMemo(symbol) = orig.get
      } else {
        currentMemo.remove(symbol)
      }

      ret
    }

    val replaceDeferred = new Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case q"new slinky.readwrite.DeferredReader[$_, $t]()" =>
          q"${TermName(t.tpe.asInstanceOf[ConstantType].value.value.asInstanceOf[String])}"
        case o =>
          super.transform(o)
      }
    }

    if (currentMemo.get(symbol).contains(None)) {
      c.abort(c.enclosingPosition, "Skipping derivation macro when getting regular implicit")
    } else if (currentMemo.get(symbol).flatten.isDefined) {
      q"new _root_.slinky.readwrite.DeferredReader[${tTag.tpe}, ${c.internal.constantType(Constant(currentMemo(symbol).get))}]"
    } else {
      val regularImplicit = withMemoValue(symbol, None){
        c.inferImplicitValue(
          c.typecheck(tq"_root_.slinky.readwrite.Reader[${tTag.tpe}]", mode = c.TYPEmode).tpe,
          silent = true
        )
      }

      if (regularImplicit.isEmpty) {
        if (symbol.isModuleClass) {
          q"""new _root_.slinky.readwrite.Reader[${tTag.tpe}] {
                def forceRead(o: _root_.scala.scalajs.js.Object): ${tTag.tpe} = {
                  ${symbol.asClass.module}
                }
              }"""
        } else if (symbol.isClass && symbol.asClass.isCaseClass) {
          val constructor = symbol.asClass.primaryConstructor
          val paramsLists = constructor.asMethod.paramLists
          val retName = c.freshName()
          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: _root_.slinky.readwrite.Reader[${tTag.tpe}] = null
              ${TermName(retName)} = new _root_.slinky.readwrite.Reader[${tTag.tpe}] {
                def forceRead(o: _root_.scala.scalajs.js.Object): ${tTag.tpe} = {
                  new ${tTag.tpe}(
                    ...${
                      paramsLists.map { pl =>
                        pl.map { p =>
                          val paramReader = c.untypecheck(replaceDeferred.transform(
                            c.inferImplicitValue(c.typecheck(tq"_root_.slinky.readwrite.Reader[${p.typeSignature}]", mode = c.TYPEmode).tpe)
                          ))
                          q"$paramReader.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
                        }
                      }
                    }
                  )
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
              var ${TermName(retName)}: _root_.slinky.readwrite.Reader[${tTag.tpe}] = null
              ${TermName(retName)} = new _root_.slinky.readwrite.Reader[${tTag.tpe}] {
                def forceRead(o: _root_.scala.scalajs.js.Object): ${tTag.tpe} = {
                  new ${tTag.tpe}(${c.untypecheck(replaceDeferred.transform(
                    c.inferImplicitValue(c.typecheck(tq"_root_.slinky.readwrite.Reader[${actualValue.typeSignature}]", mode = c.TYPEmode).tpe)
                  ))}.read(
                    o
                  ))
                }
              }

              ${TermName(retName)}
            }"""
          }
        } else if (symbol.isClass && symbol.asClass.isSealed) {
          def getSubclasses(clazz: ClassSymbol): Set[Symbol] = {
            // from magnolia
            val children = clazz.knownDirectSubclasses
            val (abstractTypes, concreteTypes) = children.partition(_.isAbstract)

            abstractTypes.map(_.asClass).flatMap(getSubclasses(_)) ++ concreteTypes
          }

          val retName = c.freshName()

          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: _root_.slinky.readwrite.Reader[${tTag.tpe}] = null
              ${TermName(retName)} = new _root_.slinky.readwrite.Reader[${tTag.tpe}] {
                def forceRead(o: _root_.scala.scalajs.js.Object): ${tTag.tpe} = {
                  o.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type.asInstanceOf[_root_.java.lang.String] match {
                    case ..${getSubclasses(symbol.asClass).map { sub =>
                      cq"""
                        ${sub.name.toString} =>
                          ${c.untypecheck(replaceDeferred.transform(
                            c.inferImplicitValue(c.typecheck(tq"_root_.slinky.readwrite.Reader[$sub]", mode = c.TYPEmode).tpe)
                          ))}.read(
                            o
                          )
                      """
                    }}
                  }
                }
              }

              ${TermName(retName)}
            }"""
          }
        } else {
          q"_root_.slinky.readwrite.Reader.fallback[${tTag.tpe}]"
        }
      } else {
        regularImplicit
      }
    }
  }
}

trait CoreReaders extends MacroReaders with FallbackReaders {
  implicit def jsAnyReader[T <: js.Any]: Reader[T] = _.asInstanceOf[T]

  implicit val unitReader: Reader[Unit] = _ => ()

  implicit val stringReader: Reader[String] = _.asInstanceOf[String]

  implicit val charReader: Reader[Char] = _.asInstanceOf[String].head

  implicit val byteReader: Reader[Byte] = _.asInstanceOf[Byte]

  implicit val shortReader: Reader[Short] = _.asInstanceOf[Short]

  implicit val intReader: Reader[Int] = _.asInstanceOf[Int]

  implicit val longReader: Reader[Long] = _.asInstanceOf[String].toLong

  implicit val booleanReader: Reader[Boolean] = _.asInstanceOf[Boolean]

  implicit val doubleReader: Reader[Double] = _.asInstanceOf[Double]

  implicit val floatReader: Reader[Float] = _.asInstanceOf[Float]

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

  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] = s => {
    if (js.isUndefined(s) || s == null) {
      None
    } else {
      Some(reader.read(s))
    }
  }

  implicit def collectionReader[T, C[_]](implicit reader: Reader[T],
                                         cbf: CanBuildFrom[Nothing, T, C[T]],
                                         ev: C[T] <:< Iterable[T]): Reader[C[T]] =
    _.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)).to[C]

  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] =
    _.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
      oReader.read(v)
    }
}
