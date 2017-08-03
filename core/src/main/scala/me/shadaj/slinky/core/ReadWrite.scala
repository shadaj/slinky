package me.shadaj.slinky.core

import magnolia.{Coderivation, Derivation}

import scala.scalajs.js
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.ListMap
import scala.concurrent.Future

import scala.language.implicitConversions

trait WithRaw {
  def raw: js.Object with js.Dynamic = {
    this.asInstanceOf[js.Dynamic].__raw.asInstanceOf[js.Object with js.Dynamic ]
  }
}

trait Reader[P] {
  def read(o: js.Object, root: Boolean = false): P = {
    val dyn = o.asInstanceOf[js.Dynamic]

    if (dyn != js.undefined && dyn.__scalaRef != js.undefined) {
      dyn.__scalaRef.asInstanceOf[P]
    } else {
      forceRead(o, root)
    }
  }

  protected def forceRead(o: js.Object, root: Boolean = false): P
}

object Reader extends Derivation[Reader]  {
  implicit def objectReader[T <: js.Object]: Reader[T] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[T]

  implicit val unitReader: Reader[Unit] = (s, root) => ()

  implicit val stringReader: Reader[String] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[String]

  implicit val intReader: Reader[Int] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Int]

  implicit val booleanReader: Reader[Boolean] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Boolean]

  implicit val doubleReader: Reader[Double] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Double]

  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] = (s, root) => {
    val value = if (root) {
      s.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]
    } else {
      s
    }

    if (value == js.undefined) {
      None
    } else {
      Some(reader.read(value))
    }
  }

  implicit def collectionReader[T, Col[_]](implicit reader: Reader[T],
                                           cbf: CanBuildFrom[Nothing, T, Col[T]]): Reader[Col[T]] = (s, root) => {
    val value = (if (root) {
      s.asInstanceOf[js.Dynamic].value
    } else {
      s
    }).asInstanceOf[js.Array[js.Object]]

    val read = value.map(o => reader.read(o))

    read.to[Col]
  }

  implicit def functionReader[I, O](implicit iWriter: Writer[I], oReader: Reader[O]): Reader[I => O] = (s, root) => {
    val fn = s.asInstanceOf[js.Function1[js.Object, js.Object]]
    (i: I) => {
      oReader.read(fn(iWriter.write(i)))
    }
  }

  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] = (s, root) => {
    import scala.scalajs.js.JSConverters._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
    s.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
      oReader.read(v)
    }
  }

  override type Value = js.Object

  override def dereference(value: js.Object, param: String): js.Object = {
    value.asInstanceOf[js.Dynamic].selectDynamic(param).asInstanceOf[js.Object]
  }

  override def call[T](typeclass: Reader[T], value: js.Object): T = typeclass.read(value)

  override def construct[T](body: js.Object => T): Reader[T] = new Reader[T] {
    override def forceRead(o: js.Object, root: Boolean): T = body(o)
  }

  override def combine[Supertype, Right <: Supertype](left: Reader[_ <: Supertype], right: Reader[Right]): Reader[Supertype] = {
    new Reader[Supertype] {
      override def forceRead(o: js.Object, root: Boolean): Supertype = {
        val leftRead = left.read(o)
        val rightRead = right.read(o)
        println(leftRead, rightRead)
        ??? // TODO: what do we do here?
      }
    }
  }
}

trait Writer[P] {
  def write(p: P, root: Boolean = false): js.Object
}

object Writer extends Coderivation[Writer] {
  implicit def objectWriter[T <: js.Object]: Writer[T] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s
  }

  implicit val unitWriter: Writer[Unit] = (s, root) => js.Dynamic.literal()

  implicit val stringWriter: Writer[String] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val intReader: Writer[Int] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val booleanReader: Writer[Boolean] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val doubleReader: Writer[Double] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit def optionWriter[T](implicit writer: Writer[T]): Writer[Option[T]] = (s, root) => {
    if (root) {
      s.map { v =>
        js.Dynamic.literal("value" -> writer.write(v))
      }.getOrElse(js.Dynamic.literal())
    } else {
      s.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])
    }
  }

  implicit def collectionWriter[T, C[_]](implicit writer: Writer[T],
                                         cbf: CanBuildFrom[C[T], T, Seq[T]],
                                         ev: C[T] <:< Iterable[T]): Writer[C[T]] = (s, root) => {
    val arr = js.Array(s.to[Seq](cbf).map(v => writer.write(v)): _*)

    if (root) {
      js.Dynamic.literal("value" -> arr)
    } else {
      arr.asInstanceOf[js.Object]
    }
  }

  implicit def functionWriter[I, O](implicit iReader: Reader[I], oWriter: Writer[O]): Writer[I => O] = (s, root) => {
    val fn: js.Function1[js.Object, js.Object] = (i: js.Object) => {
      oWriter.write(s(iReader.read(i)))
    }

    fn.asInstanceOf[js.Object]
  }

  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = (s, root) => {
    import scala.scalajs.js.JSConverters._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }

  type Return = js.Object
  def call[T](writer: Writer[T], value: T): Return = writer.write(value)
  def construct[T](body: T => Return): Writer[T] = new Writer[T] {
    override def write(p: T, root: Boolean): js.Object = body(p)
  }

  def join(name: String, xs: ListMap[String, Return]): Return = {
    val ret = js.Dynamic.literal()
    xs.foreach(p => ret.updateDynamic(p._1)(p._2))
    ret
  }
}

@js.native
trait ObjectOrWritten[T] extends js.Object

object ObjectOrWritten {
  implicit def toUndefOrObject[T, O <: js.Object](value: js.Object): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(value.asInstanceOf[ObjectOrWritten[T]])
  implicit def fromObject[T, O <: js.Object](obj: O): ObjectOrWritten[T] = obj.asInstanceOf[ObjectOrWritten[T]]
  implicit def toUndefOrWritten[T](value: T)(implicit writer: Writer[T]): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(writer.write(value).asInstanceOf[ObjectOrWritten[T]])
  implicit def fromWritten[T](v: T)(implicit writer: Writer[T]): ObjectOrWritten[T] = writer.write(v).asInstanceOf[ObjectOrWritten[T]]
}
