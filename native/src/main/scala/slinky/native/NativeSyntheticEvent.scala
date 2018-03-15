package slinky.native

import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js

case class NativeSyntheticEvent[T](nativeEvent: T)

object NativeSyntheticEvent {
  implicit def reader[T](implicit tReader: Reader[T]): Reader[NativeSyntheticEvent[T]] = { o =>
    NativeSyntheticEvent(tReader.read(o.asInstanceOf[js.Dynamic].nativeEvent.asInstanceOf[js.Object]))
  }

  implicit def writer[T](implicit tWriter: Writer[T]): Writer[NativeSyntheticEvent[T]] = { s =>
    js.Dynamic.literal(
      nativeEvent = tWriter.write(s.nativeEvent)
    )
  }
}
