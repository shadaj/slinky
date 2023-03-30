package slinky.core.annotations

import slinky.core.{Component, StatelessComponent}
import slinky.core.facade.{ErrorBoundaryInfo, Fragment, ReactElement}
import slinky.web.html._

import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Try

// These are here rather than in test so that the non-unit statement warning can be checked.

@react class TestComponent extends Component {
  type Props = Int => Unit
  type State = Int

  override def initialState: Int = 0

  override def componentWillUpdate(nextProps: Props, nextState: Int): Unit = {
    props.apply(nextState)
  }

  override def componentDidMount(): Unit = {
    setState((s, _) => {
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
    setState((s, _) => {
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
  override val getDerivedStateFromProps = (nextProps: Props, _: State) => {
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

@react class SubComponentWithReactElementContainers extends StatelessComponent {
  case class Props(seq: Seq[ReactElement],
                   list: List[ReactElement],
                   option: Option[ReactElement],
                   jsUndefOr: js.UndefOr[String],
                   attempt: Try[ReactElement],
                   function: () => ReactElement,
                   fragment: ReactElement,
                   future: Future[ReactElement],
                   varargs: ReactElement*)

  override def render(): ReactElement = {
    div(props.seq, props.list, props.option, props.jsUndefOr, props.fragment, props.varargs)
  }
}

@react class ComponentWithVariableReactElementContainers extends StatelessComponent {
  import scala.concurrent.ExecutionContext.Implicits.global

  type Props = Unit

  override def render(): ReactElement = {
    val s = Seq("a", "b")
    val l = List("c", "d")
    val o = Some("e")
    val j = "f"
    val t = Try(h2("g"))
    val fn = () => "h"
    val fr = Fragment(List(h1("i"), i("j")))
    val f = Future { "k" }
    val v = "l"

    SubComponentWithReactElementContainers(s, l, o, j, t, fn, fr, f, v)
  }
}

@react class ComponentWithInlineReactElementContainers extends StatelessComponent {
  import scala.concurrent.ExecutionContext.Implicits.global

  type Props = Unit

  override def render(): ReactElement = {
    SubComponentWithReactElementContainers(
      Seq("a", "b"),
      List("c", "d"),
      Some("e"),
      "f",
      Try(h2("g")),
      () => "h",
      Fragment(List(h1("i"), i("j"))),
      Future { "k" },
      "l"
    )
  }
}
