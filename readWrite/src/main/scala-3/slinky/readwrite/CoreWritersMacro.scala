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
      case vc: ExoticTypes.ValueClass[T] => 
        MacroWriters.ValueClassWriter(vc, summonInline[Writer[vc.Repr]])
      case m: Mirror.ProductOf[T] => deriveProduct(m)
      case m: Mirror.SumOf[T] => deriveSum(m)
      case nu: ExoticTypes.NominalUnion[T] => 
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
    MacroWriters.SumWriter(labels, writers, m.ordinal)
  }
}

object MacroWriters {
  class ValueClassWriter[T, R](vc: ExoticTypes.ValueClass[T] { type Repr = R }, w: Writer[R]) extends Writer[T] {
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
            val written = writer.asInstanceOf[Writer[_]].write(value.asInstanceOf)
            if (!js.isUndefined(written)) {
              d(label.asInstanceOf[String]) = written
            }
          }
        d.asInstanceOf[js.Object]
      }
  }

  class SumWriter[T](labels: Tuple, writers: Tuple, ordinal: T => Int) extends Writer[T] {
      def write(p: T): js.Object = {
        // n.b. using function instead of full-fledged mirror b/c scala3-sjs somehow manages
        // to replace the path-dependent m.MirroredMonoType with garbage like org.scalatest.Exceptional
        // for no good reason
        val ord = ordinal(p)
        val typ = labels.productElement(ord)
        val base = writers.productElement(ord).asInstanceOf[Writer[T]].write(p)
        base.asInstanceOf[js.Dynamic]._type = typ.asInstanceOf[js.Any]
        base.asInstanceOf[js.Dynamic]._ord = ord
        base
      }
  }
}
