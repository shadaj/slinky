package slinky.core

import slinky.core.facade.{ErrorBoundaryInfo, PrivateComponentClass, React, ReactElement}
import slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

abstract class DefinitionBase[Props, State, Snapshot](jsProps: js.Object) extends React.Component(jsProps) {
  import DefinitionBase._

  // we extract out props/state reader/writer from the _base value defined in on the constructor
  // see componentConstructor in BaseComponentWrapper
  @inline private[this] final def stateReader: Reader[State] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._stateReader.asInstanceOf[Reader[State]]
  @inline private[this] final def stateWriter: Writer[State] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._stateWriter.asInstanceOf[Writer[State]]
  @inline private[this] final def propsReader: Reader[Props] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._propsReader.asInstanceOf[Reader[Props]]

  def initialState: State

  this.asInstanceOf[PrivateComponentClass].stateR = {
    val initialStateValue: State = initialState
    val stateWithExtraApplyFix = if (initialStateValue.getClass == null) {
      initialStateValue.asInstanceOf[js.Function0[State]].apply()
    } else initialStateValue

    if (BaseComponentWrapper.scalaComponentWritingEnabled && defaultBase != null) {
      writeWithWrappingAdjustment(stateWriter)(stateWithExtraApplyFix)
    } else js.Dynamic.literal(__ = stateWithExtraApplyFix.asInstanceOf[js.Any])
  }

  @inline private final def readPropsValue(value: js.Object): Props = {
    if (js.typeOf(value) == "object" && value.hasOwnProperty("__")) {
      value.asInstanceOf[js.Dynamic].__.asInstanceOf[Props]
    } else {
      readWithWrappingAdjustment(propsReader)(value)
    }
  }

  @inline private final def readStateValue(value: js.Object): State = {
    if (js.typeOf(value) == "object" && value.hasOwnProperty("__")) {
      value.asInstanceOf[js.Dynamic].__.asInstanceOf[State]
    } else {
      readWithWrappingAdjustment(stateReader)(value)
    }
  }

  @JSName("props_scala")
  @inline final def props: Props = {
    readPropsValue(this.asInstanceOf[PrivateComponentClass].propsR)
  }

  @JSName("state_scala")
  @inline final def state: State = {
    readStateValue(this.asInstanceOf[PrivateComponentClass].stateR)
  }

