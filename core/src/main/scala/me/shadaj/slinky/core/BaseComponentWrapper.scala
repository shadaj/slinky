package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.language.implicitConversions

class KeyAndRefAddingStage[D <: js.Any](val props: js.Dictionary[js.Any], val constructor: js.Object) {
  def withKey(key: String): KeyAndRefAddingStage[D] = {
    props("key") = key
    new KeyAndRefAddingStage[D](props, constructor)
  }

  def withRef(ref: js.Object => Unit): KeyAndRefAddingStage[D] = {
    props("ref") = ref
    new KeyAndRefAddingStage[D](props, constructor)
  }
}

object KeyAndRefAddingStage {
  implicit def build[D <: js.Any](stage: KeyAndRefAddingStage[D]): ReactElement = {
    React.createElement(stage.constructor, stage.props)
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

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    new KeyAndRefAddingStage(propsObj, componentConstructor)
  }
}

object BaseComponentWrapper {
  implicit def proplessKeyAndRef[C <: BaseComponentWrapper { type Props = Unit }](c: C)(implicit constructorTag: ConstructorTag[c.Def]): KeyAndRefAddingStage[c.Def] = {
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
