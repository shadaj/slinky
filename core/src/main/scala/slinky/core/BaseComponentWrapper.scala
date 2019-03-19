package slinky.core

import slinky.core.facade.{React, ReactRaw, ReactElement, ReactRef}
import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.JSExport

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.macros.whitebox

final class KeyAndRefAddingStage[D](private val args: js.Array[js.Any]) extends AnyVal {
  @inline def withKey(key: String): KeyAndRefAddingStage[D] = {
    args(1).asInstanceOf[js.Dictionary[js.Any]]("key") = key
    this
  }

  @inline def withRef(ref: D => Unit): KeyAndRefAddingStage[D] = {
    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = ref
    this
  }

  @inline def withRef(ref: ReactRef[D]): KeyAndRefAddingStage[D] = {
    args(1).asInstanceOf[js.Dictionary[js.Any]]("ref") = ref
    this
  }
}

object KeyAndRefAddingStage {
  @inline implicit def build[D](stage: KeyAndRefAddingStage[D]): ReactElement = {
    ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, stage.args).asInstanceOf[ReactElement]
  }
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

  def componentConstructor(implicit propsReader: Reader[Props],
                           stateWriter: Writer[State], stateReader: Reader[State],
                           constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    if (!scala.scalajs.LinkingInfo.productionMode) {
      constructor.displayName = getClass.getSimpleName
    }

    constructor._base = this.asInstanceOf[js.Any]

    if (this.getDerivedStateFromProps != null) {
      constructor.getDerivedStateFromProps = ((props: js.Object, state: js.Object) => {
        val propsScala = if (js.typeOf(props) == "object" && props.hasOwnProperty("__")) {
          props.asInstanceOf[js.Dynamic].__.asInstanceOf[Props]
        } else {
          DefinitionBase.readWithWrappingAdjustment(propsReader)(props)
        }

        val stateScala = if (js.typeOf(state) == "object" && state.hasOwnProperty("__")) {
          state.asInstanceOf[js.Dynamic].__.asInstanceOf[State]
        } else {
          DefinitionBase.readWithWrappingAdjustment(stateReader)(state)
        }

        val newState = getDerivedStateFromProps(propsScala, stateScala)

        if (BaseComponentWrapper.scalaComponentWritingEnabled) {
          DefinitionBase.writeWithWrappingAdjustment(stateWriter)(newState)
        } else {
          js.Dynamic.literal(__ = newState.asInstanceOf[js.Any])
        }
      }): js.Function2[js.Object, js.Object, js.Object]
    }

    if (this.getDerivedStateFromError != null) {
      constructor.getDerivedStateFromError = ((error: js.Error) => {
        val newState = getDerivedStateFromError(error)

        if (BaseComponentWrapper.scalaComponentWritingEnabled) {
          DefinitionBase.writeWithWrappingAdjustment(stateWriter)(newState)
        } else {
          js.Dynamic.literal(__ = newState.asInstanceOf[js.Any])
        }
      }): js.Function1[js.Error, js.Object]
    }

    // we only receive non-null reader/writers here when we generate a full typeclass; otherwise we don't set
    // the reader/writer values since we can just use the fallback ones
    if (propsReader != null) this.asInstanceOf[js.Dynamic]._propsReader = propsReader.asInstanceOf[js.Any]
    if (stateReader != null) this.asInstanceOf[js.Dynamic]._stateReader = stateReader.asInstanceOf[js.Any]
    if (stateWriter != null) this.asInstanceOf[js.Dynamic]._stateWriter = stateWriter.asInstanceOf[js.Any]

    BaseComponentWrapper.componentConstructorMiddleware(
      constructor.asInstanceOf[js.Object], this.asInstanceOf[js.Object])
  }

  private var componentConstructorInstance: js.Object = null

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    val propsObj = js.Dictionary("__" -> p.asInstanceOf[js.Any])

    if (componentConstructorInstance == null) {
      componentConstructorInstance =
        componentConstructor(
          null,
          hot_stateWriter, hot_stateReader,
          constructorTag
        )
    }

    new KeyAndRefAddingStage(js.Array(
      componentConstructorInstance,
      propsObj
    ))
  }

  def apply()(implicit ev: Unit =:= Props, constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    apply(())(constructorTag)
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
    if (scala.scalajs.LinkingInfo.productionMode) {
      throw new IllegalStateException("Cannot enable Scala component writing in production mode")
    } else {
      scalaComponentWritingEnabled = true
    }
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

trait StateReaderProvider extends js.Object
object StateReaderProvider {
  def impl(c: whitebox.Context): c.Expr[StateReaderProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Reader[comp.State] = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type], silent = false)
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateReaderProvider]")
  }

  implicit def get: StateReaderProvider = macro impl
}

trait StateWriterProvider extends js.Object
object StateWriterProvider {
  def impl(c: whitebox.Context): c.Expr[StateWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Writer[comp.State] = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type], silent = false)
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateWriterProvider]")
  }

  implicit def get: StateWriterProvider = macro impl
}
