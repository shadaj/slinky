package slinky.readwrite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.reflect.ClassTag
import CompatUtil._

trait TypeConstructorReaders {
  implicit def optionReader[T](implicit reader: => Reader[T]): Reader[Option[T]] =
    (s => {
      if (js.isUndefined(s) || s == null) {
        None
      } else {
        Some(reader.read(s))
      }
    }): AlwaysReadReader[Option[T]]

  implicit def eitherReader[A, B](implicit aReader: => Reader[A], bReader: => Reader[B]): Reader[Either[A, B]] = o => {
    if (o.asInstanceOf[js.Dynamic].isLeft.asInstanceOf[Boolean]) {
      Left(aReader.read(o.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]))
    } else {
      Right(bReader.read(o.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]))
    }
  }

  implicit def collectionReader[T, C[A] <: Iterable[A]](
    implicit reader: => Reader[T],
    bf: Factory[T, C[T]]
  ): Reader[C[T]] =
    c => bf.fromSpecific(c.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)))

  implicit def arrayReader[T](implicit reader: => Reader[T], classTag: ClassTag[T]): Reader[Array[T]] = { c =>
    c.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)).toArray
  }

  implicit def mapReader[A, B](implicit abReader: => Reader[(A, B)]): Reader[Map[A, B]] = o => {
    collectionReader[(A, B), Iterable].read(o).toMap
  }
  
  implicit def futureReader[O](implicit oReader: => Reader[O]): Reader[Future[O]] =
    _.asInstanceOf[js.Promise[js.Object]].toFuture.map(v => oReader.read(v))
}
