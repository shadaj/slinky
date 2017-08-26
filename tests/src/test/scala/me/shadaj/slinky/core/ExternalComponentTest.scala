package me.shadaj.slinky.core

import me.shadaj.slinky.core.annotations.react
import org.scalatest.FunSuite
import me.shadaj.slinky.web.html._

@react object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component = "div"
}

@react object ExternalSimpleWithProps extends ExternalComponent {
  case class Props(a: Int)
  override val component = "div"
}

@react object ExternalSimpleWithAttributes extends ExternalComponentWithAttributes[div.tag.type] {
  override type Props = Unit

  override val component = "div"
}

class ExternalComponentTest extends FunSuite {
  test("Can construct an external component with generated apply") {
    assertCompiles("""div(ExternalSimpleWithProps(a = 1))""")
  }

  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    assertCompiles("""div(ExternalSimple())""")
  }

  test("Implicit macro to shortcut ExternalComponentWithAttributes can be invoked") {
    assertCompiles("""div(ExternalSimpleWithAttributes())""")
  }

  test("Can construct an external component taking Unit props with no arguments") {
    assertCompiles("""ExternalSimple()""")
  }

  test("Cannot pass in attributes to a basic external component") {
    assertCompiles("""ExternalSimple(className := "hi")""")
  }

  test("Can construct an external component taking Unit props and attributes with no arguments") {
    assertCompiles("""ExternalSimpleWithAttributes(className := "hi")""")
  }

  test("Can construct an external component taking Unit props and attributes with some children") {
    assertCompiles("""ExternalSimpleWithAttributes(className := "hi")(div())""")
  }
}
