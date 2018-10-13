package slinky.core

import slinky.core.facade.{ErrorBoundaryInfo, React, ReactElement}

import scala.scalajs.js

abstract class Component extends React.Component(null) {
  type Props
  type State
  type Snapshot

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

  def getSnapshotBeforeUpdate(prevProps: Props, prevState: State): Snapshot = null.asInstanceOf[Snapshot]

  def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

  def componentDidUpdate(prevProps: Props, prevState: State, snapshot: Snapshot): Unit = {}

  def componentWillUnmount(): Unit = {}

  def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {}

  def render(): ReactElement
}

object Component {
  type Wrapper = ComponentWrapper
}

abstract class StatelessComponent extends Component {
  type State = Unit
  def initialState: State = ()
}

object StatelessComponent {
  type Wrapper = StatelessComponentWrapper
}