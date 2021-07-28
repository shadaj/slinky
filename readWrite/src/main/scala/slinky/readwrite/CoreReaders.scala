package slinky.readwrite

import scala.annotation.compileTimeOnly
import scala.scalajs.js


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

trait CoreReaders extends MacroReaders with UnionReaders with FallbackReaders with FunctionReaders with TypeConstructorReaders {
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

  // reader is not by-name implicit for consistency with undefOrWriter
  implicit def undefOrReader[T](implicit reader: Reader[T]): Reader[js.UndefOr[T]] = s => {
    if (js.isUndefined(s)) {
      js.undefined
    } else {
      reader.read(s)
    }
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

}
