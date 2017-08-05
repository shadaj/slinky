package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ReactElement, React}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag

abstract class BaseComponent {
  type Props

  type State

  type Def <: Definition

  type Definition <: js.Object

  def componentConstructor(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    constructor.displayName = getClass.getSimpleName
    BaseComponent.componentConstructorMiddleware(
      constructor.asInstanceOf[js.Object], this.asInstanceOf[js.Object])
  }

  def apply(p: Props, key: String = null, ref: Def => Unit = null)(implicit constructorTag: ConstructorTag[Def], propsWriter: Writer[Props]): ReactElement = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    if (key != null) {
      propsObj("key") = key
    }

    if (ref != null) {
      propsObj("ref") = ref: js.Function1[Def, Unit]
    }

    React.createElement(componentConstructor, propsObj)
  }
}

object BaseComponent {
  private var componentConstructorMiddleware = (constructor: js.Object, _: js.Object) => {
    constructor
  }

  /**
    * Inserts a component constructor middleware function, which transforms a component constructor
    * given the original constructor and the outer component object (for state tracking)
    *
    * This is used for hot-loading, which wraps the component constructor inside a proxy component
    *
    * @param middleware the middleware function to use
    */
  def insertMiddleware(middleware: (js.Object, js.Object) => js.Object): Unit = {
    val orig = componentConstructorMiddleware
    componentConstructorMiddleware = (constructor: js.Object, componentObject: js.Object) => {
      middleware(orig(constructor, componentObject), componentObject)
    }
  }
}
