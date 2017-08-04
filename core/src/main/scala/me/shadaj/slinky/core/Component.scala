package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, PrivateComponentClass, React, ReactProxy}

import scala.scalajs.{LinkingInfo, js}
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

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
      this.asInstanceOf[PrivateComponentClass].setStateR(js.Dynamic.literal(__ = s.asInstanceOf[js.Any]))
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

  final val enableHotLoading = !js.isUndefined(ReactProxy) && LinkingInfo.developmentMode
  private var processedEnable = false

  private def superComponentConstructor(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    val component = constructorTag.constructor
    component.displayName = getClass.getSimpleName

    component.asInstanceOf[js.Object]
  }

  def componentConstructor(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    if (enableHotLoading) {
      if (!processedEnable) {
        processedEnable = true

        if (enableHotLoading) {
          if (js.isUndefined(js.Dynamic.global.proxies)) {
            js.Dynamic.global.proxies = js.Dynamic.literal()
          }

          if (js.isUndefined(js.Dynamic.global.proxies.selectDynamic(this.getClass.getName))) {
            println("creating proxy")
            js.Dynamic.global.proxies.updateDynamic(this.getClass.getName)(ReactProxy.createProxy(superComponentConstructor))
          } else {
            println("updating proxy")
            val forceUpdate = ReactProxy.getForceUpdate(React)
            js.Dynamic.global.proxies.selectDynamic(this.getClass.getName).update(superComponentConstructor).asInstanceOf[js.Array[js.Object]]
              .foreach(o => forceUpdate(o))
          }
        }
      }

      js.Dynamic.global.proxies.selectDynamic(this.getClass.getName).get().asInstanceOf[js.Object]
    } else {
      superComponentConstructor
    }
  }

  def apply(p: Props, key: String = null, ref: Def => Unit = null)(implicit constructorTag: ConstructorTag[Def], propsWriter: Writer[Props]): ComponentInstance = {
    val propsObj = js.Dynamic.literal(__ = p.asInstanceOf[js.Any])

    if (key != null) {
      propsObj.asInstanceOf[js.Dynamic].key = key
    }

    if (ref != null) {
      propsObj.asInstanceOf[js.Dynamic].ref = ref: js.Function1[Def, Unit]
    }

    React.createElement(componentConstructor, propsObj)
  }
}
