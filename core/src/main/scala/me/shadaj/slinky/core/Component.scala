package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ErrorBoundaryInfo, React, ReactElement}

import scala.scalajs.js

abstract class Component extends React.Component(null) {
  type Props
  type State

  def initialState: State

  final def props: Props = ???

  final def state: State = ???

  final def setState(s: State): Unit = ???

  final def setState(fn: State => State): Unit = ???

  final def setState(fn: (State, Props) => State): Unit = ???

  final def setState(s: State, callback: () => Unit): Unit = ???

  final def setState(fn: State => State, callback: () => Unit): Unit = ???

  final def setState(fn: (State, Props) => State, callback: () => Unit): Unit = ???

  def componentWillMount(): Unit = {}

  def componentDidMount(): Unit = {}

  def componentWillReceiveProps(nextProps: Props): Unit = {}

  def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

  def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

  def componentWillUnmount(): Unit = {}

  def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {}

  def render(): ReactElement
}

abstract class StatelessComponent extends Component {
  type State = Unit
  def initialState: State = ()
}
