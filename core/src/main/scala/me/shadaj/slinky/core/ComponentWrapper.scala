package me.shadaj.slinky.core

import me.shadaj.slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.language.implicitConversions

abstract class ComponentWrapper extends BaseComponentWrapper {
  override type Definition = DefinitionBase[Props, State]
}

@js.native
trait ReactComponentClass extends js.Object

object ReactComponentClass {
  implicit def wrapperToClass[T <: ComponentWrapper](wrapper: T)
                                                    (implicit propsReader: Reader[wrapper.Props],
                                                     propsWriter: Writer[wrapper.Props],
                                                     stateReader: Reader[wrapper.State],
                                                     stateWriter: Writer[wrapper.State],
                                                     ctag: ConstructorTag[wrapper.Def]): ReactComponentClass = {
    wrapper.componentConstructor.asInstanceOf[ReactComponentClass]
  }
}