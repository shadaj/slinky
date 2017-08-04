package me.shadaj.slinky.core

abstract class Component extends BaseComponent {
  override type Definition = DefinitionBase[Props, State]
}
