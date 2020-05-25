package slinky.core

import org.scalajs.dom
import slinky.core.facade.Profiler
import slinky.web.ReactDOM
import slinky.web.html.div

import org.scalatest.funsuite.AnyFunSuite

class ProfilerTest extends AnyFunSuite {
  test("Can render a Profiler component with children") {
    val target = dom.document.createElement("div")
    ReactDOM.render(
      Profiler(id = "profiler", onRender = (_, _, _, _, _, _, _) => {})(
        div("hello!")
      ),
      target
    )

    assert(target.innerHTML == "<div>hello!</div>")
  }
}
