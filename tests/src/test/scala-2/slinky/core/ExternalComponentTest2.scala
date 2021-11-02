package slinky.core

import slinky.web.html.div
import slinky.core.annotations.react
import scala.scalajs.js

import org.scalatest.funsuite.AnyFunSuite

@react object ExternalSimpleWithProps2 extends ExternalComponent {
  case class Props(a: Int)
  override val component = "div"
}

@react object ExternalDivWithAllDefaulted2 extends ExternalComponent {
  case class Props(id: String = "foo")
  override val component = "div"
}

class ExternalComponentTest2 extends AnyFunSuite {
  test("Can construct an external component with generated apply") {
    div(ExternalSimpleWithProps2(a = 1))
  }

  test("Can construct an external component with default parameters") {
    div(ExternalDivWithAllDefaulted2())
  }
}