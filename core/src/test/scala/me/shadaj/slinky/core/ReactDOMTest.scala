package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, ReactDOM}
import me.shadaj.slinky.core.html._
import org.scalatest.FunSuite
import org.scalajs.dom.{Element, document}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

object TestComponent extends Component {
  type Props = Unit
  type State = Unit

  @ScalaJSDefined
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Unit = ()

    override def render(): ComponentInstance = {
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
    val comp: ComponentInstance = TestComponent(())
    val target = document.createElement("div")
    val instance = ReactDOM.render(
      comp,
      target
    )

    assert(target.childNodes(0).asInstanceOf[Element] == ReactDOM.findDOMNode(instance))
  }
}
