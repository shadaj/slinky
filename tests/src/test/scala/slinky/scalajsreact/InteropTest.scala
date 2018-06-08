package slinky.scalajsreact

import org.scalatest.FunSuite
import slinky.web.ReactDOM
import org.scalajs.dom.document
import japgolly.scalajs.react.vdom.html_<^._
import Converters._
import slinky.web.html.div

class InteropTest extends FunSuite {
  test("Can convert Scala.js React node to Slinky") {
    val target = document.createElement("div")
    ReactDOM.render(
      <.a(),
      target
    )

    assert(target.innerHTML == "<a></a>")
  }

  test("Can convert Slinky element to Scala.js React node and render through Slinky") {
    val target = document.createElement("div")
    ReactDOM.render(
      <.a(
        div("hello!")
      ),
      target
    )

    assert(target.innerHTML == "<a><div>hello!</div></a>")
  }
}
