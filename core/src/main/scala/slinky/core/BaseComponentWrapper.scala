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

abstract class BaseComponentWrapper(pr: PropsReaderProvider, pw: PropsWriterProvider, sr: StateReaderProvider, sw: StateWriterProvider) {
  type Props

  type State

  type Def <: Definition

  type Definition <: js.Object

  private[core] val hot_propsReader = pr.asInstanceOf[Reader[Props]]
  private[core] val hot_propsWriter = pw.asInstanceOf[Writer[Props]]
  private[core] val hot_stateReader = sr.asInstanceOf[Reader[State]]
  private[core] val hot_stateWriter = sw.asInstanceOf[Writer[State]]

  def componentConstructor(implicit propsReader: Reader[Props],
                           stateWriter: Writer[State], stateReader: Reader[State],
                           constructorTag: ConstructorTag[Def]): js.Object = {
    val constructor = constructorTag.constructor
    constructor.displayName = getClass.getSimpleName
    constructor._base = this.asInstanceOf[js.Any]

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
    val propsObj = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      DefinitionBase.writeWithWrappingAdjustment(hot_propsWriter)(p).asInstanceOf[js.Dictionary[js.Any]]
    } else {
      js.Dictionary("__" -> p.asInstanceOf[js.Any])
    }

    if (componentConstructorInstance == null) {
      componentConstructorInstance =
        componentConstructor(
          hot_propsReader,
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

trait PropsReaderProvider extends js.Object
object PropsReaderProvider {
  def impl(c: blackbox.Context): c.Expr[PropsReaderProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val readerType = tq"_root_.slinky.readwrite.Reader[$compName.Props]"
    val q"val x: $typedReaderType = null" = c.typecheck(q"val x: $readerType = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.PropsReaderProvider]")
  }

  implicit def get: PropsReaderProvider = macro impl
}

trait PropsWriterProvider extends js.Object
object PropsWriterProvider {
  def impl(c: blackbox.Context): c.Expr[PropsWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val readerType = tq"_root_.slinky.readwrite.Writer[$compName.Props]"
    val q"val x: $typedReaderType = null" = c.typecheck(q"val x: $readerType = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.PropsWriterProvider]")
  }

  implicit def get: PropsWriterProvider = macro impl
}

trait StateReaderProvider extends js.Object
object StateReaderProvider {
  def impl(c: blackbox.Context): c.Expr[StateReaderProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val readerType = tq"_root_.slinky.readwrite.Reader[$compName.State]"
    val q"val x: $typedReaderType = null" = c.typecheck(q"val x: $readerType = null")
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
    val readerType = tq"_root_.slinky.readwrite.Writer[$compName.State]"
    val q"val x: $typedReaderType = null" = c.typecheck(q"val x: $readerType = null")
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateWriterProvider]")
  }

  implicit def get: StateWriterProvider = macro impl
}
