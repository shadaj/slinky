package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.fascade.{ComponentInstance, React}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

abstract class Component {
  type Props

  type State

  type Def <: Definition

  @ScalaJSDefined
  abstract class Definition(jsProps: js.Object) extends React.Component(jsProps) {
    def initialState: State

    stateR = js.Dynamic.literal(
      "value" -> initialState.asInstanceOf[js.Any]
    )

    @JSName("props_scala")
    final def props: Props = propsR.value.asInstanceOf[Props]

    @JSName("state_scala")
    final def state: State = stateR.value.asInstanceOf[State]

    @JSName("setState_scala")
    final def setState(s: State): Unit = {
      setStateR(js.Dynamic.literal(
        "value" -> s.asInstanceOf[js.Any]
      ))
    }

    def willMount(): Unit = {}

    this.asInstanceOf[js.Dynamic].componentWillMount = () => {
      willMount()
    }

    def didMount(): Unit = {}

    this.asInstanceOf[js.Dynamic].componentDidMount = () => {
      didMount()
    }

    def willReceiveProps(props: Props): Unit = {}

    this.asInstanceOf[js.Dynamic].componentWillReceiveProps = (props: js.Dynamic) => {
      willReceiveProps(
        props.value.asInstanceOf[Props]
      )
    }

    def shouldUpdate(nextProps: Props, nextState: State): Boolean = true

    this.asInstanceOf[js.Dynamic].shouldComponentUpdate = (nextProps: js.Dynamic, nextState: js.Dynamic) => {
      shouldUpdate(
        nextProps.value.asInstanceOf[Props],
        nextState.value.asInstanceOf[State]
      )
    }

    def willUpdate(nextProps: Props, nextState: State): Unit = {}

    this.asInstanceOf[js.Dynamic].componentWillUpdate = (nextProps: js.Dynamic, nextState: js.Dynamic) => {
      willUpdate(
        nextProps.value.asInstanceOf[Props],
        nextState.value.asInstanceOf[State]
      )
    }

    def didUpdate(prevProps: Props, prevState: State): Unit = {}

    this.asInstanceOf[js.Dynamic].componentDidUpdate = (prevProps: js.Dynamic, prevState: js.Dynamic) => {
      didUpdate(
        prevProps.value.asInstanceOf[Props],
        prevState.value.asInstanceOf[State]
      )
    }

    def willUnmount(): Unit = {}

    this.asInstanceOf[js.Dynamic].componentWillUnmount = () => {
      willUnmount()
    }

    @JSName("render")
    def render(): ComponentInstance
  }

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def]): ComponentInstance = {
    val component = constructorTag.constructor
    component.displayName = getClass.getSimpleName

    val propsObj = js.Dynamic.literal("value" -> p.asInstanceOf[js.Any])

    React.createElement(component, propsObj)
  }
}
