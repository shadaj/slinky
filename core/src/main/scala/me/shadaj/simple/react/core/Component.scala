package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.fascade.{ComponentInstance, PrivateComponentClass, React}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

trait WithRaw {
  def raw: js.Dynamic = {
    this.asInstanceOf[js.Dynamic].__raw_ref
  }
}

abstract class Component {
  type Props

  type State

  type Def <: Definition

  @ScalaJSDefined
  abstract class Definition(jsProps: js.Object)(implicit propsReader: Reader[Props],
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
      this.asInstanceOf[PrivateComponentClass].setStateR(stateWriter.write(s, true))
    }

    def componentWillMount(): Unit = {}

    def componentDidMount(): Unit = {}

    def componentWillReceiveProps(props: Props): Unit = {}

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
    def render(): ComponentInstance
  }

  def componentReference(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    constructorTag.constructor.asInstanceOf[js.Object]
  }

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def], propsWriter: Writer[Props]): ComponentInstance = {
    val component = constructorTag.constructor
    component.displayName = getClass.getSimpleName

    val propsObj = propsWriter.write(p, true)

    React.createElement(component.asInstanceOf[js.Object], propsObj)
  }
}
