package slinky.core

import slinky.core.facade.ReactElement
import slinky.web.svg._

import scala.scalajs.js

import org.scalatest.funsuite.AnyFunSuite

class SVGTest extends AnyFunSuite {
  test("Can specify key attribute for SVG element") {
    val instance: ReactElement = circle(key := "1")

    assert(instance.asInstanceOf[js.Dynamic].key.asInstanceOf[String] == "1")
  }

  test("Can specify className attribute for SVG element") {
    val instance: ReactElement = svg(className := "foo")

    assert(instance.asInstanceOf[js.Dynamic].props.className.asInstanceOf[String] == "foo")
  }

  test("Can specify role attribute for SVG element") {
    val instance: ReactElement = svg(role := "button")

    assert(instance.asInstanceOf[js.Dynamic].props.role.asInstanceOf[String] == "button")
  }
}
