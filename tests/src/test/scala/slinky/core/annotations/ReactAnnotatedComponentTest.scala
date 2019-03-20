package slinky.core.annotations

import slinky.core.{Component, StatelessComponent}
import slinky.core.facade.{ErrorBoundaryInfo, ReactElement}
import slinky.web.ReactDOM
import org.scalajs.dom
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.Promise
import scala.scalajs.js

@react class TestComponent extends Component {
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
    }, () => {
      props.apply(state)
    })
  }

  override def render(): ReactElement = {
    null
  }
}

@react class TestComponentStateCaseClass extends Component {
  type Props = Unit
  case class State()

  override def initialState: State = State()

  override def render(): ReactElement = {
    null
  }
}

@react class TestComponentCaseClass extends Component {
  case class Props(a: Int)
  type State = Int

  override def initialState: Int = 0

  override def render(): ReactElement = {
    null
  }
}

@react class TestComponentOverrideType extends StatelessComponent {
  override type Props = Int

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

@react class TestForceUpdateComponent extends Component {
  type Props = Function0[Unit]
  type State = Int

  override def componentDidUpdate(prevProps: Props, prevState: State): Unit = {
    props.apply()
  }

  override def initialState: Int = 0

  override def render(): ReactElement = {
    null
  }
}

@react class TakeValuesFromCompanionObject extends Component {
  import TakeValuesFromCompanionObject._

  type Props = Unit
  type State = Int

  override def initialState: Int = 0

  override def render(): ReactElement = {
    foo
  }
}

object TakeValuesFromCompanionObject {
  val foo = "hello"
}

@react class BadComponent extends StatelessComponent {
  type Props = Unit

  override def render(): ReactElement = {
    throw new Exception("BOO")
  }
}

@react class ErrorBoundaryComponent extends StatelessComponent {
  case class Props(bad: Boolean, handler: (js.Error, ErrorBoundaryInfo) => Unit)

  override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
    props.handler.apply(error, info)
  }

  override def render(): ReactElement = {
    if (props.bad) {
      BadComponent()
    } else {
      null
    }
  }
}

@react class TestComponentForSnapshot extends Component {
  type Props = Int => Unit
  type State = Int
  type Snapshot = Int

  override def initialState: Int = 0

  override def componentDidMount(): Unit = forceUpdate()

  override def getSnapshotBeforeUpdate(prevProps: Int => Unit, prevState: Int): Snapshot = {
    123
  }

  override def componentDidUpdate(prevProps: Int => Unit, prevState: Int, snapshot: Snapshot): Unit = {
    props(snapshot)
  }

  override def render(): ReactElement = {
    null
  }
}

@react class DerivedStateComponent extends Component {
  case class Props(num: Int, onValue: Int => Unit)
  type State = Int

  override def initialState: Int = 0

  override def render(): ReactElement = {
    if (state != 0) {
      props.onValue(state)
    }

    null
  }
}

object DerivedStateComponent {
  override val getDerivedStateFromProps = (nextProps: Props, prevState: State) => {
    nextProps.num
  }
}

@react class ComponentWithChildren extends StatelessComponent {
  case class Props(int: Int, children: ReactElement*)

  override def render(): ReactElement = {
    props.children
  }
}

@react class ComponentWithOnlyChildren extends StatelessComponent {
  case class Props(children: Int*)

  override def render(): ReactElement = {
    props.children.map(_.toString)
  }
}

@react class ComponentWithNonVarargChildren extends StatelessComponent {
  case class Props(int: Int, children: List[String])

  override def render(): ReactElement = {
    props.children
  }
}

class ReactAnnotatedComponentTest extends AsyncFunSuite {
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

  test("Error boundary component catches an exception in its children") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      ErrorBoundaryComponent(true, (error, info) => {
        promise.success(assert(true))
      }),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Error boundary component works fine with no errors") {
    var sawError = false

    ReactDOM.render(
      ErrorBoundaryComponent(false, (error, info) => {
        sawError = true
      }),
      dom.document.createElement("div")
    )

    assert(!sawError)
  }

  test("getSnapshotBeforeUpdate is run and returned value is passed to componentDidUpdate") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponentForSnapshot(i => promise.success(assert(i == 123))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("getDerivedStateFromProps results in state being calculated based on props") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      DerivedStateComponent(
        123, i => promise.success(assert(i == 123))
      ),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Can use curried apply for components with children") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithChildren(0)("hello", "bye"),
      targetNode
    )

    assert(targetNode.innerHTML == "hellobye")
  }

  test("Can use direct apply for components with only a children prop") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithOnlyChildren(0, 1, 2),
      targetNode
    )

    assert(targetNode.innerHTML == "012")
  }

  test("Can use curried apply for components with non-vararg children") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithNonVarargChildren(0)(List("a", "b", "c")),
      targetNode
    )

    assert(targetNode.innerHTML == "abc")
  }
}
