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
    this.asInstanceOf[js.Dynamic]._base._stateReader.asInstanceOf[Reader[State]]
  @inline private[slinky] final def stateWriter: Writer[State] =
    this.asInstanceOf[js.Dynamic]._base._stateWriter.asInstanceOf[Writer[State]]
  @inline private[this] final def propsReader: Reader[Props] =
    this.asInstanceOf[js.Dynamic]._base._propsReader.asInstanceOf[Reader[Props]]

  def initialState: State

  @inline private[slinky] final def readPropsValue(value: js.Object): Props = readValue(value, propsReader)

  @inline private[slinky] final def readStateValue(value: js.Object): State = readValue(value, stateReader)

  @JSName("props_scala")
  @inline final def props: Props = {
    readPropsValue(this.asInstanceOf[PrivateComponentClass].propsR)
  }

  @JSName("state_scala")
  @inline final def state: State = {
    readStateValue(this.asInstanceOf[PrivateComponentClass].stateR)
  }

  @JSName("setState_scala_1")
  @inline final def setState(s: State): Unit = {
    val stateObject = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(s)
    } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])

    this.asInstanceOf[PrivateComponentClass].setStateR(stateObject)
  }

  @JSName("setState_scala_2")
  @inline final def setState(fn: State => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object) => {
      val s = fn(readStateValue(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala_3")
  @inline final def setState(fn: (State, Props) => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      val s = fn(readStateValue(ps), readPropsValue(p))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala_4")
  @inline final def setState(s: State, callback: () => Unit): Unit = {
    val stateObject = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(s)
    } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    this.asInstanceOf[PrivateComponentClass].setStateR(stateObject, callback)
  }

  @JSName("setState_scala_5")
  @inline final def setState(fn: State => State, callback: () => Unit): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object) => {
      val s = fn(readStateValue(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    }, callback)
  }

  @JSName("setState_scala_6")
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

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}
  def componentDidUpdate(prevProps: Props, prevState: State, snapshot: Snapshot): Unit = {
    this.asInstanceOf[js.Dynamic].componentDidUpdateScala(prevProps.asInstanceOf[js.Any], prevState.asInstanceOf[js.Any]).asInstanceOf[Unit]
  }

  def componentWillUnmount(): Unit = {}

  def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {}

  def render(): ReactElement
}

object DefinitionBase {
  private[slinky] val defaultBase = new DefinitionBase[Unit, Unit, Unit](null) {
    override def initialState: Unit = ()
    override def render(): ReactElement = null
  }.asInstanceOf[js.Dynamic]

  @inline private[slinky] final def readValue[P](value: js.Object, propsReader: => Reader[P]): P = {
    if (js.typeOf(value) == "object" && value.hasOwnProperty("__")) {
      value.asInstanceOf[js.Dynamic].__.asInstanceOf[P]
    } else {
      readWithWrappingAdjustment(propsReader)(value)
    }
  }

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
