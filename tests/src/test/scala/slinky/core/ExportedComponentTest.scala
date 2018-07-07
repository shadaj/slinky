package slinky.core

import slinky.core.facade.{React, ReactElement}
import slinky.web.ReactDOM
import org.scalatest.FunSuite

import scala.scalajs.js
import org.scalajs.dom.document

object TestExportedComponentWithState extends ComponentWrapper {
  case class Props(name: String)
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 1

    override def render(): ReactElement = {
      s"${props.name} $state"
    }
  }
}

object TestExportedComponentStateless extends StatelessComponentWrapper {
  case class Props(name: String)

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def render(): ReactElement = {
      s"${props.name}"
    }
  }
}

class ExportedComponentTest extends FunSuite {
  test("Can construct an instance of an exported component with JS-provided props") {
    val container = document.createElement("div")
    ReactDOM.render(React.createElement(
      TestExportedComponentWithState: ReactComponentClass[_],
      js.Dictionary(
        "name" -> "lol"
      )
    ), container)

    assert(container.innerHTML == "lol 1")
  }

  test("Can construct an instance of a stateless exported component with JS-provided props") {
    val container = document.createElement("div")
    ReactDOM.render(React.createElement(
      TestExportedComponentStateless: ReactComponentClass[_],
      js.Dictionary(
        "name" -> "lol"
      )
    ), container)

    assert(container.innerHTML == "lol")
  }
}
