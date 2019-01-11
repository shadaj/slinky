package slinky.core

import org.scalajs.dom
import org.scalatest.FunSuite
import slinky.core.facade.Suspense
import slinky.web.ReactDOM
import slinky.web.html.div

class SuspenseTest extends FunSuite {
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
