package me.shadaj.simple.react.core

import shapeless.PolyDefns.->

import scala.scalajs.js
import shapeless._
import shapeless.labelled.{FieldType, field}

trait Reader[P] {
  def read(o: js.Object, root: Boolean = false): P
}

object Reader {
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

  implicit val doubleReader: Reader[Double] = (s, root) => (if (root) {
    s.asInstanceOf[js.Dynamic].value
  } else {
    s
  }).asInstanceOf[Int]

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

  implicit val doubleReader: Writer[Double] = (s, root) => if (root) {
    js.Dynamic.literal("value" -> s)
  } else {
    s.asInstanceOf[js.Object]
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
