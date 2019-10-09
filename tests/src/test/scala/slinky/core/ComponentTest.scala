package slinky.core

import slinky.core.facade.{ErrorBoundaryInfo, ReactElement}
import slinky.web.ReactDOM
import org.scalajs.dom
import org.scalatest.{Assertion, AsyncFunSuite}
import slinky.readwrite.{Reader, Writer}

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

object TestComponentExtraApply extends ComponentWrapper {
  type Props = Int => Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState(): Int = 0

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

object TestComponentForSeqState extends ComponentWrapper {
  type Props = () => Unit
  type State = Seq[String]

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState = Seq.empty

    override def componentDidMount(): Unit = {
      setState(state :+ "hello")
    }

    override def render() = {
      if (state.nonEmpty) {
        props.apply()
      }
      
      null
    }
  }
}

object TestComponentForShouldComponentUpdate extends ComponentWrapper {
  type Props = () => Unit
  type State = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def shouldComponentUpdate(nextProps: Props, nextState: State) = {
      nextState == 123
    }

    override def componentDidUpdate(prevProps: Props, prevState: State) = {
      prevProps.apply()
    }

    override def render(): ReactElement = {
      null
    }
  }
}

object TestComponentForSnapshot extends ComponentWrapper {
  type Props = Int => Unit
  type State = Int
  type Snapshot = Int

  class Def(jsProps: js.Object) extends Definition(jsProps) {
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

object BadComponent extends StatelessComponentWrapper {
  type Props = Unit

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def render(): ReactElement = {
      throw new Exception("BOO")
    }
  }
}

object ErrorBoundaryComponent extends StatelessComponentWrapper {
  case class Props(bad: Boolean, handler: (js.Error, ErrorBoundaryInfo) => Unit)

  class Def(jsProps: js.Object) extends Definition(jsProps) {
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
}

object DerivedStateComponent extends ComponentWrapper {
  case class Props(num: Int, onValue: Int => Unit)
  type State = Int

  override val getDerivedStateFromProps = (nextProps: Props, prevState: State) => {
    nextProps.num
  }

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def render(): ReactElement = {
      if (state != 0) {
        props.onValue(state)
      }

      null
    }
  }
}

object DerivedStateReturnNullComponent extends ComponentWrapper {
  case class Props(returnNull: Boolean, value: Int)
  case class State(value: Int)

  override val getDerivedStateFromProps = (nextProps: Props, prevState: State) => {
    if (nextProps.returnNull) null else State(nextProps.value)
  }

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState = State(0)

    override def render(): ReactElement = {
      state.value.toString
    }
  }
}

object DerivedStateFromErrorComponent extends ComponentWrapper {
  case class Props(onValue: Int => Unit)
  type State = Int

  override val getDerivedStateFromError = (e: js.Error) => {
    123
  }

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: Int = 0

    override def render(): ReactElement = {
      if (state != 0) {
        props.onValue(state)
        null
      } else {
        BadComponent()
      }
    }
  }
}

// compilation test: state providers for underivable type
object TestUnderivable {
  case class UnDerivable private(private val a: Int)

  object UnDerivableReaderWriter {
    implicit val unDerivableReader: Reader[UnDerivable] = null
    implicit val unDerivableWriter: Writer[UnDerivable] = null
  }

  import UnDerivableReaderWriter._

  object App extends ComponentWrapper {
    type Props = Unit
    type State = UnDerivable
  }
}

object DefaultStateParamsComponent extends ComponentWrapper {
  type Props = Unit
  case class State(a: Int = 1)

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState = null

    override def render(): ReactElement = {
      null
    }
  }
}

object TypeParamsComponent extends ComponentWrapper {
  case class TypedProps[T](abc: T)
  case class TypedState[T](abc: T)

  type Props = TypedProps[_]
  type State = TypedState[_]

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState = TypedState(props.abc)

    override def render(): ReactElement = {
      state.abc.toString
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

  test("initialState works with empty parentheses") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponentExtraApply(i => promise.success(assert(i == 1))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("setState with Seq state runs correct overloaded definition") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponentForSeqState(() => promise.success(assert(true))),
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

  test("Cannot reuse half-built component") {
    val halfBuilt = NoPropsComponent()
    val fullyBuilt: ReactElement = halfBuilt.withKey("abc")

    assertThrows[IllegalStateException] {
      val fullyBuilt2: ReactElement = halfBuilt.withKey("abc2")
    }
  }

  test("shouldComponentUpdate controls when component is updated") {
    var called = false
    var ref: TestComponentForShouldComponentUpdate.Def = null

    ReactDOM.render(
      TestComponentForShouldComponentUpdate(() => {
        called = true
      }).withRef(r => ref = r),
      dom.document.createElement("div")
    )

    ref.setState(123)
    assert(called)

    called = false
    ref.setState(1)
    assert(!called)
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
      ErrorBoundaryComponent(ErrorBoundaryComponent.Props(true, (error, info) => {
        promise.success(assert(true))
      })),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Error boundary component works fine with no errors") {
    var sawError = false

    ReactDOM.render(
      ErrorBoundaryComponent(ErrorBoundaryComponent.Props(false, (error, info) => {
        sawError = true
      })),
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
      DerivedStateComponent(DerivedStateComponent.Props(
        123, i => promise.success(assert(i == 123))
      )),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("getDerivedStateFromProps doesn't update state when null is returned") {
    val container = dom.document.createElement("div")

    ReactDOM.render(
      DerivedStateReturnNullComponent(DerivedStateReturnNullComponent.Props(
        false, 123
      )),
      container
    )

    assert(container.innerHTML == "123")

    ReactDOM.render(
      DerivedStateReturnNullComponent(DerivedStateReturnNullComponent.Props(
        true, 456
      )),
      container
    )

    assert(container.innerHTML == "123")
  }

  test("getDerivedStateFromError results in state being calculated based on error") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      DerivedStateFromErrorComponent(DerivedStateFromErrorComponent.Props(
        i => promise.success(assert(i == 123))
      )),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Can render a component with type parameters") {
    val container = dom.document.createElement("div")
    ReactDOM.render(
      TypeParamsComponent(TypeParamsComponent.TypedProps(123)),
      container
    )

    assert(container.innerHTML == "123")
  }
}
