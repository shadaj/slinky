package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.ReactElement

import scala.scalajs.js

abstract class Component {
  type Props
  type State

  def initialState: State

  final def props: Props = ???

  final def state: State = ???

  final def setState(s: State): Unit = ???

  final def setState(fn: State => State): Unit = ???

  final def setState(fn: (State, Props) => State): Unit = ???

  final def setState(s: State, callback: js.Function0[Unit]): Unit = ???

  final def setState(fn: State => State, callback: js.Function0[Unit]): Unit = ???

  final def setState(fn: (State, Props) => State, callback: js.Function0[Unit]): Unit = ???

  def componentWillMount(): Unit = {}

  def componentDidMount(): Unit = {}

  def componentWillReceiveProps(nextProps: Props): Unit = {}

  def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

  def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

  def componentWillUnmount(): Unit = {}

  def render(): ReactElement
}