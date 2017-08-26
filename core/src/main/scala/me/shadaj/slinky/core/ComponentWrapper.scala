package me.shadaj.slinky.core

abstract class ComponentWrapper extends BaseComponentWrapper {
  override type Definition = DefinitionBase[Props, State]
}
