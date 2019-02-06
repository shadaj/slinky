package slinky.core

import org.scalatest.FunSuite
import org.scalajs.dom.document

import slinky.core.facade.React
import slinky.web.ReactDOM

class FunctionalComponentTest extends FunSuite {
  test("Can render a functional component") {
    val container = document.createElement("div")
    val component = FunctionalComponent[Int](_.toString)
    ReactDOM.render(component(1), container)

    assert(container.innerHTML == "1")
  }

  test("Re-rendering a memoed component with same props works") {
    val container = document.createElement("div")
    var renderCount = 0
    case class Props(a: Int)
    val component = React.memo(FunctionalComponent[Props] { props =>
      renderCount += 1
      props.a.toString
    })

    val inProps = Props(1)
    ReactDOM.render(component(inProps), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)

    ReactDOM.render(component(inProps), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)
  }

  test("Re-rendering a memoed component with different props works") {
    val container = document.createElement("div")
    var renderCount = 0
    case class Props(a: Int)
    val component = React.memo(FunctionalComponent[Props] { props =>
      renderCount += 1
      props.a.toString
    })

    val inProps = Props(1)
    ReactDOM.render(component(inProps), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)

    ReactDOM.render(component(inProps.copy(a = 2)), container)
    assert(container.innerHTML == "2")
    assert(renderCount == 2)
  }
}
