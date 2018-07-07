package slinky.core

import slinky.core.facade.ReactElement
import slinky.web.ReactDOM
import org.scalatest.FunSuite
import slinky.web.html._

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.MouseEvent

class InnerClassCustom extends js.Object {
  val customTag = new CustomTag("custom-element")
  val customClass = new CustomAttribute[String]("class")
  val customColorAttr = new CustomAttribute[String]("color")

  def run(): Unit = {
    val divContainer = dom.document.createElement("div")
    ReactDOM.render(customTag(customClass := "foo", customColorAttr := "bar")("hello!"), divContainer)
    assert(divContainer.innerHTML == """<custom-element class="foo" color="bar">hello!</custom-element>""")
  }
}

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

  test("Can create empty tag") {
    a()
  }

  test("Can provide string attributes through method API") {
    val instance: ReactElement = a(href = "abc", id = "def")
    assert(instance.asInstanceOf[js.Dynamic].props.href.asInstanceOf[String] == "abc")
    assert(instance.asInstanceOf[js.Dynamic].props.id.asInstanceOf[String] == "def")
  }

  test("Can add dynamic attributes after method API") {
    val instance: ReactElement = a(href = "abc").withDynamicAttributes(id := "def")
    assert(instance.asInstanceOf[js.Dynamic].props.href.asInstanceOf[String] == "abc")
    assert(instance.asInstanceOf[js.Dynamic].props.id.asInstanceOf[String] == "def")
  }

  test("Can provide a custom tag, which is supported by all components") {
    val customHref = new CustomAttribute[String]("href")

    val instance: ReactElement = a(customHref := "foo")
    assert(instance.asInstanceOf[js.Dynamic].props.href.asInstanceOf[String] == "foo")
  }

  test("Can use Boolean attribute by providing a value") {
    val instance: ReactElement = input(disabled = false)
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

  test("Can construct a custom tag with existing attributes") {
    val customTag = new CustomTag("custom-element")

    val divContainer = dom.document.createElement("div")
    ReactDOM.render(customTag(href := "foo")("hello!"), divContainer)
    assert(divContainer.innerHTML == """<custom-element href="foo">hello!</custom-element>""")
  }

  test("Can construct a custom tag with custom attributes") {
    val customTag = new CustomTag("custom-element")
    val customClass = new CustomAttribute[String]("class")
    val customColorAttr = new CustomAttribute[String]("color")

    val divContainer = dom.document.createElement("div")
    ReactDOM.render(customTag(customClass := "foo", customColorAttr := "bar")("hello!"), divContainer)
    assert(divContainer.innerHTML == """<custom-element class="foo" color="bar">hello!</custom-element>""")
  }

  test("Can construct a custom tag with custom attributes inside another class") {
    (new InnerClassCustom).run()
  }
}
