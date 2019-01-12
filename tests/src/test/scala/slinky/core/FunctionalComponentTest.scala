package slinky.core

import org.scalatest.FunSuite
import org.scalajs.dom.document

import slinky.web.ReactDOM

class FunctionalComponentTest extends FunSuite {
  test("Can render a functional component") {
    val container = document.createElement("div")
    val component = FunctionalComponent[Int](_.toString)
    ReactDOM.render(component(1), container)

    assert(container.innerHTML == "1")
  }
}