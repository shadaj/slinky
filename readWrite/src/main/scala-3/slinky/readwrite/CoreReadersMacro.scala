package slinky.readwrite

import scala.deriving._
import scala.compiletime._
import scalajs.js
import scala.util.control.NonFatal

trait MacroReaders {
  inline implicit def deriveReader[T]: Reader[T] = {
    summonFrom {
      case r: Reader[T] => r
      case vc: ExoticTypes.ValueClass[T] =>
        MacroReaders.ValueClassReader(vc, summonInline[Reader[vc.Repr]])
      case m: Mirror.ProductOf[T] => deriveProduct(m)
      case m: Mirror.SumOf[T] => deriveSum(m)
      case nu: ExoticTypes.NominalUnion[T] => MacroReaders.UnionReader(summonAll[Tuple.Map[nu.Constituents, Reader]])
      case _ => Reader.fallback[T]
    }
  }

  inline def deriveProduct[T](m: Mirror.ProductOf[T]): Reader[T] = {
    val labels = constValueTuple[m.MirroredElemLabels]
    val readers = summonAll[Tuple.Map[m.MirroredElemTypes, Reader]]
    val defaults = summonFrom {
      case d: ExoticTypes.DefaultConstructorParameters[T] => d.values
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
  class ValueClassReader[T, R](vc: ExoticTypes.ValueClass[T] { type Repr = R }, reader: Reader[R]) extends Reader[T] {
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
