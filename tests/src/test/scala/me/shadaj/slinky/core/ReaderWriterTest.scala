package me.shadaj.slinky.core

import org.scalatest.FunSuite

import scala.scalajs.js

class ReaderWriterTest extends FunSuite {
  private def readWrittenSame[T: Reader: Writer](v: T, isOpaque: Boolean = false) = {
    val written = implicitly[Writer[T]].write(v)
    if (!isOpaque) {
      assert(js.isUndefined(written) || js.isUndefined(written.asInstanceOf[js.Dynamic].__))
    } else {
      assert(!js.isUndefined(written.asInstanceOf[js.Dynamic].__))
    }

    assert(implicitly[Reader[T]].read(written) == v)
    assert(implicitly[Reader[T]].read(implicitly[Writer[T]].write(v, true), true) == v)
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
    readWrittenSame('a', true)
  }

  test("Read/write - long") {
    readWrittenSame(1L, true)
  }

  test("Read/write - float") {
    readWrittenSame(1F)
  }

  test("Read/write - double") {
    readWrittenSame(1D)
  }

  test("Read/write - js.UndefOr") {
    val defined: js.UndefOr[List[Int]] = List(1)
    readWrittenSame(defined)
    val undefined: js.UndefOr[List[Int]] = js.undefined
    readWrittenSame(undefined)
  }

  test("Read/write - case class") {
    case class CaseClass(int: Int, boolean: Boolean)
    readWrittenSame(CaseClass(1, true))
  }

  test("Read/write - sequences") {
    readWrittenSame(List(1, 2))
  }

  test("Read/write - opaque class") {
    class OpaqueClass(int: Int)
    readWrittenSame(new OpaqueClass(1), true)
  }
}
