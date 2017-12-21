package me.shadaj.slinky.core

import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.ReactDOM
import org.scalajs.dom
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.Promise
import scala.scalajs.js

@react
class TestComponent extends Component {
  type Props = Int => Unit
  type State = Int

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

@react class TestComponentForSetStateCallback extends Component {
  type Props = Int => Unit
  type State = Int

  override def initialState: Int = 0

  override def componentDidMount(): Unit = {
    setState((s, p) => {
      s + 1
    }, new Function0[Unit] {
      override def apply(): Unit = props.apply(state)
    })
  }

  override def render(): ReactElement = {
    null
  }
}

@react
class TestComponentStateCaseClass extends Component {
  type Props = Unit
  case class State()

  override def initialState: State = State()

  override def render(): ReactElement = {
    null
  }
}

@react
class TestComponentCaseClass extends Component {
  case class Props(a: Int)
  type State = Int

  override def initialState: Int = 0

  override def render(): ReactElement = {
    null
  }
}


@react class NoPropsComponent extends Component {
  type Props = Unit
  type State = Int

  override def initialState: Int = 0

  override def render(): ReactElement = {
    null
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

  test("Can construct a component with macro apply and provide key") {
    val element: ReactElement = TestComponentCaseClass(a = 1).withKey("test")
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
}
