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

  test("Can construct a component and provide key") {
    assertCompiles("""TestComponent(_ => ())(key = "test")""")
  }

  test("Can construct a component with macro apply and provide key") {
    assertCompiles("""TestComponentCaseClass(a = 1)(key = "test")""")
  }

  test("Can construct a component taking Unit props with no arguments") {
    assertCompiles("""NoPropsComponent()""")
  }

  test("Can construct a component taking Unit props with refs and key") {
    assertCompiles("""NoPropsComponent("hi", (r: js.Object) => {})""")
  }
}
