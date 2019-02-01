package slinky.core

import slinky.readwrite.Reader
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

class FunctionalComponent[P] private[core](private[core] val component: js.Object) {
  type Props = P
  
  def this(fn: P => ReactElement)(implicit fnCompName: FunctionalComponentName) = {
    this({
      var ret: js.Function1[js.Object, ReactElement] = null
      ret = ((obj: js.Object) => {
        if (obj.hasOwnProperty("__")) {
          fn(obj.asInstanceOf[js.Dynamic].__.asInstanceOf[P])
        } else {
          fn(ret.asInstanceOf[js.Dynamic].__propsReader.asInstanceOf[Reader[P]].read(obj))
        }
      })

      if (!scala.scalajs.LinkingInfo.productionMode) {
        ret.asInstanceOf[js.Dynamic].displayName = fnCompName.name
      }

      ret
    }.asInstanceOf[js.Object])
  }

  private[core] def componentWithReader(propsReader: Reader[P]) = {
    component.asInstanceOf[js.Dynamic].__propsReader = propsReader.asInstanceOf[js.Object]
    component
  }

  final def apply(props: P): KeyAddingStage = {
    new KeyAddingStage(js.Dynamic.literal(
      __ = props.asInstanceOf[js.Any]
    ).asInstanceOf[js.Dictionary[js.Any]], component)
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
