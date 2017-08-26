package me.shadaj.slinky.core

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
abstract class StatelessDefinition[Props](jsProps: js.Object)
                                         (implicit propsReader: Reader[Props],
                                          propsWriter: Writer[Props]) extends DefinitionBase[Props, Unit](jsProps) {
  override def initialState: Unit = ()
}

abstract class StatelessComponentWrapper extends BaseComponentWrapper {
  override type State = Unit

  override type Definition = StatelessDefinition[Props]
}
