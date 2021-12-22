package slinky.core

import slinky.core.facade.{ReactElement, ReactRaw, ReactRef}
import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag

final class KeyAndRefAddingStage[D](private val args: js.Array[js.Any]) extends AnyVal {
  @inline def withKey(key: String): KeyAndRefAddingStage[D] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("key") = key
    this
  }

  @inline def withRef(ref: D => Unit): KeyAndRefAddingStage[D] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = ref
    this
  }

  @inline def withRef(ref: ReactRef[D]): KeyAndRefAddingStage[D] = {
    if (args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = ref
    this
  }
}

object KeyAndRefAddingStage {
  @inline implicit def build[D](stage: KeyAndRefAddingStage[D]): ReactElement = {
    if (stage.args(0) == null) {
      throw new IllegalStateException("This component has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, stage.args)
      .asInstanceOf[ReactElement]

    stage.args(0) = null

    ret
  }

  @inline implicit def buildContainer[D, F[_]](
    stage: F[KeyAndRefAddingStage[D]]
  )(implicit f: ReactElementContainer[F]): F[ReactElement] =
    f.map(stage)(build)
}

abstract class BaseComponentWrapper(sr: StateReaderProvider, sw: StateWriterProvider) {
  type Props

  type State

  type Snapshot

  type Def <: Definition

  type Definition <: js.Object

  val getDerivedStateFromProps: (Props, State) => State = null

  val getDerivedStateFromError: js.Error => State = null

  private[core] val hot_stateReader = sr.asInstanceOf[Reader[State]]
  private[core] val hot_stateWriter = sw.asInstanceOf[Writer[State]]

  private var patchedConstructor: js.Dynamic = null

  private def getPatchedConstructor(implicit constructorTag: ConstructorTag[Def]) = {
    if (patchedConstructor == null) {
      val constructor        = constructorTag.constructor
      val componentPrototype = constructor.prototype

      if (componentPrototype.componentWillMount == DefinitionBase.defaultBase.componentWillMount) {
        componentPrototype.componentWillMount = js.undefined
      }

      if (componentPrototype.componentDidMount == DefinitionBase.defaultBase.componentDidMount) {
        componentPrototype.componentDidMount = js.undefined
      }

      if (componentPrototype.componentWillReceiveProps != DefinitionBase.defaultBase.componentWillReceiveProps) {
        val orig = componentPrototype.componentWillReceiveProps.asInstanceOf[js.ThisFunction1[Def, Props, Unit]]
        componentPrototype.componentWillReceiveProps = ((self: Def, props: js.Object) => {
          orig(
            self,
            self.asInstanceOf[DefinitionBase[Props, _, _]].readPropsValue(props)
          )
        }): js.ThisFunction1[Def, js.Object, Unit]
      } else {
        componentPrototype.componentWillReceiveProps = js.undefined
      }

      if (componentPrototype.shouldComponentUpdate != DefinitionBase.defaultBase.shouldComponentUpdate) {
        val orig = componentPrototype.shouldComponentUpdate.asInstanceOf[js.ThisFunction2[Def, Props, State, Boolean]]
        componentPrototype.shouldComponentUpdate = ((self: Def, nextProps: js.Object, nextState: js.Object) => {
          orig(
            self,
            self.asInstanceOf[DefinitionBase[Props, _, _]].readPropsValue(nextProps),
            self.asInstanceOf[DefinitionBase[_, State, _]].readStateValue(nextState)
          )
        }): js.ThisFunction2[Def, js.Object, js.Object, Boolean]
      } else {
        componentPrototype.shouldComponentUpdate = js.undefined
      }

      if (componentPrototype.componentWillUpdate != DefinitionBase.defaultBase.componentWillUpdate) {
        val orig = componentPrototype.componentWillUpdate.asInstanceOf[js.ThisFunction2[Def, Props, State, Unit]]
        componentPrototype.componentWillUpdate = ((self: Def, nextProps: js.Object, nextState: js.Object) => {
          orig(
            self,
            self.asInstanceOf[DefinitionBase[Props, _, _]].readPropsValue(nextProps),
            self.asInstanceOf[DefinitionBase[_, State, _]].readStateValue(nextState)
          )
        }): js.ThisFunction2[Def, js.Object, js.Object, Unit]
      } else {
        componentPrototype.componentWillUpdate = js.undefined
      }

      if (componentPrototype.getSnapshotBeforeUpdate != DefinitionBase.defaultBase.getSnapshotBeforeUpdate) {
        val orig = componentPrototype.getSnapshotBeforeUpdate.asInstanceOf[js.ThisFunction2[Def, Props, State, Any]]
        componentPrototype.getSnapshotBeforeUpdate = ((self: Def, prevProps: js.Object, prevState: js.Object) => {
          orig(
            self,
            self.asInstanceOf[DefinitionBase[Props, _, _]].readPropsValue(prevProps),
            self.asInstanceOf[DefinitionBase[_, State, _]].readStateValue(prevState)
          )
        }): js.ThisFunction2[Def, js.Object, js.Object, Any]
      } else {
        componentPrototype.getSnapshotBeforeUpdate = js.undefined
      }

      if (componentPrototype.componentDidUpdate != DefinitionBase.defaultBase.componentDidUpdate) {
        val orig = componentPrototype.componentDidUpdate
        componentPrototype.componentDidUpdateScala = orig
        componentPrototype.componentDidUpdate =
          ((self: Def, prevProps: js.Object, prevState: js.Object, snapshot: js.Any) => {
            orig
              .asInstanceOf[js.ThisFunction]
              .call(
                self,
                self.asInstanceOf[DefinitionBase[Props, _, _]].readPropsValue(prevProps).asInstanceOf[js.Any],
                self.asInstanceOf[DefinitionBase[_, State, _]].readStateValue(prevState).asInstanceOf[js.Any],
                snapshot.asInstanceOf[js.Any]
              )
              .asInstanceOf[Unit]
          }): js.ThisFunction3[Def, js.Object, js.Object, js.Any, Unit]
      } else {
        componentPrototype.componentDidUpdate = js.undefined
      }

      if (componentPrototype.componentWillUnmount == DefinitionBase.defaultBase.componentWillUnmount) {
        componentPrototype.componentWillUnmount = js.undefined
      }

      if (componentPrototype.componentDidCatch == DefinitionBase.defaultBase.componentDidCatch) {
        componentPrototype.componentDidCatch = js.undefined
      }

      componentPrototype._base = this.asInstanceOf[js.Any]

      val descriptor      = js.Object.getOwnPropertyDescriptor(componentPrototype.asInstanceOf[js.Object], "initialState")
      val needsExtraApply = !js.isUndefined(descriptor) && !js.isUndefined(descriptor.asInstanceOf[js.Dynamic].writable)
      this.asInstanceOf[js.Dynamic].needsExtraApply = needsExtraApply

      if (!scala.scalajs.LinkingInfo.productionMode) {
        constructor.displayName = getClass.getSimpleName
      }

      if (this.getDerivedStateFromProps != null) {
        constructor.getDerivedStateFromProps = ((props: js.Object, state: js.Object) => {
          val propsScala =
            DefinitionBase.readValue(props, this.asInstanceOf[js.Dynamic]._propsReader.asInstanceOf[Reader[Props]])
          val stateScala =
            DefinitionBase.readValue(state, this.asInstanceOf[js.Dynamic]._stateReader.asInstanceOf[Reader[State]])

          val newState = getDerivedStateFromProps(propsScala, stateScala)

          if (newState == null) null
          else {
            if (BaseComponentWrapper.scalaComponentWritingEnabled) {
              DefinitionBase.writeWithWrappingAdjustment(
                this.asInstanceOf[js.Dynamic]._stateWriter.asInstanceOf[Writer[State]]
              )(newState)
            } else {
              js.Dynamic.literal(__ = newState.asInstanceOf[js.Any])
            }
          }
        }): js.Function2[js.Object, js.Object, js.Object]
      }

      if (this.getDerivedStateFromError != null) {
        constructor.getDerivedStateFromError = ((error: js.Error) => {
          val newState = getDerivedStateFromError(error)

          if (newState == null) null
          else {
            if (BaseComponentWrapper.scalaComponentWritingEnabled) {
              DefinitionBase.writeWithWrappingAdjustment(
                this.asInstanceOf[js.Dynamic]._stateWriter.asInstanceOf[Writer[State]]
              )(newState)
            } else {
              js.Dynamic.literal(__ = newState.asInstanceOf[js.Any])
            }
          }
        }): js.Function1[js.Error, js.Object]
      }

      patchedConstructor = constructor
    }

    patchedConstructor
  }

  def componentConstructor(
    implicit propsReader: Reader[Props],
    stateWriter: Writer[State],
    stateReader: Reader[State],
    constructorTag: ConstructorTag[Def]
  ): js.Object = {
    // we only receive non-null reader/writers here when we generate a full typeclass; otherwise we don't set
    // the reader/writer values since we can just use the fallback ones
    // also, we don't overwrite the reader if we already have one since we may have already exported
    if (propsReader != null) this.asInstanceOf[js.Dynamic]._propsReader = propsReader.asInstanceOf[js.Any]
    if (stateReader != null) this.asInstanceOf[js.Dynamic]._stateReader = stateReader.asInstanceOf[js.Any]
    if (stateWriter != null) this.asInstanceOf[js.Dynamic]._stateWriter = stateWriter.asInstanceOf[js.Any]

    BaseComponentWrapper.componentConstructorMiddleware(
      getPatchedConstructor(constructorTag).asInstanceOf[js.Object],
      this.asInstanceOf[js.Object]
    )
  }

  private var componentConstructorInstance: js.Object = null

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    if (componentConstructorInstance == null) {
      componentConstructorInstance = componentConstructor(
        null,
        hot_stateWriter,
        hot_stateReader,
        constructorTag
      )
    }

    new KeyAndRefAddingStage(
      js.Array(
        componentConstructorInstance,
        propsObj
      )
    )
  }

  def apply()(implicit ev: Unit =:= Props, constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] =
    apply(())(constructorTag)
}

object BaseComponentWrapper {
  implicit def proplessKeyAndRef[C <: BaseComponentWrapper { type Props = Unit }](
    c: C
  )(implicit constructorTag: ConstructorTag[c.Def]): KeyAndRefAddingStage[c.Def] =
    c.apply(())

  private var componentConstructorMiddleware = (constructor: js.Object, _: js.Object) => {
    constructor
  }

  private[core] var scalaComponentWritingEnabled = false

  /**
    * Enables writing props and state for Scala defined components. This is
    * needed for hot loading, where data must be written to a JS object and
    * then read when the application is reloaded.
    */
  def enableScalaComponentWriting(): Unit =
    if (scala.scalajs.LinkingInfo.productionMode) {
      throw new IllegalStateException("Cannot enable Scala component writing in production mode")
    } else {
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
