package slinky.core

import slinky.readwrite.Reader
import slinky.core.facade.{ReactElement, ReactRaw, ReactRef}
import scala.scalajs.js

final class KeyAddingStage(private val args: js.Array[js.Any]) extends AnyVal {
  @inline def withKey(key: String): ReactElement = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("key") = key
    KeyAddingStage.build(this)
  }
}

object KeyAddingStage {
  @inline implicit def build(stage: KeyAddingStage): ReactElement = {
    if (stage.args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, stage.args)
      .asInstanceOf[ReactElement]

    stage.args(0) = null

    ret
  }

  @inline implicit def buildContainer[F[_]](
    stage: F[KeyAddingStage]
  )(implicit f: ReactElementContainer[F]): F[ReactElement] =
    f.map(stage)(build)
}

trait FunctionalComponentCore[P, R, S] extends Any { self: S =>
  type Props  = P
  type Result = R

  private[core] def component: js.Function

  private[core] def componentWithReader(propsReader: Reader[P]) = {
    component.asInstanceOf[js.Dynamic].__propsReader = propsReader.asInstanceOf[js.Object]
    component
  }

  private[core] def makeAnother(underlying: js.Function): S

  @inline def apply(props: P): R
}

final class FunctionalComponent[P] private[core] (private[core] val component: js.Function)
    extends AnyVal
    with FunctionalComponentCore[P, KeyAddingStage, FunctionalComponent[P]] {
  @inline def apply(props: P): KeyAddingStage =
    new KeyAddingStage(
      js.Array(
        component,
        js.Dynamic.literal(
          __ = props.asInstanceOf[js.Any]
        )
      )
    )

  private[core] def makeAnother(underlying: js.Function): FunctionalComponent[P] =
    new FunctionalComponent[P](underlying)
}

final class FunctionalComponentTakingRef[P, R] private[core] (private[core] val component: js.Function)
    extends AnyVal
    with FunctionalComponentCore[P, KeyAddingStage, FunctionalComponentTakingRef[P, R]] {
  @inline def apply(props: P): KeyAddingStage =
    new KeyAddingStage(
      js.Array(
        component,
        js.Dynamic.literal(
          __ = props.asInstanceOf[js.Any]
        )
      )
    )

  private[core] def makeAnother(underlying: js.Function): FunctionalComponentTakingRef[P, R] =
    new FunctionalComponentTakingRef[P, R](underlying)
}

final class FunctionalComponentForwardedRef[P, R] private[core] (private[core] val component: js.Function)
    extends AnyVal
    with FunctionalComponentCore[P, KeyAndRefAddingStage[R], FunctionalComponentForwardedRef[P, R]] {
  @inline def apply(props: P): KeyAndRefAddingStage[R] =
    new KeyAndRefAddingStage[R](
      js.Array(
        component,
        js.Dynamic.literal(
          __ = props.asInstanceOf[js.Any]
        )
      )
    )

  private[core] def makeAnother(underlying: js.Function): FunctionalComponentForwardedRef[P, R] =
    new FunctionalComponentForwardedRef[P, R](underlying)
}

object FunctionalComponent {
  def apply[P](fn: P => ReactElement)(implicit name: FunctionalComponentName) =
    new FunctionalComponent[P]({
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
    })

  def apply[P, R](fn: (P, ReactRef[R]) => ReactElement)(implicit name: FunctionalComponentName) =
    new FunctionalComponentTakingRef[P, R]({
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
    })
}
