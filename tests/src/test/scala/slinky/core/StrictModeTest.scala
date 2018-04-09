package slinky.core

import org.scalajs.dom
import org.scalatest.FunSuite
import slinky.core.facade.StrictMode
import slinky.web.ReactDOM
import slinky.web.html.div

class StrictModeTest extends FunSuite {
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
