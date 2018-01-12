package me.shadaj.slinky.core

import me.shadaj.slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.language.implicitConversions

abstract class ComponentWrapper(implicit pr: PropsReaderProvider, pw: PropsWriterProvider, sr: StateReaderProvider, sw: StateWriterProvider) extends BaseComponentWrapper(pr, pw, sr, sw) {
  override type Definition = DefinitionBase[Props, State]
}

@js.native
trait ReactComponentClass extends js.Object

object ReactComponentClass {
  implicit def wrapperToClass[T <: ComponentWrapper](wrapper: T)
                                                    (implicit propsWriter: Writer[T#Props], propsReader: Reader[T#Props],
                                                     stateWriter: Writer[T#State], stateReader: Reader[T#State],
                                                     ctag: ConstructorTag[wrapper.Def]): ReactComponentClass = {
    wrapper.componentConstructor.asInstanceOf[ReactComponentClass]
  }
}