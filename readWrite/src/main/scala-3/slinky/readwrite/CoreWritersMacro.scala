package slinky.readwrite

import scala.deriving._
import scala.compiletime._
import scalajs.js
import scala.reflect.ClassTag
import scala.util.control.NonFatal

trait MacroWriters {
  inline implicit def deriveWriter[T]: Writer[T] = {
    summonFrom {
      case w: Writer[T] => w
      case vc: ValueClass[T] => 
        MacroWriters.ValueClassWriter(vc, summonInline[Writer[vc.Repr]])
      case m: Mirror.ProductOf[T] => deriveProduct(m)
      case m: Mirror.SumOf[T] => deriveSum(m)
      case nu: NominalUnion[T] => 
        MacroWriters.UnionWriter(
          summonAll[Tuple.Map[nu.Constituents, Writer]],
          summonAll[Tuple.Map[nu.Constituents, ClassTag]]
        )
    }
  }

  inline def deriveProduct[T](m: Mirror.ProductOf[T]): Writer[T] = {
    val labels = constValueTuple[m.MirroredElemLabels]
    val writers = summonAll[Tuple.Map[m.MirroredElemTypes, Writer]]
    MacroWriters.ProductWriter(labels, writers)
  }

  inline def deriveSum[T](m: Mirror.SumOf[T]): Writer[T] = {
    val labels = constValueTuple[m.MirroredElemLabels]
    val writers = summonAll[Tuple.Map[m.MirroredElemTypes, Writer]]
    MacroWriters.SumWriter(labels, writers, m)
  }
}

object MacroWriters {
  class ValueClassWriter[T, R](vc: ValueClass[T] { type Repr = R }, w: Writer[R]) extends Writer[T] {
    def write(p: T): js.Object = w.write(vc.from(p))
  }

  class UnionWriter[T](writers: Tuple, classTags: Tuple) extends Writer[T] {
    def write(p: T): js.Object = 
      classTags.productIterator.indexWhere(_.asInstanceOf[ClassTag[_]].runtimeClass == p.getClass) match {
        case -1 =>
          var lastEx: Throwable = null
          writers.productIterator.asInstanceOf[Iterator[Writer[T]]]
            .map { w => try { Some(w.write(p)) } catch { case NonFatal(e) => lastEx = e; None } }
            .collectFirst { case Some(obj) => obj }
            .getOrElse { throw lastEx }
        case other => writers.productElement(other).asInstanceOf[Writer[T]].write(p)
      }
  }

  class ProductWriter[T](labels: Tuple, writers: Tuple) extends Writer[T] {
      def write(p: T): js.Object = {
        val d = js.Dictionary[js.Object]()
        labels.productIterator
          .zip(writers.productIterator)
          .zip(p.asInstanceOf[Product].productIterator)
          .foreach { case ((label, writer), value) =>
            d(label.asInstanceOf[String]) = writer.asInstanceOf[Writer[_]].write(value.asInstanceOf)
          }
        d.asInstanceOf[js.Object]
      }
  }

  class SumWriter[T](labels: Tuple, writers: Tuple, m: Mirror.SumOf[T]) extends Writer[T] {
      def write(p: T): js.Object = {
        val ord = m.ordinal(p)
        val typ = labels.productElement(ord)
        val base = writers.productElement(ord).asInstanceOf[Writer[T]].write(p)
        base.asInstanceOf[js.Dynamic]._type = typ.asInstanceOf[js.Any]
        base.asInstanceOf[js.Dynamic]._ord = ord
        base
      }
  }
}

//class MacroWritersImpl(_c: whitebox.Context) extends GenericDeriveImpl(_c) {
//  import c.universe._
//
//  val typeclassType: c.universe.Type = typeOf[Writer[_]]
//
//  def deferredInstance(forType: Type, constantType: Type) =
//    q"new _root_.slinky.readwrite.DeferredWriter[$forType, $constantType]"
//
//  def maybeExtractDeferred(tree: Tree): Option[Tree] =
//    tree match {
//      case q"new _root_.slinky.readwrite.DeferredWriter[$_, $t]()" =>
//        Some(t)
//      case q"new slinky.readwrite.DeferredWriter[$_, $t]()" =>
//        Some(t)
//      case _ => None
//    }
//
//  def createModuleTypeclass(tpe: Type, moduleReference: Tree): Tree =
//    q"""new _root_.slinky.readwrite.Writer[$tpe] {
//          def write(v: $tpe): _root_.scala.scalajs.js.Object = {
//            _root_.scala.scalajs.js.Dynamic.literal()
//          }
//        }"""
//
//  def createCaseClassTypeclass(clazz: Type, params: Seq[Seq[Param]]): Tree = {
//    val paramsTrees = params.flatMap(_.map { p =>
//      q"""{
//         val writtenParam = ${getTypeclass(p.tpe)}.write(v.${p.name.toTermName})
//         if (!_root_.scala.scalajs.js.isUndefined(writtenParam)) {
//           ret.${TermName(p.name.encodedName.toString)} = writtenParam
//         }
//       }"""
//    })
//
//    q"""new _root_.slinky.readwrite.Writer[$clazz] {
//          def write(v: $clazz): _root_.scala.scalajs.js.Object = {
//            val ret = _root_.scala.scalajs.js.Dynamic.literal()
//            ..$paramsTrees
//            ret
//          }
//        }"""
//  }
//
//  def createValueClassTypeclass(clazz: Type, param: Param): Tree =
//    q"""new _root_.slinky.readwrite.Writer[$clazz] {
//          def write(v: $clazz): _root_.scala.scalajs.js.Object = {
//            ${getTypeclass(param.tpe)}.write(v.${param.name.toTermName})
//          }
//        }"""
//
//  def createSealedTraitTypeclass(traitType: Type, subclasses: Seq[Symbol]): Tree = {
//    val cases = subclasses.map { sub =>
//      cq"""(value: $sub) =>
//             val ret = ${getTypeclass(sub.asType.toType)}.write(value)
//             ret.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type = ${sub.name.toString}
//             ret"""
//    }
//
//    q"""new _root_.slinky.readwrite.Writer[$traitType] {
//          def write(v: $traitType): _root_.scala.scalajs.js.Object = {
//            v match {
//              case ..$cases
//              case _ => _root_.slinky.readwrite.Writer.fallback[$traitType].write(v)
//            }
//          }
//        }"""
//  }
//
//  def createFallback(forType: Type) = q"_root_.slinky.readwrite.Writer.fallback[$forType]"
//}
