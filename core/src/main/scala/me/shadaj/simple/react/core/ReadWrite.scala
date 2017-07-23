package me.shadaj.simple.react.core

import scala.scalajs.js
import shapeless._
import shapeless.labelled.{FieldType, field}

import scala.collection.generic.CanBuildFrom
import scala.language.implicitConversions

trait Reader[P] {
  def read(o: js.Object, root: Boolean = false): P
}

object Reader {
  implicit def objectReader[T <: js.Object]: Reader[T] = (s, root) => s.asInstanceOf[T]

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
  }).asInstanceOf[Int]

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

  implicit val hnilReader: Reader[HNil] = (s, root) => HNil

  implicit def hconsReader[Key <: Symbol: Witness.Aux, H: Reader, T <: HList: Reader]: Reader[FieldType[Key, H] :: T] =
    (o, root) => {
      val headObj = o.asInstanceOf[js.Dynamic].selectDynamic(implicitly[Witness.Aux[Key]].value.name)
      field[Key](implicitly[Reader[H]].read(headObj.asInstanceOf[js.Object])) :: implicitly[Reader[T]].read(o)
    }

  implicit def caseClassReader[C, R <: HList](implicit
                                              gen: LabelledGeneric.Aux[C, R],
                                              rc: Lazy[Reader[R]]
                                             ): Reader[C] = (s, root) => {
    gen.from(rc.value.read(s))
  }
}

trait Writer[P] {
  def write(p: P, root: Boolean = false): js.Object
}

object Writer {
  implicit def objectWriter[T <: js.Object]: Writer[T] = (s, root) => s

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

  implicit val hnilWriter: Writer[HNil] = (_, _) => js.Dynamic.literal()

  implicit def hconsWriter[Key <: Symbol: Witness.Aux, H: Writer, T <: HList: Writer]: Writer[FieldType[Key, H] :: T] =
    (o, root) => {
      val out = implicitly[Writer[T]].write(o.tail)
      out.asInstanceOf[js.Dynamic].updateDynamic(implicitly[Witness.Aux[Key]].value.name)(implicitly[Writer[H]].write(o.head))
      out
    }

  implicit def caseClassWriter[C, R <: HList](implicit
                                              gen: LabelledGeneric.Aux[C, R],
                                              rc: Lazy[Writer[R]]
                                             ): Writer[C] = (s, root) => {
    rc.value.write(gen.to(s))
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
