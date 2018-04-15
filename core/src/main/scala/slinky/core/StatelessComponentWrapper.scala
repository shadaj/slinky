package slinky.core

import scala.scalajs.js

abstract class StatelessDefinition[Props, Snapshot](jsProps: js.Object) extends DefinitionBase[Props, Unit, Snapshot](jsProps) {
  override def initialState: Unit = ()
}

abstract class StatelessComponentWrapper(implicit pr: PropsReaderProvider, pw: PropsWriterProvider, sr: StateReaderProvider, sw: StateWriterProvider) extends BaseComponentWrapper(pr, pw, sr, sw) {
  override type State = Unit

  override type Definition = StatelessDefinition[Props, Snapshot]
}
