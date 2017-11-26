package me.shadaj.slinky.core

import scala.scalajs.js
import scala.collection.generic.CanBuildFrom
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.language.higherKinds
import scala.language.experimental.macros
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import magnolia._

import scala.reflect.ClassTag
import scala.scalajs.js.|

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

object Reader {
  implicit def jsAnyReader[T <: js.Any]: Reader[T] = (s, root) => (if (root) {
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

  implicit val charReader: Reader[Char] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[String].head

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

  implicit val longReader: Reader[Long] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[String].toLong

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

  implicit def undefOrReader[T](implicit reader: Reader[T]): Reader[js.UndefOr[T]] = (s, root) => {
    val value = if (root) {
      s.asInstanceOf[js.Dynamic].value.asInstanceOf[js.Object]
    } else {
      s
    }

    if (js.isUndefined(value)) {
      js.undefined
    } else {
      reader.read(value)
    }
  }

  implicit def unionReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[A | B] = (s, root) => {
    try {
      aReader.read(s, root)
    } catch {
      case e: Throwable =>
        bReader.read(s, root)
    }
  }

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

  type Typeclass[T] = Reader[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = (o: js.Object, root) => {
    if (ctx.isValueClass) {
      ctx.construct { param =>
        param.typeclass.read(o, root)
      }
    } else {
      ctx.construct { param =>
        param.typeclass.read(o.asInstanceOf[js.Dynamic].selectDynamic(param.label).asInstanceOf[js.Object])
      }
    }
  }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = (o: js.Object, _) => {
    val typeString = o.asInstanceOf[js.Dynamic]._type.asInstanceOf[String]
    ctx.subtypes.find(_.label == typeString).get.typeclass.read(o, true)
  }

  def fallback[T]: Reader[T] = (v, _) => {
    if (js.isUndefined(v.asInstanceOf[js.Dynamic].__)) {
      throw new Exception("Tried to read opaque Scala.js type that was not written by opaque writer")
    } else {
      v.asInstanceOf[js.Dynamic].__.asInstanceOf[T]
    }
  }

  implicit def generic[T]: Typeclass[T] = macro Magnolia.gen[T]
}

trait Writer[P] {
  def write(p: P, root: Boolean = false): js.Object
}

object Writer {
  implicit def jsAnyWriter[T <: js.Any]: Writer[T] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val unitWriter: Writer[Unit] = (_, _) => js.Dynamic.literal()

  implicit val stringWriter: Writer[String] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
  }

  implicit val charWriter: Writer[Char] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s.toString)
  } else {
    s.toString.asInstanceOf[js.Object]
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

  implicit val longWriter: Writer[Long] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s.toString)
  } else {
    s.toString.asInstanceOf[js.Object]
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

  implicit def undefOrWriter[T](implicit writer: Writer[T]): Writer[js.UndefOr[T]] = (s, root) => {
    if (root) {
      s.map { v =>
        js.Dynamic.literal("value" -> writer.write(v))
      }.getOrElse(js.Dynamic.literal())
    } else {
      s.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])
    }
  }

  implicit def unionWriter[A: ClassTag, B: ClassTag](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[A | B] = (s, root) => {
    s match {
      case a: A => aWriter.write(a, root)
      case b: B => bWriter.write(b, root)
    }
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

  type Typeclass[T] = Writer[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = (value: T, root) => {
    if (ctx.isValueClass) {
      val param = ctx.parameters.head
      param.typeclass.write(param.dereference(value), root)
    } else if (ctx.isObject) {
      js.Dynamic.literal("_type" -> ctx.typeName)
    } else {
      val ret = js.Dynamic.literal()
      ctx.parameters.foreach { param =>
        ret.updateDynamic(param.label)(param.typeclass.write(param.dereference(value)))
      }

      ret
    }
  }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = (value: T, _) => {
    ctx.dispatch(value) { sub =>
      val ret = sub.typeclass.write(sub.cast(value), root = true)

      ret.asInstanceOf[js.Dynamic].updateDynamic("_type")(sub.label)

      ret
    }
  }

  def fallback[T]: Writer[T] = (s, _) => {
    js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
  }

  implicit def generic[T]: Typeclass[T] = macro Magnolia.gen[T]
}

@js.native
trait ObjectOrWritten[T] extends js.Object

object ObjectOrWritten {
  implicit def toUndefOrObject[T, O <: js.Object](value: js.Object): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(value.asInstanceOf[ObjectOrWritten[T]])
  implicit def fromObject[T, O <: js.Object](obj: O): ObjectOrWritten[T] = obj.asInstanceOf[ObjectOrWritten[T]]
  implicit def toUndefOrWritten[T](value: T)(implicit writer: Writer[T]): js.UndefOr[ObjectOrWritten[T]] = js.UndefOr.any2undefOrA(writer.write(value).asInstanceOf[ObjectOrWritten[T]])
  implicit def fromWritten[T](v: T)(implicit writer: Writer[T]): ObjectOrWritten[T] = writer.write(v).asInstanceOf[ObjectOrWritten[T]]
}
