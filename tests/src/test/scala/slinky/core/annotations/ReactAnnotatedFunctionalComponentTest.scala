package slinky.core.annotations

import slinky.core.FunctionalComponent
import slinky.core.facade.ReactElement

import slinky.web.ReactDOM
import slinky.web.html.div

import org.scalajs.dom
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.Promise
import scala.scalajs.js

@react object SimpleFunctionalComponent {
  case class Props[T](in: Seq[T])

  val component = FunctionalComponent[Props[_]] { case Props(in) =>
    in.mkString(" ")
  }
}

@react object FunctionalComponentJustReExpose {
  val component = FunctionalComponent[Int] { in =>
    in.toString
  }
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
}
