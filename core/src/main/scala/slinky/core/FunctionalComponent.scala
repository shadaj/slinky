package slinky.core

import slinky.readwrite.Reader
import slinky.core.facade.{React, ReactRaw, ReactElement, ReactRef}
import scala.scalajs.js

import scala.reflect.macros.whitebox
import scala.language.experimental.macros

import scala.language.implicitConversions

final class KeyAddingStage(private val args: js.Array[js.Any]) extends AnyVal {
  def withKey(key: String): ReactElement = {
    args(1).asInstanceOf[js.Dictionary[js.Any]]("key") = key
    KeyAddingStage.build(this)
  }
}

object KeyAddingStage {
  implicit def build(stage: KeyAddingStage): ReactElement = {
    ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, stage.args).asInstanceOf[ReactElement]
  }
}

final class FunctionalComponent[P] private[core](private[core] val component: js.Object) extends AnyVal {
  type Props = P
  type Result = KeyAddingStage

  private[core] def componentWithReader(propsReader: Reader[P]) = {
    component.asInstanceOf[js.Dynamic].__propsReader = propsReader.asInstanceOf[js.Object]
    component
  }

  def apply(props: P): KeyAddingStage = {
    new KeyAddingStage(js.Array(component, js.Dynamic.literal(
      __ = props.asInstanceOf[js.Any]
    )))
  }
}

final class FunctionalComponentTakingRef[P, R] private[core](private[core] val component: js.Object) extends AnyVal {
  type Props = P
  type Result = KeyAddingStage

  private[core] def componentWithReader(propsReader: Reader[P]) = {
    component.asInstanceOf[js.Dynamic].__propsReader = propsReader.asInstanceOf[js.Object]
    component
  }

  def apply(props: P): KeyAddingStage = {
    new KeyAddingStage(js.Array(component, js.Dynamic.literal(
      __ = props.asInstanceOf[js.Any]
    )))
  }
}

final class FunctionalComponentForwardedRef[P, R] private[core](private[core] val component: js.Object) extends AnyVal {
  type Props = P
  type Result = KeyAndRefAddingStage[R]

  private[core] def componentWithReader(propsReader: Reader[P]) = {
    component.asInstanceOf[js.Dynamic].__propsReader = propsReader.asInstanceOf[js.Object]
    component
  }

  def apply(props: P): KeyAndRefAddingStage[R] = {
    new KeyAndRefAddingStage[R](js.Array(
      component,
      js.Dynamic.literal(
        __ = props.asInstanceOf[js.Any]
      )
    ))
  }
}

object FunctionalComponent {
  def apply[P](fn: P => ReactElement)(implicit name: FunctionalComponentName) = new FunctionalComponent[P]({
    var ret: js.Function1[js.Object, ReactElement] = null
    ret = ((obj: js.Object) => {
      if (obj.hasOwnProperty("__")) {
        fn(obj.asInstanceOf[js.Dynamic].__.asInstanceOf[P])
      } else {
        fn(ret.asInstanceOf[js.Dynamic].__propsReader.asInstanceOf[Reader[P]].read(obj))
      }
    })

    if (!scala.scalajs.LinkingInfo.productionMode) {
      ret.asInstanceOf[js.Dynamic].displayName = name.name
    }

    ret
  }.asInstanceOf[js.Object])

  def apply[P, R](fn: (P, ReactRef[R]) => ReactElement)(implicit name: FunctionalComponentName) = new FunctionalComponentTakingRef[P, R]({
    var ret: js.Function2[js.Object, ReactRef[R], ReactElement] = null
    ret = ((obj: js.Object, ref: ReactRef[R]) => {
      if (obj.hasOwnProperty("__")) {
        fn(obj.asInstanceOf[js.Dynamic].__.asInstanceOf[P], ref)
      } else {
        fn(ret.asInstanceOf[js.Dynamic].__propsReader.asInstanceOf[Reader[P]].read(obj), ref)
      }
    })

    if (!scala.scalajs.LinkingInfo.productionMode) {
      ret.asInstanceOf[js.Dynamic].displayName = name.name
    }

    ret
  }.asInstanceOf[js.Object])
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
      name == "<init>" || (name.startsWith("<local ") && name.endsWith(">")) || name == "component"
    }

    def findNonSyntheticOwner(current: Symbol): Symbol = {
      if (isSyntheticName(current.name.decodedName.toString.trim)) {
        findNonSyntheticOwner(current.owner)
      } else {
        current
      }
    }

    c.Expr(q"new _root_.slinky.core.FunctionalComponentName(${findNonSyntheticOwner(c.internal.enclosingOwner).name.decodedName.toString})")
  }
}
