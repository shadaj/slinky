package slinky.core

import slinky.core.facade.{React, ReactElement}
import scala.scalajs.js

import scala.reflect.macros.whitebox
import scala.language.experimental.macros

import scala.language.implicitConversions

class KeyAddingStage(private[KeyAddingStage] val props: js.Dictionary[js.Any],
                     private[KeyAddingStage] val constructor: js.Object) {
  def withKey(key: String): ReactElement = {
    props("key") = key
    KeyAddingStage.build(this)
  }
}

object KeyAddingStage {
  implicit def build[D <: js.Any](stage: KeyAddingStage): ReactElement = {
    React.createElement(stage.constructor, stage.props)
  }
}

class FunctionalComponent[P](fn: P => ReactElement)(implicit fnCompName: FunctionalComponentName) {
  private val component = ((obj: js.Object) => {
    fn(obj.asInstanceOf[js.Dynamic].__.asInstanceOf[P])
  }): js.Function1[js.Object, ReactElement]

  component.asInstanceOf[js.Dynamic].displayName = fnCompName.name

  final def apply(props: P): KeyAddingStage = {
    new KeyAddingStage(js.Dynamic.literal(
      __ = props.asInstanceOf[js.Any]
    ).asInstanceOf[js.Dictionary[js.Any]], component.asInstanceOf[js.Object])
  }
}

object FunctionalComponent {
  def apply[P](fn: P => ReactElement)(implicit name: FunctionalComponentName) = new FunctionalComponent[P](fn)
}

final class FunctionalComponentName(val name: String) extends AnyVal
object FunctionalComponentName {
  implicit def get: FunctionalComponentName = macro FunctionalComponentNameMacros.impl
}

object FunctionalComponentNameMacros {
  def impl(c: whitebox.Context): c.Expr[FunctionalComponentName] = {
    import c.universe._
    
    // from lihaoyi/sourcecode
    def isSyntheticName(name: String) = {
      name == "<init>" || (name.startsWith("<local ") && name.endsWith(">"))
    }  

    def findNonSyntheticOwner(current: Symbol): Symbol = {
      if (isSyntheticName(current.name.decodedName.toString)) {
        findNonSyntheticOwner(current.owner)
      } else current
    }

    c.Expr(q"new _root_.slinky.core.FunctionalComponentName(${findNonSyntheticOwner(c.internal.enclosingOwner).name.decodedName.toString})")
  }
}
