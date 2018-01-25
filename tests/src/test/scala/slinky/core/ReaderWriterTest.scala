package slinky.core

import slinky.readwrite.{Reader, Writer}
import org.scalatest.FunSuite

import scala.scalajs.js
import scala.scalajs.js.|

// cannot be a local class
class ValueClass(val int: Int) extends AnyVal

class ReaderWriterTest extends FunSuite {
  private def readWrittenSame[T](v: T, isOpaque: Boolean = false)(implicit reader: Reader[T], writer: Writer[T]) = {
    val written = writer.write(v)
    if (!isOpaque) {
      assert(js.isUndefined(written) || js.isUndefined(written.asInstanceOf[js.Dynamic].__))
    } else {
      assert(!js.isUndefined(written.asInstanceOf[js.Dynamic].__))
    }

    assert(reader.read(written) == v)
  }

  test("Read/write - byte") {
    readWrittenSame(1.toByte)
  }

  test("Read/write - short") {
    readWrittenSame(1.toShort)
  }

  test("Read/write - int") {
    readWrittenSame(1)
  }

  test("Read/write - char") {
    readWrittenSame('a')
  }

  test("Read/write - long") {
    readWrittenSame(1L)
  }

  test("Read/write - float") {
    readWrittenSame(1F)
  }

  test("Read/write - double") {
    readWrittenSame(1D)
  }

  test("Read/write - js.Dynamic") {
    readWrittenSame(js.Dynamic.literal(a = 1))
  }

  test("Read/write - js.UndefOr") {
    val defined: js.UndefOr[List[Int]] = List(1)
    readWrittenSame(defined)
    val undefined: js.UndefOr[List[Int]] = js.undefined
    readWrittenSame(undefined)
  }

  test("Read/write - js.|") {
    readWrittenSame[Int | String](1)
    readWrittenSame[Int | String]("str")
  }

  test("Read/write - js.Array") {
    readWrittenSame[js.Array[String]](js.Array("hello"))
  }

  test("Read/write - case class") {
    case class CaseClass(int: Int, boolean: Boolean)
    readWrittenSame(CaseClass(1, true))
  }

  test("Read/write - case class with default js.undefined") {
    case class CaseClass(int: Int, boolean: js.UndefOr[Boolean] = js.undefined)
    readWrittenSame(CaseClass(1))
    readWrittenSame(CaseClass(1, true))

    // Assert that any undefined property in the input does not map to the written object's property;
    // not even as js.undefined
    val written = implicitly[Writer[CaseClass]].write(CaseClass(1))
    assert(written.asInstanceOf[js.Object].hasOwnProperty("int"))
    assert(!written.asInstanceOf[js.Object].hasOwnProperty("boolean"))
  }

  test("Read/write - sealed trait with case objects") {
    sealed trait MySealedTrait
    case class SubTypeA(int: Int) extends MySealedTrait
    case class SubTypeB(boolean: Boolean) extends MySealedTrait
    case object SubTypeC extends MySealedTrait

    readWrittenSame[MySealedTrait](SubTypeA(-1))
    readWrittenSame[MySealedTrait](SubTypeB(true))
    readWrittenSame[MySealedTrait](SubTypeC)
  }

  test("Read/write - value class") {
    readWrittenSame(new ValueClass(1))

    // directly writes the inner value without wrapping it in an object
    assert(implicitly[Writer[ValueClass]].write(new ValueClass(1)).asInstanceOf[Int] == 1)
  }

  test("Read/write - sequences") {
    readWrittenSame(List(1, 2))
  }

  test("Read/write - opaque class") {
    class OpaqueClass(int: Int)
    readWrittenSame(new OpaqueClass(1), true)
  }
}
