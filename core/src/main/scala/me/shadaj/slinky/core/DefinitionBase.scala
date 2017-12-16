package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{PrivateComponentClass, React, ReactElement}
import me.shadaj.slinky.readwrite.{Reader, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

abstract class DefinitionBase[Props, State](jsProps: js.Object) extends React.Component(jsProps) {
  import DefinitionBase._

  // we extract out props/state reader/writer from the _base value defined in on the constructor
  // see componentConstructor in BaseComponentWrapper
  @inline private[this] final def stateReader: Reader[State] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._stateReader.asInstanceOf[Reader[State]]
  @inline private[this] final def stateWriter: Writer[State] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._stateWriter.asInstanceOf[Writer[State]]
  @inline private[this] final def propsReader: Reader[Props] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._propsReader.asInstanceOf[Reader[Props]]
  @inline private[this] final def propsWriter: Writer[Props] =
    this.asInstanceOf[js.Dynamic].__proto__.constructor._base._propsWriter.asInstanceOf[Writer[Props]]

  def initialState: State

  this.asInstanceOf[PrivateComponentClass].stateR = {
    if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(initialState)
    } else js.Dynamic.literal(__ = initialState.asInstanceOf[js.Any])
  }

  @JSName("props_scala")
  @inline final def props: Props = {
    readWithWrappingAdjustment(propsReader)(this.asInstanceOf[PrivateComponentClass].propsR)
  }

  @JSName("state_scala")
  @inline final def state: State = {
    readWithWrappingAdjustment(stateReader)(this.asInstanceOf[PrivateComponentClass].stateR)
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
      val s = fn(readWithWrappingAdjustment(stateReader)(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      val s = fn(readWithWrappingAdjustment(stateReader)(ps), readWithWrappingAdjustment(propsReader)(p))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala")
  @inline final def setState(s: State, callback: js.Function0[Unit]): Unit = {
    val stateObject = if (BaseComponentWrapper.scalaComponentWritingEnabled) {
      writeWithWrappingAdjustment(stateWriter)(s)
    } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    this.asInstanceOf[PrivateComponentClass].setStateR(stateObject, callback)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: State => State, callback: js.Function0[Unit]): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object) => {
      val s = fn(readWithWrappingAdjustment(stateReader)(ps))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    }, callback)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State, callback: js.Function0[Unit]): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      val s = fn(readWithWrappingAdjustment(stateReader)(ps), readWithWrappingAdjustment(propsReader)(p))
      if (BaseComponentWrapper.scalaComponentWritingEnabled) {
        writeWithWrappingAdjustment(stateWriter)(s)
      } else js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
    }, callback)
  }

  def componentWillMount(): Unit = {}

  def componentDidMount(): Unit = {}

  def componentWillReceiveProps(nextProps: Props): Unit = {}

  {
    val orig = this.asInstanceOf[js.Dynamic].componentWillReceiveProps.asInstanceOf[js.Function1[Props, Unit]]
    this.asInstanceOf[js.Dynamic].componentWillReceiveProps = (props: js.Object) => {
      orig.call(
        this,
        readWithWrappingAdjustment(propsReader)(props).asInstanceOf[js.Any]
      )
    }
  }

  def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

  {
    val orig = this.asInstanceOf[js.Dynamic].shouldComponentUpdate.asInstanceOf[js.Function2[Props, State, Boolean]]
    this.asInstanceOf[js.Dynamic].shouldComponentUpdate = (nextProps: js.Object, nextState: js.Object) => {
      orig.call(
        this,
        readWithWrappingAdjustment(propsReader)(nextProps).asInstanceOf[js.Any],
        readWithWrappingAdjustment(stateReader)(nextState).asInstanceOf[js.Any]
      )
    }
  }

  def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

  {
    val orig = this.asInstanceOf[js.Dynamic].componentWillUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
    this.asInstanceOf[js.Dynamic].componentWillUpdate = (nextProps: js.Object, nextState: js.Object) => {
      orig.call(
        this,
        readWithWrappingAdjustment(propsReader)(nextProps).asInstanceOf[js.Any],
        readWithWrappingAdjustment(stateReader)(nextState).asInstanceOf[js.Any]
      )
    }
  }

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

  {
    val orig = this.asInstanceOf[js.Dynamic].componentDidUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
    this.asInstanceOf[js.Dynamic].componentDidUpdate = (prevProps: js.Object, prevState: js.Object) => {
      orig.call(
        this,
        readWithWrappingAdjustment(propsReader)(prevProps).asInstanceOf[js.Any],
        readWithWrappingAdjustment(stateReader)(prevState).asInstanceOf[js.Any]
      )
    }
  }

  def componentWillUnmount(): Unit = {}

  @JSName("render")
  def render(): ReactElement
}

object DefinitionBase {
  @inline private[slinky] final def readWithWrappingAdjustment[T](reader: Reader[T])(value: js.Object): T = {
    val __value = value.asInstanceOf[js.Dynamic].__value

    if (value.hasOwnProperty("__value")) {
      reader.read(__value.asInstanceOf[js.Object])
    } else {
      reader.read(value)
    }
  }

  @inline private[slinky] final  def writeWithWrappingAdjustment[T](writer: Writer[T])(value: T): js.Object = {
    val __value = writer.write(value)

    if (js.typeOf(__value) == "object") {
      __value
    } else {
      js.Dynamic.literal(__value = __value)
    }
  }
}
