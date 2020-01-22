package slinky.core

import slinky.readwrite.Reader

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag

@js.native
trait ReactComponentClass[P] extends js.Object

object ReactComponentClass {
  implicit def wrapperToClass[T <: BaseComponentWrapper](wrapper: T)
                                                        (implicit propsReader: Reader[wrapper.Props],
                                                         ctag: ConstructorTag[wrapper.Def]): ReactComponentClass[wrapper.Props] = {
    wrapper.componentConstructor(propsReader, wrapper.hot_stateWriter, wrapper.hot_stateReader, ctag).asInstanceOf[ReactComponentClass[wrapper.Props]]
  }

  implicit def functionalComponentToClass[P](component: FunctionalComponent[P])(implicit propsReader: Reader[P]): ReactComponentClass[P] = {
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]
  }

  implicit def functionalComponentTakingRefToClass[P, R <: js.Any](component: FunctionalComponentTakingRef[P, R])(implicit propsReader: Reader[P]): ReactComponentClass[P] = {
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]
  }

  implicit def functionalComponentForwardedRefToClass[P, R <: js.Any](component: FunctionalComponentForwardedRef[P, R])(implicit propsReader: Reader[P]): ReactComponentClass[P] = {
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]
  }
}