  @JSName("setState_scala")
  @inline final def setState(s: State): Unit = {
    val stateObject = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(s)
    } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])

    this.asInstanceOf[PrivateComponentClass].setStateR(stateObject)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: State => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object) => {
      val s = fn(readStateValue(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      val s = fn(readStateValue(ps), readPropsValue(p))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala")
  @inline final def setState(s: State, callback: () => Unit): Unit = {
    val stateObject = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(s)
    } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    this.asInstanceOf[PrivateComponentClass].setStateR(stateObject, callback)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: State => State, callback: () => Unit): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object) => {
      val s = fn(readStateValue(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    }, callback)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State, callback: () => Unit): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      val s = fn(readStateValue(ps), readPropsValue(p))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    }, callback)
  }

  def componentWillMount(): Unit = {}

  def componentDidMount(): Unit = {}

  def componentWillReceiveProps(nextProps: Props): Unit = {}

  def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

  def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

  def getSnapshotBeforeUpdate(prevProps: Props, prevState: State): Snapshot = null.asInstanceOf[Snapshot]

  private val origComponentDidUpdate = this.asInstanceOf[js.Dynamic].componentDidUpdate.bind(this).asInstanceOf[js.Function3[Props, State, Any, Unit]]

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}
  def componentDidUpdate(prevProps: Props, prevState: State, snapshot: Snapshot): Unit = {
    origComponentDidUpdate.asInstanceOf[js.Function2[Props, State, Unit]].apply(prevProps, prevState)
  }

  def componentWillUnmount(): Unit = {}

  def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {}

  @JSName("render")
  def render(): ReactElement

  if (defaultBase != null) {
    if (this.asInstanceOf[js.Dynamic].componentWillMount == defaultBase.componentWillMount) {
      this.asInstanceOf[js.Dynamic].componentWillMount = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentDidMount == defaultBase.componentDidMount) {
      this.asInstanceOf[js.Dynamic].componentDidMount = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentWillReceiveProps != defaultBase.componentWillReceiveProps) {
      val orig = this.asInstanceOf[js.Dynamic].componentWillReceiveProps.bind(this).asInstanceOf[js.Function1[Props, Unit]]
      this.asInstanceOf[js.Dynamic].componentWillReceiveProps = (props: js.Object) => {
        orig(
          readPropsValue(props)
        )
      }
    } else {
      this.asInstanceOf[js.Dynamic].componentWillReceiveProps = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].shouldComponentUpdate != defaultBase.shouldComponentUpdate) {
      val orig = this.asInstanceOf[js.Dynamic].shouldComponentUpdate.bind(this).asInstanceOf[js.Function2[Props, State, Boolean]]
      this.asInstanceOf[js.Dynamic].shouldComponentUpdate = (nextProps: js.Object, nextState: js.Object) => {
        orig(
          readPropsValue(nextProps),
          readStateValue(nextState)
        )
      }
    } else {
      this.asInstanceOf[js.Dynamic].shouldComponentUpdate = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentWillUpdate != defaultBase.componentWillUpdate) {
      val orig = this.asInstanceOf[js.Dynamic].componentWillUpdate.bind(this).asInstanceOf[js.Function2[Props, State, Unit]]
      this.asInstanceOf[js.Dynamic].componentWillUpdate = (nextProps: js.Object, nextState: js.Object) => {
        orig(
          readPropsValue(nextProps),
          readStateValue(nextState)
        )
      }
    } else {
      this.asInstanceOf[js.Dynamic].componentWillUpdate = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].getSnapshotBeforeUpdate != defaultBase.getSnapshotBeforeUpdate) {
      val orig = this.asInstanceOf[js.Dynamic].getSnapshotBeforeUpdate.bind(this).asInstanceOf[js.Function2[Props, State, Any]]
      this.asInstanceOf[js.Dynamic].getSnapshotBeforeUpdate = (prevProps: js.Object, prevState: js.Object) => {
        orig(
          readPropsValue(prevProps),
          readStateValue(prevState)
        )
      }
    } else {
      this.asInstanceOf[js.Dynamic].getSnapshotBeforeUpdate = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentDidUpdate != defaultBase.componentDidUpdate) {
      this.asInstanceOf[js.Dynamic].componentDidUpdate = (prevProps: js.Object, prevState: js.Object, snapshot: Any) => {
        origComponentDidUpdate(
          readPropsValue(prevProps),
          readStateValue(prevState),
          snapshot
        )
      }
    } else {
      this.asInstanceOf[js.Dynamic].componentDidUpdate = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentWillUnmount == defaultBase.componentWillUnmount) {
      this.asInstanceOf[js.Dynamic].componentWillUnmount = js.undefined
    }

    if (this.asInstanceOf[js.Dynamic].componentDidCatch == defaultBase.componentDidCatch) {
      this.asInstanceOf[js.Dynamic].componentDidCatch = js.undefined
    }
  }
}

object DefinitionBase {
  private[DefinitionBase] val defaultBase = new DefinitionBase[Unit, Unit, Unit](null) {
    override def initialState: Unit = ()
    override def render(): ReactElement = null
  }.asInstanceOf[js.Dynamic]

  private[slinky] final def readWithWrappingAdjustment[T](reader: Reader[T])(value: js.Object): T = {
    val __value = value.asInstanceOf[js.Dynamic].__value

    if (value.hasOwnProperty("__value")) {
      reader.read(__value.asInstanceOf[js.Object])
    } else {
      reader.read(value)
    }
  }

  private[slinky] final def writeWithWrappingAdjustment[T](writer: Writer[T])(value: T): js.Object = {
    val __value = writer.write(value)

    if (js.typeOf(__value) == "object") {
      __value
    } else {
      js.Dynamic.literal(__value = __value)
    }
  }
}
