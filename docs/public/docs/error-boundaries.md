# Error Boundaries
Slinky supports writing error boundary components, a feature introduced in React 16 to make it easier to handle component exceptions. Error boundaries are components that can catch exceptions thrown by any of their children and display custom UI based on the error, such as a popup.

To create an error boundary using the `@react` macro annotation, simply define the `componentDidCatch` method:
```scala
@react class ErrorBoundaryComponent extends Component {
  type Props = ReactElement
  case class State(hasError: Boolean)

  def initialState = State(hasError = false)

  override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
    setState(State(hasError = true))
    println(s"got an error $error")
  }

  override def render(): ReactElement = {
    if (state.hasError) {
      h1("Something went wrong.")
    } else {
      props
    }
  }
}
```

If using the `ComponentWrapper` API, you similarly implement the `componentDidCatch` method.
```scala
object ErrorBoundaryComponent extends ComponentWrapper {
  type Props = ReactElement
  case class State(hasError: Boolean)

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def initialState = State(hasError = false)

    override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
      setState(State(hasError = true))
      println(s"got an error $error")
    }

    override def render(): ReactElement = {
      if (state.hasError) {
        h1("Something went wrong.")
      } else {
        props
      }
    }
  }
}
```

Using error boundary components is no different than using regular Slinky components. Simply render them to your tree and React will automatically set them up as error boundaries.
