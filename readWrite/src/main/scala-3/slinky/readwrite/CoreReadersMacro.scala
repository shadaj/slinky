package slinky.readwrite

import scala.deriving._
import scala.compiletime._
import scalajs.js
import scala.util.control.NonFatal

trait MacroReaders {
  inline implicit def deriveReader[T]: Reader[T] = {
    summonFrom {
      case r: Reader[T] => r
      case vc: ValueClass[T] =>
        MacroReaders.ValueClassReader(vc, summonInline[Reader[vc.Repr]])
      case m: Mirror.ProductOf[T] => deriveProduct(m)
      case m: Mirror.SumOf[T] => deriveSum(m)
      case nu: NominalUnion[T] => MacroReaders.UnionReader(summonAll[Tuple.Map[nu.Constituents, Reader]])
    }
  }

  inline def deriveProduct[T](m: Mirror.ProductOf[T]): Reader[T] = {
    val labels = constValueTuple[m.MirroredElemLabels]
    val readers = summonAll[Tuple.Map[m.MirroredElemTypes, Reader]]
    val defaults = summonFrom {
      case d: slinky.readwrite.DefaultConstructorParameters[T] => d.values
      case _ => null
    }
    MacroReaders.ProductReader(m, labels, readers, defaults)
  }

  inline def deriveSum[T](m: Mirror.SumOf[T]): Reader[T] = {
    val readers = summonAll[Tuple.Map[m.MirroredElemTypes, Reader]]
    MacroReaders.SumReader(readers)
  }
}

object MacroReaders {
  class ValueClassReader[T, R](vc: ValueClass[T] { type Repr = R }, reader: Reader[R]) extends Reader[T] {
    protected def forceRead(o: scala.scalajs.js.Object): T = vc.to(reader.read(o))
  }

  class UnionReader[T](readers: Tuple) extends Reader[T] {
    protected def forceRead(o: scala.scalajs.js.Object): T = {
      var lastEx: Throwable = null
      readers.productIterator.asInstanceOf[Iterator[Reader[T]]]
        .map { r => try { Some(r.read(o)) } catch { case NonFatal(ex) => lastEx = ex; None }}
        .collectFirst { case Some(a) => a }
        .getOrElse(throw lastEx)
    }
  }

  class ProductReader[T](m: Mirror.ProductOf[T], labels: Tuple, readers: Tuple, defaults: Array[Option[Any]]) extends Reader[T] {
    protected def forceRead(o: scala.scalajs.js.Object): T = {
      val dyn = o.asInstanceOf[js.Dictionary[js.Object]]
      m.fromProduct(new Product{
        def canEqual(that: Any) = this == that
        def productArity = readers.productArity
        def productElement(idx: Int): Any = {
          val key = labels.productElement(idx).asInstanceOf[String]
          def doRead = readers.productElement(idx).asInstanceOf[Reader[_]].read(dyn(key))
          if (!o.hasOwnProperty(key) && (defaults ne null)) {
            defaults(idx).getOrElse(doRead)
          } else {
            doRead
          }
        }
      })
    }
  }

  class SumReader[T](readers: Tuple) extends Reader[T] {
    protected def forceRead(o: scala.scalajs.js.Object): T = {
      val ord = o.asInstanceOf[js.Dynamic]._ord.asInstanceOf[Int]
      readers.productElement(ord).asInstanceOf[Reader[T]].read(o)
    }
  }
}
//class MacroReadersImpl(_c: whitebox.Context) extends GenericDeriveImpl(_c) {
//  import c.universe._
//
//  val typeclassType: c.universe.Type = typeOf[Reader[_]]
//
//  def deferredInstance(forType: c.universe.Type, constantType: c.universe.Type) =
//    q"new _root_.slinky.readwrite.DeferredReader[$forType, $constantType]"
//
//  def maybeExtractDeferred(tree: c.Tree): Option[c.Tree] =
//    tree match {
//      case q"new _root_.slinky.readwrite.DeferredReader[$_, $t]()" =>
//        Some(t)
//      case q"new slinky.readwrite.DeferredReader[$_, $t]()" =>
//        Some(t)
//      case _ => None
//    }
//
//  def createModuleTypeclass(tpe: c.universe.Type, moduleReference: c.Tree): c.Tree =
//    q"""new _root_.slinky.readwrite.Reader[$tpe] {
//          def forceRead(o: _root_.scala.scalajs.js.Object): $tpe = {
//            $moduleReference
//          }
//        }"""
//
//  def createCaseClassTypeclass(clazz: c.Type, params: Seq[Seq[Param]]): c.Tree = {
//    val paramsTrees = params.map(_.map { p =>
//      p.transformIfVarArg {
//        p.default.map { d =>
//          q"if (_root_.scala.scalajs.js.isUndefined(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName})) $d else ${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
//        }.getOrElse {
//          q"${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
//        }
//      }
//    })
//
//    q"""new _root_.slinky.readwrite.Reader[$clazz] {
//          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
//            new $clazz(...$paramsTrees)
//          }
//        }"""
//  }
//
//  def createValueClassTypeclass(clazz: c.Type, param: Param): c.Tree =
//    q"""new _root_.slinky.readwrite.Reader[$clazz] {
//          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
//            new $clazz(${getTypeclass(param.tpe)}.read(o))
//          }
//        }"""
//
//  def createSealedTraitTypeclass(traitType: c.Type, subclasses: Seq[c.Symbol]): c.Tree = {
//    val cases = subclasses.map(sub => cq"""${sub.name.toString} => ${getTypeclass(sub.asType.toType)}.read(o)""")
//
//    q"""new _root_.slinky.readwrite.Reader[$traitType] {
//          def forceRead(o: _root_.scala.scalajs.js.Object): $traitType = {
//            o.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type.asInstanceOf[_root_.java.lang.String] match {
//              case ..$cases
//              case _ => _root_.slinky.readwrite.Reader.fallback[$traitType].read(o)
//            }
//          }
//        }"""
//  }
//
//  def createFallback(forType: c.Type) = q"_root_.slinky.readwrite.Reader.fallback[$forType]"
//}
