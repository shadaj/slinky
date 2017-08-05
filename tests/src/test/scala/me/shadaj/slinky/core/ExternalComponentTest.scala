package me.shadaj.slinky.core

import org.scalatest.FunSuite

import scala.scalajs.js

import facade.ReactElement

object ExternalSimple extends ExternalComponent {
  override type Props = Unit
  override val component = null
}

object ExternalSimpleWithTagMods extends ExternalComponentWithTagMods {
  override type Props = Unit
  override type Element = Nothing

  override val component = null
}

class ExternalComponentTest extends FunSuite {
  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    assertCompiles(
      """ExternalSimple(()): ReactElement"""
    )
  }

  test("Implicit macro to shortcut ExternalComponentWithTagMods can be invoked") {
    assertCompiles(
      """ExternalSimpleWithTagMods(()): ReactElement"""
    )
  }
}
