package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.ReactElement
import org.scalatest.FunSuite
import me.shadaj.slinky.web.html._

import scala.scalajs.js

import org.scalajs.dom.MouseEvent

class TagTest extends FunSuite {
  test("Fails compilation when an incompatible attr is provided") {
    assertDoesNotCompile("div(width := 1)")
  }

  test("Sequence of different tag types can be typed to TagComponent[Any]") {
    assertCompiles("val foo: Seq[ReactElement] = Seq(div(), a())")
  }

  test("Sequence of different tag types can used as child of tag") {
    assertCompiles("div(Seq(div(), a()))")
  }

  test("Can provide a custom tag, which is supported by all components") {
    val customHref = new CustomAttribute[String]("href")

    val instance: ReactElement = a(customHref := "foo")
    assert(instance.asInstanceOf[js.Dynamic].props.href.asInstanceOf[String] == "foo")
  }

  test("Can use Boolean attribute by itself providing a value") {
    val instance: ReactElement = input(disabled := false)
    assert(instance.asInstanceOf[js.Dynamic].props.disabled.asInstanceOf[Boolean] == false)
  }

  test("Can use Boolean attribute by itself without providing value") {
    val instance: ReactElement = input(disabled)
    assert(instance.asInstanceOf[js.Dynamic].props.disabled.asInstanceOf[Boolean] == true)
  }

  test("Using a non-Boolean attribute by itself does not compiles") {
    assertDoesNotCompile("input(href)")
  }

  test("Mouse events can be given a function taking a MouseEvent") {
    assertCompiles("div(onMouseOver := ((v: MouseEvent) => {}))")
  }
}
