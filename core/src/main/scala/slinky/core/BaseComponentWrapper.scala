package slinky.core

import slinky.core.facade.{React, ReactElement, ReactRef}
import slinky.readwrite.{Reader, Writer}

import scala.language.experimental.macros
import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.language.implicitConversions
import scala.reflect.macros.blackbox

class KeyAndRefAddingStage[D <: js.Any](val props: js.Dictionary[js.Any], val constructor: js.Object) {
  def withKey(key: String): KeyAndRefAddingStage[D] = {
    props("key") = key
    new KeyAndRefAddingStage[D](props, constructor)
  }

  def withRef(ref: D => Unit): KeyAndRefAddingStage[D] = {
    props("ref") = ref
    new KeyAndRefAddingStage[D](props, constructor)
  }

  def withRef(ref: ReactRef[D]): KeyAndRefAddingStage[D] = {
    props("ref") = ref
    new KeyAndRefAddingStage[D](props, constructor)
  }
}

object KeyAndRefAddingStage {
  implicit def build[D <: js.Any](stage: KeyAndRefAddingStage[D]): ReactElement = {
    React.createElement(stage.constructor, stage.props)
  }
}

abstract class BaseComponentWrapper(sr: StateReaderProvider, sw: StateWriterProvider) {
  type Props

  type State

  type Snapshot

  type Def <: Definition

  type Definition <: js.Object

  def getDerivedStateFromProps(nextProps: Props, prevState: State): State = null.asInstanceOf[State]

  private[core] val hot_stateReader = sr.asInstanceOf[Reader[State]]
  private[core] val hot_stateWriter = sw.asInstanceOf[Writer[State]]

  def componentConstructor(implicit propsReader: Reader[Props],
                           stateWriter: Writer[State], stateReader: Reader[State],
                           constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    constructor.displayName = getClass.getSimpleName
    constructor._base = this.asInstanceOf[js.Any]

    if (this.asInstanceOf[js.Dynamic].getDerivedStateFromProps__O__O__O != BaseComponentWrapper.defaultGetDerivedState) {
      constructor.getDerivedStateFromProps = ((nextProps: js.Object, prevState: js.Object) => {
        val nextPropsScala = if (js.typeOf(nextProps) == "object" && nextProps.hasOwnProperty("__")) {
          nextProps.asInstanceOf[js.Dynamic].__.asInstanceOf[Props]
        } else {
          DefinitionBase.readWithWrappingAdjustment(propsReader)(nextProps)
        }

        val prevStateScala = if (js.typeOf(prevState) == "object" && prevState.hasOwnProperty("__")) {
          prevState.asInstanceOf[js.Dynamic].__.asInstanceOf[State]
        } else {
          DefinitionBase.readWithWrappingAdjustment(stateReader)(prevState)
        }

        val newState = getDerivedStateFromProps(nextPropsScala, prevStateScala)

        if (BaseComponentWrapper.scalaComponentWritingEnabled) {
          DefinitionBase.writeWithWrappingAdjustment(stateWriter)(newState)
        } else {
          js.Dynamic.literal(__ = newState.asInstanceOf[js.Any])
        }
      }): js.Function2[js.Object, js.Object, js.Object]
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

    new KeyAndRefAddingStage(
      propsObj,
      componentConstructorInstance
    )
  }

  def apply()(implicit ev: Unit =:= Props, constructorTag: ConstructorTag[Def]): KeyAndRefAddingStage[Def] = {
    apply(())(constructorTag)
  }
}

object BaseComponentWrapper {
  private[BaseComponentWrapper] val defaultGetDerivedState = {
    new BaseComponentWrapper(null, null) {
      override type Props = Unit
      override type State = Unit
      override type Def = Nothing
    }.asInstanceOf[js.Dynamic].getDerivedStateFromProps__O__O__O
  }

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
  def impl(c: blackbox.Context): c.Expr[StateReaderProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Reader[comp.State] = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateReaderProvider]")
  }

  implicit def get: StateReaderProvider = macro impl
}

trait StateWriterProvider extends js.Object
object StateWriterProvider {
  def impl(c: blackbox.Context): c.Expr[StateWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Writer[comp.State] = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateWriterProvider]")
  }

  implicit def get: StateWriterProvider = macro impl
}
