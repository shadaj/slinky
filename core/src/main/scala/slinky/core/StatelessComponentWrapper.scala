package slinky.core

import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js

abstract class StatelessDefinition[Props, Snapshot](jsProps: js.Object) extends DefinitionBase[Props, Unit, Snapshot](jsProps) {
  override def initialState: Unit = ()
}

abstract class StatelessComponentWrapper(implicit pr: PropsReaderProvider, pw: PropsWriterProvider) extends BaseComponentWrapper(
  pr, pw,
  Reader.unitReader.asInstanceOf[StateReaderProvider],
  Writer.unitWriter.asInstanceOf[StateWriterProvider]
) {
  override type State = Unit

  override type Definition = StatelessDefinition[Props, Snapshot]
}
