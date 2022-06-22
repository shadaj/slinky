package slinky.core

import slinky.readwrite.Reader

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import slinky.readwrite.Writer
import slinky.core.facade.React
import slinky.core.facade.ReactElement

@js.native
trait ReactComponentClass[P] extends js.Object

object ReactComponentClass {
  implicit class RichReactComponentClass[P: Writer](val c: ReactComponentClass[P]) {
    @inline def apply(props: P): ReactElement =
      React.createElement(c, implicitly[Writer[P]].write(props).asInstanceOf[js.Dictionary[js.Any]])
  }

  implicit def wrapperToClass[T <: BaseComponentWrapper](wrapper: T)(
    implicit propsReader: Reader[wrapper.Props],
    ctag: ConstructorTag[wrapper.Def]
  ): ReactComponentClass[wrapper.Props] =
    wrapper
      .componentConstructor(propsReader, wrapper.hot_stateWriter, wrapper.hot_stateReader, ctag)
      .asInstanceOf[ReactComponentClass[wrapper.Props]]

  implicit def externalToClass(
    external: ExternalComponentWithAttributesWithRefType[_, _]
  ): ReactComponentClass[external.Props] =
    external.component
      .asInstanceOf[ReactComponentClass[external.Props]]

  implicit def externalNoPropsToClass(
    external: ExternalComponentNoPropsWithAttributesWithRefType[_, _]
  ): ReactComponentClass[Unit] =
    external.component.asInstanceOf[ReactComponentClass[Unit]]

  implicit def functionalComponentToClass[P](
    component: FunctionalComponent[P]
  )(implicit propsReader: Reader[P]): ReactComponentClass[P] =
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]

  implicit def functionalComponentTakingRefToClass[P, R <: js.Any](
    component: FunctionalComponentTakingRef[P, R]
  )(implicit propsReader: Reader[P]): ReactComponentClass[P] =
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]

  implicit def functionalComponentForwardedRefToClass[P, R <: js.Any](
    component: FunctionalComponentForwardedRef[P, R]
  )(implicit propsReader: Reader[P]): ReactComponentClass[P] =
    component.componentWithReader(propsReader).asInstanceOf[ReactComponentClass[P]]
}
