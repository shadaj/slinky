package slinky.core.annotations

import slinky.core.FunctionalComponent
import slinky.core.facade.ReactElement
import slinky.web.ReactDOM

import org.scalajs.dom
import org.scalatest.funsuite.AsyncFunSuite

@react object SimpleFunctionalComponent {
  case class Props[T](in: Seq[T])

  val component = FunctionalComponent[Props[_]] {
    case Props(in) =>
      in.mkString(" ")
  }
}

@react object FunctionalComponentJustReExpose {
  val component = FunctionalComponent[Int](in => in.toString)
}

@react object FunctionalComponentWithPrivateValComponent {
  private val component = FunctionalComponent[Int](in => in.toString)
}

@react object FunctionalComponentWithProtectedValComponent {
  protected val component = FunctionalComponent[Int](in => in.toString)
}

@react object FunctionalComponentEmptyProps {
  case class Props()
  val component = FunctionalComponent[Props](_ => "test")
}

@react object FunctionalComponentUnitProps {
  type Props = Unit
  val component = FunctionalComponent[Props](_ => "test")
}

@react(expandChildren = true) object FunctionalComponentExpandChildren {
  case class Props(
    alpha: String,
    children: Seq[ReactElement]
  )
  val component = FunctionalComponent[Props](p => s"${p.alpha}: ${p.children.mkString(" ")}")
}

class ReactAnnotatedFunctionalComponentTest extends AsyncFunSuite {
  test("Simple component has generated apply") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      SimpleFunctionalComponent(in = Seq(1, 2, 3)),
      container
    )

    assert(container.innerHTML == "1 2 3")
  }

  test("Component without case class re-exports apply method") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentJustReExpose(1),
      container
    )

    assert(container.innerHTML == "1")
  }

  test("Component with private component definition works") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentWithPrivateValComponent(1),
      container
    )

    assert(container.innerHTML == "1")
  }

  test("Component with protected component definition works") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentWithProtectedValComponent(1),
      container
    )

    assert(container.innerHTML == "1")
  }

  test("Component with empty props has shortcut apply") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentEmptyProps(),
      container
    )

    assert(container.innerHTML == "test")
  }

  test("Component with unit props has shortcut apply") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentUnitProps(),
      container
    )

    assert(container.innerHTML == "test")
  }

  test("Component with expandChildren option has apply with children varargs") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      FunctionalComponentExpandChildren("alpha")(1, 2, 3),
      container
    )

    assert(container.innerHTML == "alpha: 1 2 3")
  }
}
