package me.shadaj.slinky.core

import org.scalatest.FunSuite

import me.shadaj.slinky.web.html._

object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component = "div"
}

object ExternalSimpleWithTagMods extends ExternalComponentWithTagMods[div.tag.type] {
  override type Props = Unit

  override val component = "div"
}

class ExternalComponentTest extends FunSuite {
  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    assertCompiles("""div(ExternalSimple())""")
  }

  test("Implicit macro to shortcut ExternalComponentWithTagMods can be invoked") {
    assertCompiles("""div(ExternalSimpleWithTagMods())""")
  }

  test("Can construct an external component taking Unit props with no arguments") {
    assertCompiles("""ExternalSimple()""")
  }

  test("Can construct an external component taking Unit props and tag mods with no arguments") {
    assertCompiles("""ExternalSimpleWithTagMods(className := "hi")""")
  }
}
