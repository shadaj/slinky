package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag

import scala.language.implicitConversions

class KeyAndRefAddingStage[D <: js.Any](props: js.Dictionary[js.Any], val c: BaseComponentWrapper) {
  def apply(key: String)(implicit constructorTag: ConstructorTag[D]): ReactElement = apply(key, null)(constructorTag)
  def apply(ref: js.Object => Unit)(implicit constructorTag: ConstructorTag[D]): ReactElement = apply(null, ref)(constructorTag)
  def apply(key: String, ref: js.Object => Unit)(implicit constructorTag: ConstructorTag[D]): ReactElement = {
    if (key != null) {
      props("key") = key
    }

    if (ref != null) {
      props("ref") = ref: js.Function1[_, Unit]
    }

    React.createElement(c.componentConstructor(constructorTag.asInstanceOf[ConstructorTag[c.Def]]), props)
  }
}

object KeyAndRefAddingStage {
  implicit def shortcutToElement[D <: js.Any](stage: KeyAndRefAddingStage[D])
                                (implicit constructorTag: ConstructorTag[D]): ReactElement = {
    stage.apply(null, null)(constructorTag)
  }
}

abstract class BaseComponentWrapper {
  type Props

  type State

  type Def <: Definition

  type Definition <: js.Object

  def componentConstructor(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    constructor.displayName = getClass.getSimpleName
    BaseComponentWrapper.componentConstructorMiddleware(
      constructor.asInstanceOf[js.Object], this.asInstanceOf[js.Object])
  }

  def apply(p: Props): KeyAndRefAddingStage[Def] = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    new KeyAndRefAddingStage(propsObj, this)
  }
}

object BaseComponentWrapper {
  implicit def proplessKeyAndRef[C <: BaseComponentWrapper { type Props = Unit }](c: C): KeyAndRefAddingStage[c.Def] = {
    c.apply(())
  }

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
