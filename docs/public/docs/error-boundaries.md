# Error Boundaries
Slinky supports writing error boundary components, a feature introduced in React 16 to make it easier to handle component exceptions. Error boundaries are components that can catch exceptions thrown by any of their children and display custom UI based on the error, such as a popup.

To create an error boundary using the `@react` macro annotation, simply define the `componentDidCatch` method:
```scala
@react class ErrorBoundaryComponent extends StatelessComponent {
  type Props = Unit

  override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
    println(s"got an error $error")
  }

  override def render(): ReactElement = {
    ...
  }
}
```

If using the `ComponentWrapper` API, you will need to mix in the `ErrorBoundary` trait to the `Def` class and then implement the `componentDidCatch` method as above.
```scala
object ErrorBoundaryComponent extends StatelessComponentWrapper {
  type Props = Unit

  class Def(jsProps: js.Object) extends Definition(jsProps) with ErrorBoundary {
    override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
      println(s"got an error $error")
    }

    override def render(): ReactElement = {
      ...
    }
  }
}
```

Using error boundary components is no different than using regular Slinky components. Simply render them to your tree and React will automatically set them up as error boundaries.
