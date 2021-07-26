package slinky.core

import slinky.core.facade.{React, ReactElement}
import slinky.web.ReactDOM

import scala.scalajs.js
import org.scalajs.dom.document

import org.scalatest.funsuite.AnyFunSuite

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

object TestExportedExternalComponent extends ExternalComponentNoProps {
  case class Props(children: Seq[ReactElement])
  val component = "div"
}

class ExportedComponentTest extends AnyFunSuite {
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

  // test("Can construct an instance of an exported functional component with JS-provided props") {
  //   case class FunctionalProps(name: String)
  //   val TestExportedFunctionalComponent = FunctionalComponent((p: FunctionalProps) => {
  //     p.name
  //   })

  //   val container = document.createElement("div")
  //   ReactDOM.render(React.createElement(
  //     TestExportedFunctionalComponent: ReactComponentClass[_],
  //     js.Dictionary(
  //       "name" -> "lol"
  //     )
  //   ), container)

  //   assert(container.innerHTML == "lol")
  // }

  test("Can construct an instance of an exported external component with JS-provided props") {
    val container = document.createElement("div")
    ReactDOM.render(React.createElement(
      TestExportedExternalComponent: ReactComponentClass[_],
      js.Dictionary(
        "children" -> js.Array("hello")
      )
    ), container)

    assert(container.innerHTML == "<div>hello</div>")
  }
}
