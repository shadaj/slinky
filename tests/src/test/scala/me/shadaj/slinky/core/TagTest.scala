package me.shadaj.slinky.core

import org.scalatest.FunSuite

import me.shadaj.slinky.web.html._

class TagTest extends FunSuite {
  test("Fails compilation when an incompatible attr is provided") {
    assertDoesNotCompile("div(width := 1)")
  }

  test("Sequence of different tag types can be typed to TagComponent[Any]") {
    assertCompiles("val foo: Seq[TagComponent[Any]] = Seq(div(), a())")
  }

  test("Sequence of different tag types can used as child of tag") {
    assertCompiles("div(Seq(div(), a()))")
  }
}
