package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, PrivateComponentClass, React}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

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

  def apply(p: Props, key: String = null, ref: Def => Unit = null)(implicit constructorTag: ConstructorTag[Def], propsWriter: Writer[Props]): ComponentInstance = {
    val propsObj = js.Dynamic.literal(__ = p.asInstanceOf[js.Any])

    if (key != null) {
      propsObj.asInstanceOf[js.Dynamic].key = key
    }

    if (ref != null) {
      propsObj.asInstanceOf[js.Dynamic].ref = ref: js.Function1[Def, Unit]
    }

    React.createElement(componentConstructor, propsObj)
  }
}

object BaseComponent {
  private var componentConstructorMiddleware = (constructor: js.Object, componentObject: js.Object) => {
    constructor
  }

  def insertMiddleware(w: (js.Object, js.Object) => js.Object) = {
    val orig = componentConstructorMiddleware
    componentConstructorMiddleware = (constructor: js.Object, componentObject: js.Object) => {
      w(orig(constructor, componentObject), componentObject)
    }
  }
}
