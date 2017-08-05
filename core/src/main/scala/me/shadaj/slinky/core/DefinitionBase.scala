package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ReactElement, PrivateComponentClass, React}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

@ScalaJSDefined
abstract class DefinitionBase[Props, State](jsProps: js.Object)(implicit propsReader: Reader[Props],
                                                                propsWriter: Writer[Props],
                                                                stateReader: Reader[State],
                                                                stateWriter: Writer[State]) extends React.Component(jsProps) {
  def initialState: State

  this.asInstanceOf[PrivateComponentClass].stateR = stateWriter.write(initialState, true)

  @JSName("props_scala")
  @inline final def props: Props = {
    propsReader.read(this.asInstanceOf[PrivateComponentClass].propsR, true)
  }

  @JSName("state_scala")
  @inline final def state: State = {
    stateReader.read(this.asInstanceOf[PrivateComponentClass].stateR, true)
  }

  @JSName("setState_scala")
  @inline final def setState(s: State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR(js.Dynamic.literal(__ = s.asInstanceOf[js.Any]))
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      js.Dynamic.literal(__ = fn(stateReader.read(ps, true), propsReader.read(p, true)).asInstanceOf[js.Any])
    })
  }

  @JSName("setState_scala")
  @inline final def setState(s: State, callback: js.Function0[Unit]): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR(js.Dynamic.literal(__ = s.asInstanceOf[js.Any]), callback)
  }

  @JSName("setState_scala")
  @inline final def setState(fn: (State, Props) => State, callback: js.Function0[Unit]): Unit = {
    this.asInstanceOf[PrivateComponentClass].setStateR((ps: js.Object, p: js.Object) => {
      js.Dynamic.literal(__ = fn(stateReader.read(ps, true), propsReader.read(p, true)).asInstanceOf[js.Any])
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
        propsReader.read(props, true).asInstanceOf[js.Any]
      )
    }
  }

  def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

  {
    val orig = this.asInstanceOf[js.Dynamic].shouldComponentUpdate.asInstanceOf[js.Function2[Props, State, Boolean]]
    this.asInstanceOf[js.Dynamic].shouldComponentUpdate = (nextProps: js.Object, nextState: js.Object) => {
      orig.call(
        this,
        propsReader.read(nextProps, true).asInstanceOf[js.Any],
        stateReader.read(nextState, true).asInstanceOf[js.Any]
      )
    }
  }

  def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

  {
    val orig = this.asInstanceOf[js.Dynamic].componentWillUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
    this.asInstanceOf[js.Dynamic].componentWillUpdate = (nextProps: js.Object, nextState: js.Object) => {
      orig.call(
        this,
        propsReader.read(nextProps, true).asInstanceOf[js.Any],
        stateReader.read(nextState, true).asInstanceOf[js.Any]
      )
    }
  }

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

  {
    val orig = this.asInstanceOf[js.Dynamic].componentDidUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
    this.asInstanceOf[js.Dynamic].componentDidUpdate = (prevProps: js.Object, prevState: js.Object) => {
      orig.call(
        this,
        propsReader.read(prevProps, true).asInstanceOf[js.Any],
        stateReader.read(prevState, true).asInstanceOf[js.Any]
      )
    }
  }

  def componentWillUnmount(): Unit = {}

  @JSName("render")
  def render(): ReactElement
}