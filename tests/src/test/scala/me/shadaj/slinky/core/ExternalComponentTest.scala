package me.shadaj.slinky.core

import org.scalatest.FunSuite

import scala.scalajs.js

import facade.ComponentInstance

object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component: js.Object = null
}

object ExternalSimpleWithTagMods extends ExternalComponentWithTagMods {
  override type Props = Unit
  override type Element = Nothing

  override val component: js.Object = null
}

class ExternalComponentTest extends FunSuite {
  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    assertCompiles(
      """ExternalSimple(()): ComponentInstance"""
    )
  }

  test("Implicit macro to shortcut ExternalComponentWithTagMods can be invoked") {
    assertCompiles(
      """ExternalSimpleWithTagMods(()): ComponentInstance"""
    )
  }
}
