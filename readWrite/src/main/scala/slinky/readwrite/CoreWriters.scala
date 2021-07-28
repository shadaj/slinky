package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.scalajs.js

@compileTimeOnly("Deferred writers are used to handle recursive structures")
final class DeferredWriter[T, Term] extends Writer[T] {
  override def write(p: T): js.Object = null
}

trait FallbackWriters {
  def fallback[T]: Writer[T] = s => js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
}

trait CoreWriters extends MacroWriters with UnionWriters with FallbackWriters with FunctionWriters with TypeConstructorWriters {
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

  // This one deliberately doesn't have a by-name parameter since with Scala 3 unions, it manages to cause
  // infinite recursion, and there's no point in that (js.undefined | js.undefined | A is same as js.undefined | A,
  // while Option[Option[A]] is very different from Option[A]). Interestingly, if writer was a by-name parameter here,
  // scalac would resolve this as valid implicit for T = Any, making Any not writable
  implicit def undefOrWriter[T](implicit writer: Writer[T]): Writer[js.UndefOr[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit val rangeWriter: Writer[Range] = r => {
    js.Dynamic.literal(start = r.start, end = r.end, step = r.step, inclusive = r.isInclusive)
  }

  implicit val inclusiveRangeWriter: Writer[Range.Inclusive] =
    rangeWriter.asInstanceOf[Writer[Range.Inclusive]]
}
