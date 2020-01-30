package slinky.core

import org.scalajs.dom
import slinky.core.facade.Suspense
import slinky.web.ReactDOM
import slinky.web.html.div

import org.scalatest.funsuite.AnyFunSuite

class SuspenseTest extends AnyFunSuite {
  test("Can render a Suspense component with children") {
    val target = dom.document.createElement("div")
    ReactDOM.render(
      Suspense(fallback = div())(
        div("hello!")
      ),
      target
    )

    assert(target.innerHTML == "<div>hello!</div>")
  }
}
