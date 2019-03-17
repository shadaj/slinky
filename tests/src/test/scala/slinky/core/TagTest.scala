package slinky.core

import org.scalatest.FunSuite

import slinky.core.facade.{React, ReactElement}
import slinky.web.{ReactDOM, SyntheticMouseEvent}
import slinky.web.html._

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{Element, html, Event, MouseEvent}

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
    val foo: Seq[ReactElement] = Seq(div(), a())
  }

  test("Sequence of different tag types can used as child of tag") {
    div(Seq(div(), a()))
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

  test("Mouse events can be given a function taking a SyntheticMouseEvent") {
    assertCompiles("div(onMouseOver := ((v: SyntheticMouseEvent[Element]) => {}))")
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

  test("Can specify defaultValue on a select tag") {
    val selectRef = React.createRef[dom.html.Select]
    ReactDOM.render(
      select(defaultValue := "item-1", ref := selectRef)(
        option(value := "item-1")("Item 1")
      ),
      dom.document.createElement("div")
    )

    assert(selectRef.current.value == "item-1")
  }

  test("Can specify defaultChecked on a checkbox") {
    val inputRef = React.createRef[dom.html.Input]
    ReactDOM.render(
      input(`type` := "checkbox", defaultChecked, ref := inputRef),
      dom.document.createElement("div")
    )

    assert(inputRef.current.checked)
  }

  test("Can grab the target for an input event listener and use input properties") {
    input(onInput := (v => v.target.value))(
      "body"
    )
  }

  test("Can get the ref to a div as an HTMLElement") {
    val myRef = React.createRef[html.Element]
    div(ref := myRef)
  }
}
