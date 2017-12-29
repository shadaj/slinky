package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.ReactDOM
import org.scalajs.dom
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.Promise
import scala.scalajs.js

object TestComponent extends ComponentWrapper {
  type Props = Int => Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def componentWillUpdate(nextProps: Props, nextState: Int): Unit = {
      props.apply(nextState)
    }

    override def componentDidMount(): Unit = {
      setState((s, p) => {
        s + 1
      })
    }

    override def render(): ReactElement = {
      null
    }
  }
}

object TestComponentForSetStateCallback extends ComponentWrapper {
  type Props = Int => Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def componentDidMount(): Unit = {
      setState((s, p) => {
        s + 1
      }, () => {
        props.apply(state)
      })
    }

    override def render(): ReactElement = {
      null
    }
  }
}
object NoPropsComponent extends ComponentWrapper {
  type Props = Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def render(): ReactElement = {
      null
    }
  }
}

object TestForceUpdateComponent extends ComponentWrapper {
  type Props = () => Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def componentDidUpdate(prevProps: Props, prevState: State): Unit = {
      props.apply()
    }

    override def initialState: Int = 0

    override def render(): ReactElement = {
      null
    }
  }
}

class ComponentTest extends AsyncFunSuite {
  test("setState given function is applied") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponent(i => promise.success(assert(i == 1))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("setState callback function is run") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponent(i => promise.success(assert(i == 1))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Can construct a component and provide key") {
    val element: ReactElement = TestComponent(_ => ()).withKey("test")
    assert(element.asInstanceOf[js.Dynamic].key.toString == "test")
  }

  test("Can construct a component taking Unit props with no arguments") {
    val element: ReactElement = NoPropsComponent()
    assert(!js.isUndefined(element.asInstanceOf[js.Dynamic]))
  }

  test("Can construct a component taking Unit props with refs and key") {
    val element: ReactElement = NoPropsComponent.withKey("hi").withRef((r: js.Object) => {})
    assert(element.asInstanceOf[js.Dynamic].key.toString == "hi")
    assert(!js.isUndefined(element.asInstanceOf[js.Dynamic].ref))
  }

  test("Force updating a component by its ref works") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestForceUpdateComponent(() => promise.success(assert(true))).withRef(ref => {
        ref.forceUpdate()
      }),
      dom.document.createElement("div")
    )

    promise.future
  }
}
