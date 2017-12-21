package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}
import me.shadaj.slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.language.implicitConversions

class KeyAndRefAddingStage[D <: js.Any](val props: js.Dictionary[js.Any], val constructor: js.Object) {
  def withKey(key: String): KeyAndRefAddingStage[D] = {
    props("key") = key
    new KeyAndRefAddingStage[D](props, constructor)
  }

  def withRef(ref: D => Unit): KeyAndRefAddingStage[D] = {
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

  def componentConstructor(implicit propsWriter: Writer[Props], propsReader: Reader[Props],
                           stateWriter: Writer[State], stateReader: Reader[State],
                           constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    constructor.displayName = getClass.getSimpleName
    constructor._base = this.asInstanceOf[js.Any]

    this.asInstanceOf[js.Dynamic]._propsWriter = propsWriter.asInstanceOf[js.Any]
    this.asInstanceOf[js.Dynamic]._propsReader = propsReader.asInstanceOf[js.Any]
    this.asInstanceOf[js.Dynamic]._stateWriter = stateWriter.asInstanceOf[js.Any]
    this.asInstanceOf[js.Dynamic]._stateReader = stateReader.asInstanceOf[js.Any]

    BaseComponentWrapper.componentConstructorMiddleware(
      constructor.asInstanceOf[js.Object], this.asInstanceOf[js.Object])
  }

  private var componentConstructorInstance: js.Object = null

  def apply(p: Props)(implicit propsWriter: Writer[Props], propsReader: Reader[Props], stateWriter: Writer[State], stateReader: Reader[State], constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    val propsObj = if(BaseComponentWrapper.scalaComponentWritingEnabled) {
      DefinitionBase.writeWithWrappingAdjustment(propsWriter)(p).asInstanceOf[js.Dictionary[js.Any]]
    } else js.Dictionary("__" -> p.asInstanceOf[js.Any])

    if (componentConstructorInstance == null) {
      componentConstructorInstance = componentConstructor
    }

    new KeyAndRefAddingStage(
      propsObj,
      componentConstructorInstance
    )
  }

  def apply()(implicit ev: Unit =:= Props, stateWriter: Writer[State], stateReader: Reader[State], constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    apply(())(Writer.unitWriter.asInstanceOf[Writer[Props]], Reader.unitReader.asInstanceOf[Reader[Props]], stateWriter, stateReader, constructorTag)
  }
}

object BaseComponentWrapper {
  implicit def proplessKeyAndRef[C <: BaseComponentWrapper { type Props = Unit }](c: C)(implicit stateWriter: Writer[c.State], stateReader: Reader[c.State], constructorTag: ConstructorTag[c.Def]): KeyAndRefAddingStage[c.Def] = {
    c.apply(())
  }

  private var componentConstructorMiddleware = (constructor: js.Object, _: js.Object) => {
    constructor
  }

  private[core] var scalaComponentWritingEnabled = false

  /**
    * Enables writing props and state for Scala defined components. This is
    * needed for hot loading, where data must be written to a JS object and
    * then read when the application is reloaded.
    */
  def enableScalaComponentWriting(): Unit = {
    scalaComponentWritingEnabled = true
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
