package me.shadaj.slinky.core

import magnolia.{Macros, Coderivation, Derivation}

import scala.scalajs.js
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.ListMap
import scala.concurrent.Future

import scala.language.implicitConversions
import scala.language.higherKinds
import scala.language.experimental.macros

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

trait Reader[P] {
  def read(o: js.Object, root: Boolean = false): P = {
    val dyn = o.asInstanceOf[js.Dynamic]

    if (!js.isUndefined(dyn) && !js.isUndefined(dyn.__)) {
      dyn.__.asInstanceOf[P]
    } else {
      forceRead(o, root)
    }
  }

  protected def forceRead(o: js.Object, root: Boolean = false): P
}

trait LowPriorityReads {
  implicit def anyReader[T]: Reader[T] = (_, _) => {
    throw new Exception("Tried to read opaque Scala.js type that was not written by opaque writer")
  }
}

object Reader extends LowPriorityReads {
  implicit def objectReader[T <: js.Object]: Reader[T] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[T]

  implicit val unitReader: Reader[Unit] = (_, _) => ()

  implicit val stringReader: Reader[String] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[String]

  implicit val byteReader: Reader[Byte] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Byte]

  implicit val shortReader: Reader[Short] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Short]

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

  implicit val floatReader: Reader[Float] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Float]

  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] = (s, root) => {
    val value = if (root) {
      s.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]
    } else {
      s
    }

    if (js.isUndefined(value)) {
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

  implicit def function0Reader[O](implicit oReader: Reader[O]): Reader[() => O] = (s, _) => {
    val fn = s.asInstanceOf[js.Function0[js.Object]]
    () => {
      oReader.read(fn())
    }
  }

  implicit def function1Reader[I, O](implicit iWriter: Writer[I], oReader: Reader[O]): Reader[I => O] = (s, _) => {
    val fn = s.asInstanceOf[js.Function1[js.Object, js.Object]]
    (i: I) => {
      oReader.read(fn(iWriter.write(i)))
    }
  }

  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] = (s, _) => {

    s.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
      oReader.read(v)
    }
  }

  type Value = js.Object
  def dereference(value: Value, param: String): Value = {
    value.asInstanceOf[js.Dynamic].selectDynamic(param).asInstanceOf[js.Object]
  }

  def call[T](typeclass: Reader[T], value: Value): T = typeclass.read(value)
  def construct[T](body: Value => T): Reader[T] = (o: js.Object, _) => body(o)

  def combine[Supertype, Right <: Supertype](left: Reader[_ <: Supertype],
                                             right: Reader[Right]): Reader[Supertype] = {
    (o: js.Object, _) => {
      val leftRead = left.read(o)
      val rightRead = right.read(o)
      println(leftRead, rightRead)
      throw new Exception("Combine is not supportes")
    }
  }

  implicit def generic[T]: Reader[T] = macro Macros.magnolia[T, Reader[_],
    Derivation[Tc] forSome { type Tc[_] }]
}

trait Writer[P] {
  def write(p: P, root: Boolean = false): js.Object
}

trait LowPriorityWrites {
  implicit def anyWriter[T]: Writer[T] = (s, _) => {
    js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
  }
}

object Writer extends LowPriorityWrites {
  implicit def objectWriter[T <: js.Object]: Writer[T] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s
  }

  implicit val unitWriter: Writer[Unit] = (_, _) => js.Dynamic.literal()

  implicit val stringWriter: Writer[String] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val byteWriter: Writer[Byte] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val shortWriter: Writer[Short] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val intWriter: Writer[Int] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val booleanWriter: Writer[Boolean] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val doubleWriter: Writer[Double] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val floatWriter: Writer[Float] = (s, root) => if (root) {
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

  implicit def function0Writer[O](implicit oWriter: Writer[O]): Writer[() => O] = (s, _) => {
    val fn: js.Function0[js.Object] = () => {
      oWriter.write(s())
    }

    fn.asInstanceOf[js.Object]
  }

  implicit def function1Writer[I, O](implicit iReader: Reader[I], oWriter: Writer[O]): Writer[I => O] = (s, _) => {
    val fn: js.Function1[js.Object, js.Object] = (i: js.Object) => {
      oWriter.write(s(iReader.read(i)))
    }

    fn.asInstanceOf[js.Object]
  }

  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = (s, _) => {
    import scala.scalajs.js.JSConverters._
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }

  type Return = js.Object
  def call[T](typeclass: Writer[T], value: T): Return  = typeclass.write(value)
  def construct[T](body: T => Return): Writer[T] = (p: T, _) => body(p)
  def join(name: String, elements: ListMap[String, Return]): Return = {
    val ret = js.Dynamic.literal()
    elements.foreach(p => ret.updateDynamic(p._1)(p._2))
    ret
  }

  implicit def generic[T]: Writer[T] = macro Macros.magnolia[T, Writer[_],
    Coderivation[Tc] forSome { type Tc[_] }]
}

@js.native
trait ObjectOrWritten[T] extends js.Object

object ObjectOrWritten {
  implicit def toUndefOrObject[T, O <: js.Object](value: js.Object): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(value.asInstanceOf[ObjectOrWritten[T]])
  implicit def fromObject[T, O <: js.Object](obj: O): ObjectOrWritten[T] = obj.asInstanceOf[ObjectOrWritten[T]]
  implicit def toUndefOrWritten[T](value: T)(implicit writer: Writer[T]): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(writer.write(value).asInstanceOf[ObjectOrWritten[T]])
  implicit def fromWritten[T](v: T)(implicit writer: Writer[T]): ObjectOrWritten[T] = writer.write(v).asInstanceOf[ObjectOrWritten[T]]
}
