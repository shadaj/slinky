package slinky.core

import org.scalajs.dom.document
import slinky.core.facade.{React, ReactElement}
import slinky.web.ReactDOM

import org.scalatest.funsuite.AnyFunSuite

class FunctionalComponentTest extends AnyFunSuite {
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

  test("Re-rendering a memoed component with matching comparison works") {
    val container = document.createElement("div")
    var renderCount = 0
    case class Props(a: Int, ignore: Int)
    val component = React.memo(FunctionalComponent[Props] { props =>
      renderCount += 1
      props.a.toString
    }, (oldProps: Props, newProps: Props) => oldProps.a == newProps.a)

    val inProps = Props(1, 2)
    ReactDOM.render(component(inProps), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)

    ReactDOM.render(component(inProps.copy(ignore = 3)), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)
  }

  test("Re-rendering a memoed component with non-matching comparison works") {
    val container = document.createElement("div")
    var renderCount = 0
    case class Props(a: Int)
    val component = React.memo(FunctionalComponent[Props] { props =>
      renderCount += 1
      props.a.toString
    }, (oldProps: Props, newProps: Props) => oldProps.a == newProps.a)

    val inProps = Props(1)
    ReactDOM.render(component(inProps), container)
    assert(container.innerHTML == "1")
    assert(renderCount == 1)

    ReactDOM.render(component(inProps.copy(a = 2)), container)
    assert(container.innerHTML == "2")
    assert(renderCount == 2)
  }

  test("Cannot reuse half-built functional component") {
    val component = FunctionalComponent[Int](_.toString)
    val halfBuilt = component(1)
    halfBuilt.withKey("abc"): ReactElement

    assertThrows[IllegalStateException] {
      halfBuilt.withKey("abc2"): ReactElement
    }
  }
}
