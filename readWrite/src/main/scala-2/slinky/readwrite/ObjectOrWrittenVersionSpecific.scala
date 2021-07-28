package slinky.readwrite

import scala.scalajs.js

trait ObjectOrWrittenVersionSpecific {
  implicit def toUndefOrObject[T, O <: js.Object](value: js.Object): js.UndefOr[ObjectOrWritten[T]] =
    value.asInstanceOf[js.UndefOr[ObjectOrWritten[T]]]

  implicit def toUndefOrWritten[T](value: T)(implicit writer: Writer[T]): js.UndefOr[ObjectOrWritten[T]] =
    writer.write(value).asInstanceOf[js.UndefOr[ObjectOrWritten[T]]]
}
