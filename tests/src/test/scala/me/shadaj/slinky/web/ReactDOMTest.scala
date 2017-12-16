package me.shadaj.slinky.web

import me.shadaj.slinky.core.ComponentWrapper
import me.shadaj.slinky.core.facade.ReactElement
import org.scalajs.dom.{Element, document}

import scala.scalajs.js
import html._
import org.scalatest.FunSuite

object TestComponent extends ComponentWrapper {
  type Props = Unit
  type State = Unit

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Unit = ()

    override def render(): ReactElement = {
      a()
    }
  }
}

class ReactDOMTest extends FunSuite {
  test("Renders a single element into the DOM") {
    val target = document.createElement("div")
    ReactDOM.render(
      a(),
      target
    )

    assert(target.childNodes(0).asInstanceOf[Element].tagName.toLowerCase == "a")
  }

  test("Finds a dom node for a component") {
    val comp: ReactElement = TestComponent(())
    val target = document.createElement("div")
    val instance = ReactDOM.render(
      comp,
      target
    ).asInstanceOf[TestComponent.Def]

    assert(target.childNodes(0).asInstanceOf[Element] == ReactDOM.findDOMNode(instance))
  }
}
