package slinky.readwrite

import scala.collection.generic.CanBuildFrom
import scala.concurrent.Future
import scala.language.experimental.macros
import scala.language.{higherKinds, implicitConversions}
import scala.reflect.ClassTag
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.|

import magnolia._

trait Reader[P] {
  def read(o: js.Object): P = {
    if (js.typeOf(o) == "object" && o.hasOwnProperty("__")) {
      o.asInstanceOf[js.Dynamic].__.asInstanceOf[P]
    } else {
      forceRead(o)
    }
  }

  protected def forceRead(o: js.Object): P
}

object Reader {
  implicit def jsAnyReader[T <: js.Any]: Reader[T] = _.asInstanceOf[T]

  implicit val unitReader: Reader[Unit] = _ => ()

  implicit val stringReader: Reader[String] = _.asInstanceOf[String]

  implicit val charReader: Reader[Char] = _.asInstanceOf[String].head

  implicit val byteReader: Reader[Byte] = _.asInstanceOf[Byte]

  implicit val shortReader: Reader[Short] = _.asInstanceOf[Short]

  implicit val intReader: Reader[Int] = _.asInstanceOf[Int]

  implicit val longReader: Reader[Long] = _.asInstanceOf[String].toLong

  implicit val booleanReader: Reader[Boolean] = _.asInstanceOf[Boolean]

  implicit val doubleReader: Reader[Double] = _.asInstanceOf[Double]

  implicit val floatReader: Reader[Float] = _.asInstanceOf[Float]

  implicit def undefOrReader[T](implicit reader: Reader[T]): Reader[js.UndefOr[T]] = s => {
    if (js.isUndefined(s)) {
      js.undefined
    } else {
      reader.read(s)
    }
  }

  implicit def unionReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[A | B] = s => {
    try {
      aReader.read(s)
    } catch {
      case _: Throwable => bReader.read(s)
    }
  }

  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] = s => {
    if (js.isUndefined(s)) {
      None
    } else {
      Some(reader.read(s))
    }
  }

  implicit def collectionReader[T, C[_]](implicit reader: Reader[T],
                                         cbf: CanBuildFrom[Nothing, T, C[T]],
                                         ev: C[T] <:< Iterable[T]): Reader[C[T]] =
    _.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)).to[C]

  implicit def function0Reader[O](implicit oReader: Reader[O]): Reader[() => O] = s => {
    val fn = s.asInstanceOf[js.Function0[js.Object]]
    () => {
      oReader.read(fn())
    }
  }

  implicit def function1Reader[I, O](implicit iWriter: Writer[I], oReader: Reader[O]): Reader[I => O] = s => {
    val fn = s.asInstanceOf[js.Function1[js.Object, js.Object]]
    (i: I) => {
      oReader.read(fn(iWriter.write(i)))
    }
  }

  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] =
    _.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
      oReader.read(v)
    }

  type Typeclass[T] = Reader[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = o => {
    if (ctx.isValueClass) {
      ctx.construct { param =>
        param.typeclass.read(o)
      }
    } else {
      ctx.construct { param =>
        param.typeclass.read(o.asInstanceOf[js.Dynamic].selectDynamic(param.label).asInstanceOf[js.Object])
      }
    }
  }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = o => {
    val typeString = o.asInstanceOf[js.Dynamic]._type.asInstanceOf[String]
    ctx.subtypes.find(_.label == typeString).get.typeclass.read(o)
  }

  def fallback[T]: Reader[T] = v => {
    if (!v.hasOwnProperty("__")) {
      throw new IllegalArgumentException("Tried to read opaque Scala.js type that was not written by opaque writer")
    } else {
      v.asInstanceOf[js.Dynamic].__.asInstanceOf[T]
    }
  }

  implicit def generic[T]: Typeclass[T] = macro Magnolia.gen[T]
}

trait Writer[P] {
  def write(p: P): js.Object
}

object Writer {
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

  implicit def undefOrWriter[T](implicit writer: Writer[T]): Writer[js.UndefOr[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit def unionWriter[A: ClassTag, B: ClassTag](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[A | B] = {
    case a: A => aWriter.write(a)
    case b: B => bWriter.write(b)
  }

  implicit def optionWriter[T](implicit writer: Writer[T]): Writer[Option[T]] =
    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])

  implicit def collectionWriter[T, C[_]](implicit writer: Writer[T],
                                         cbf: CanBuildFrom[Nothing, T, Seq[T]],
                                         ev: C[T] <:< Iterable[T]): Writer[C[T]] = s => {
    js.Array(s.to[Seq](cbf).map(v => writer.write(v)): _*).asInstanceOf[js.Object]
  }

  implicit def function0Writer[O](implicit oWriter: Writer[O]): Writer[() => O] = s => {
    val fn: js.Function0[js.Object] = () => {
      oWriter.write(s())
    }

    fn.asInstanceOf[js.Object]
  }

  implicit def function1Writer[I, O](implicit iReader: Reader[I], oWriter: Writer[O]): Writer[I => O] = s => {
    val fn: js.Function1[js.Object, js.Object] = (i: js.Object) => {
      oWriter.write(s(iReader.read(i)))
    }

    fn.asInstanceOf[js.Object]
  }

  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = s => {
    import scala.scalajs.js.JSConverters._
    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
  }

  type Typeclass[T] = Writer[T]

  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = value => {
    if (ctx.isValueClass) {
      val param = ctx.parameters.head
      param.typeclass.write(param.dereference(value))
    } else if (ctx.isObject) {
      js.Dynamic.literal("_type" -> ctx.typeName)
    } else {
      val ret = js.Dynamic.literal()
      ctx.parameters.foreach { param =>
        val dereferenced = param.dereference(value)
        // If any value is js.undefined, don't add it as a property to the written object.
        // This way, JS libraries that rely on checking if a property does not exists (where or not set to undefined)
        // will work correctly
        if (!js.isUndefined(dereferenced)) {
          ret.updateDynamic(param.label)(param.typeclass.write(dereferenced))
        }
      }

      ret
    }
  }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = value => {
    ctx.dispatch(value) { sub =>
      val ret = sub.typeclass.write(sub.cast(value))

      ret.asInstanceOf[js.Dynamic].updateDynamic("_type")(sub.label)

      ret
    }
  }

  def fallback[T]: Writer[T] = s => js.Dynamic.literal(__ = s.asInstanceOf[js.Any])

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
