package slinky.core

import slinky.core.facade.StrictMode
import slinky.web.ReactDOM
import slinky.web.html.div

import org.scalajs.dom

import org.scalatest.funsuite.AnyFunSuite

class StrictModeTest extends AnyFunSuite {
  test("Can render a StrictMode component with children") {
    val target = dom.document.createElement("div")
    ReactDOM.render(
      StrictMode(
        div()
      ),
      target
    )

    assert(target.innerHTML == "<div></div>")
  }
}
