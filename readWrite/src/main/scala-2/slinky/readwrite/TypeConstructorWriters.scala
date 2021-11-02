package slinky.readwrite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

trait TypeConstructorWriters {
  implicit def optionWriter[T](implicit writer: Writer[T]): Writer[Option[T]] =
    _.map(v => writer.write(v)).orNull

  implicit def eitherWriter[A, B](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[Either[A, B]] = { v =>
    val written = v.fold(aWriter.write, bWriter.write)
    js.Dynamic.literal(
      isLeft = v.isLeft,
      value = written
    )
  }

  implicit def collectionWriter[T, C[_]](implicit writer: Writer[T], ev: C[T] <:< Iterable[T]): Writer[C[T]] = s => {
    val ret = js.Array[js.Object]()
    s.foreach(v => ret.push(writer.write(v)))
    ret.asInstanceOf[js.Object]
  }

  implicit def arrayWriter[T](implicit writer: Writer[T]): Writer[Array[T]] = s => {
    val ret = new js.Array[js.Object](s.length)
    (0 until s.length).foreach(i => ret(i) = (writer.write(s(i))))
    ret.asInstanceOf[js.Object]
  }

  implicit def mapWriter[A, B](implicit abWriter: Writer[(A, B)]): Writer[Map[A, B]] = s => {
    collectionWriter[(A, B), Iterable].write(s)
  }

  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = s => {
    import scala.scalajs.js.JSConverters._
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }
}
