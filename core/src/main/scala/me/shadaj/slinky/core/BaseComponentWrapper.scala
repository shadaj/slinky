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
    constructor.__fullName = getClass.getName
    constructor.asInstanceOf[js.Object]
  }

  def apply(p: Props)(implicit propsWriter: Writer[Props], constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    new KeyAndRefAddingStage(propsObj, componentConstructor)
  }
}

object BaseComponentWrapper {
  implicit def proplessKeyAndRef[C <: BaseComponentWrapper { type Props = Unit }](c: C)(implicit constructorTag: ConstructorTag[c.Def]): KeyAndRefAddingStage[c.Def] = {
    c.apply(())
  }

  private[core] var getWrittenInitialStateMiddleware: Option[String => Option[js.Object]] = None
  private[core] var writtenStateMiddleware: Option[(String, () => js.Object) => Unit] = None


  def insertGetInitialWrittenStateMiddleware(fn: String => Option[js.Object]) = {
    getWrittenInitialStateMiddleware = Some(fn)
  }

  def insertWrittenStateMiddleware(fn: (String, () => js.Object) => Unit) = {
    writtenStateMiddleware = Some(fn)
  }
}
