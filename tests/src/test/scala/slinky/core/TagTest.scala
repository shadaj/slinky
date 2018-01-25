package slinky.core

import slinky.core.facade.ReactElement
import slinky.web.ReactDOM
import org.scalatest.FunSuite
import slinky.web.html._

import scala.scalajs.js
import org.scalajs.dom
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
    assert(!instance.asInstanceOf[js.Dynamic].props.disabled.asInstanceOf[Boolean])
  }

  test("Can use Boolean attribute by itself without providing value") {
    val instance: ReactElement = input(disabled)
    assert(instance.asInstanceOf[js.Dynamic].props.disabled.asInstanceOf[Boolean])
  }

  test("Using a non-Boolean attribute by itself does not compiles") {
    assertDoesNotCompile("input(href)")
  }

  test("Mouse events can be given a function taking a MouseEvent") {
    assertCompiles("div(onMouseOver := ((v: MouseEvent) => {}))")
  }

  test("Can construct tag with abstraction over element type") {
    def constructTag[T <: Tag: className.supports](tag: T): ReactElement = {
      tag.apply(className := "foo")("hello!")
    }

    val divContainer = dom.document.createElement("div")
    ReactDOM.render(constructTag(div), divContainer)
    assert(divContainer.innerHTML == """<div class="foo">hello!</div>""")
  }
}
