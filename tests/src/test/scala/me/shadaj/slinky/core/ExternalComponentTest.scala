package me.shadaj.slinky.core

import org.scalatest.FunSuite

import me.shadaj.slinky.web.html._

object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component = "div"
}

object ExternalSimpleWithAttributes extends ExternalComponentWithAttributes[div.tag.type] {
  override type Props = Unit

  override val component = "div"
}

class ExternalComponentTest extends FunSuite {
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
