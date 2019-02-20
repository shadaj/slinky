# Functional Components and Hooks
Slinky supports writing components as functional components, where the definition is simply a function from props to the elements to render. This style is also the foundation for [React Hooks](https://reactjs.org/docs/hooks-intro.html), an new style of writing components that significantly reduces the boilerplate involved.

## Writing a Functional Component
To write a functional component, create an instance of the `FunctionalComponent` class.

```scala
case class Props(name: String)
val myComponent = FunctionalComponent[Props] { props =>
  h1(s"Hello, ${props.name}!")
}
```

To generate a friendly `.apply` method like with the `@react class` style, you can place the `@react` annotation on an object that contains a `case class Props` and property `val component` which is a functional component.

```scala
@react object MyComponent {
  case class Props(name: String)
  val component = FunctionalComponent[Props] { props =>
    h1(s"Hello, ${props.name}!")
  }
}

// ...

MyComponent(name = ...)
```

## Using React Hooks
Slinky supports all the React Hooks, with APIs that are almost identical versions of the ones included in React with additional typesafety. To learn more about the available hooks, take a look at the [official React docs](https://reactjs.org/docs/hooks-intro.html). The purpose of this section is to highlight the differences between the React and Slinky APIs.

To use hooks, import them from the `Hooks` object and use them in your functional component just like you would in regular React code:

```scala
import slinky.core.facade.Hooks._

val component = FunctionalComponent[Props] { props =>
  val (state, updateState) = useState(0)
  // ...
}
```

Some hooks, like `useEffect`, `useCallback`, `useMemo`, and `useLayoutEffect`, can take an extra list of objects that determines whether values should be recalculated. In Slinky, this list can be any iterable Scala collection, such as a `Seq`. For example, the usage of `useEffect` with watched values would look like:

```scala
useEffect(
  () => { ... },
  Seq(watchedValueA, watchedValueB)
)
```

## Forwarding Refs
React includes the `React.forwardRef` function to allow functional components to take refs. In Slinky, `forwardRef` can be used by calling it on a `FunctionalComponentTakingRef`, a special version of `FunctionalComponent` that can be created by passing in a function taking two parameters -- the regular props parameter as well as the ref (of type `ReactRef`).

```scala
React.forwardRef(FunctionalComponent[
  Int /* type of props */,
  Any /* type of the data stored in the ref */
]((props: Int, ref: ReactRef[Any]) => {
  ...
}))
```

When using hooks, `forwardRef` can be used to expose an imperative API with the `useImperativeHandle` hook. The ref type passed into `FunctionalComponent[...]` determines what APIs will be available on the ref, so Scala traits work well here to expose custom imperative functions. For example, to expose a `.bump()` method on the ref of a functional component:

```scala
trait BumpHandle {
  // increases the internal bumpableState by one
  def bump(): Unit
}

val component = React.forwardRef(FunctionalComponent((props: Int, ref: ReactRef[BumpHandle]) => {
  val (bumpableState, updateBumpableState) = useState(0)
  useImperativeHandle(ref, () => {
    // this object will be used when a ref is requested
    new BumpHandle {
      def bump(): Unit = {
        updateBumpableState(bumpableState + 1)
      }
    }
  })
}))

// to use the component and the imperative functions...

div(
  component(123).withRef(ref => ref.bump())
)
```
