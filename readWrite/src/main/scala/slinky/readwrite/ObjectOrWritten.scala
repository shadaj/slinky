package slinky.readwrite

import scala.scalajs.js

@js.native
trait ObjectOrWritten[T] extends js.Object

object ObjectOrWritten extends ObjectOrWrittenVersionSpecific {
  implicit def fromObject[T, O <: js.Object](obj: O): ObjectOrWritten[T] = obj.asInstanceOf[ObjectOrWritten[T]]
  implicit def fromWritten[T](v: T)(implicit writer: Writer[T]): ObjectOrWritten[T] =
    writer.write(v).asInstanceOf[ObjectOrWritten[T]]
}
