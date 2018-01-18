package me.shadaj.slinky.core

import scala.language.implicitConversions

abstract class ComponentWrapper(implicit pr: PropsReaderProvider, pw: PropsWriterProvider, sr: StateReaderProvider, sw: StateWriterProvider) extends BaseComponentWrapper(pr, pw, sr, sw) {
  override type Definition = DefinitionBase[Props, State]
}
