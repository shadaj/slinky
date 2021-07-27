package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

@compileTimeOnly("Deferred writers are used to handle recursive structures")
final class DeferredWriter[T, Term] extends Writer[T] {
  override def write(p: T): js.Object = null
}

trait FallbackWriters {
  def fallback[T]: Writer[T] = s => js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
}

trait CoreWriters extends MacroWriters with UnionWriters with FallbackWriters with FunctionWriters {
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

  implicit def undefOrWriter[T](implicit writer: => Writer[T]): Writer[js.UndefOr[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit def optionWriter[T](implicit writer: => Writer[T]): Writer[Option[T]] =
    _.map(v => writer.write(v)).orNull

  implicit def eitherWriter[A, B](implicit aWriter: => Writer[A], bWriter: => Writer[B]): Writer[Either[A, B]] = { v =>
    val written = v.fold(aWriter.write, bWriter.write)
    js.Dynamic.literal(
      isLeft = v.isLeft,
      value = written
    )
  }

  implicit def collectionWriter[T, C[_]](implicit writer: => Writer[T], ev: C[T] <:< Iterable[T]): Writer[C[T]] = s => {
    val ret = js.Array[js.Object]()
    s.foreach(v => ret.push(writer.write(v)))
    ret.asInstanceOf[js.Object]
  }

  implicit def arrayWriter[T](implicit writer: => Writer[T]): Writer[Array[T]] = s => {
    val ret = new js.Array[js.Object](s.length)
    (0 until s.length).foreach(i => ret(i) = (writer.write(s(i))))
    ret.asInstanceOf[js.Object]
  }

  implicit def mapWriter[A, B](implicit abWriter: => Writer[(A, B)]): Writer[Map[A, B]] = s => {
    collectionWriter[(A, B), Iterable].write(s)
  }

  implicit val rangeWriter: Writer[Range] = r => {
    js.Dynamic.literal(start = r.start, end = r.end, step = r.step, inclusive = r.isInclusive)
  }

  implicit val inclusiveRangeWriter: Writer[Range.Inclusive] =
    rangeWriter.asInstanceOf[Writer[Range.Inclusive]]

  implicit def futureWriter[O](implicit oWriter: => Writer[O]): Writer[Future[O]] = s => {
    import scala.scalajs.js.JSConverters._
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }
}
