package me.shadaj.slinky.core

import org.scalatest.FunSuite

import me.shadaj.slinky.web.html._

object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component = "div"
}

object ExternalSimpleWithTagMods extends ExternalComponentWithTagMods {
  override type Props = Unit
  override type Element = div.tag.type

  override val component = "div"
}

class ExternalComponentTest extends FunSuite {
  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    div(
      ExternalSimple(())
    )

    assert(true)
  }

  test("Implicit macro to shortcut ExternalComponentWithTagMods can be invoked") {
    div(
      ExternalSimpleWithTagMods(())
    )

    assert(true)
  }
}
