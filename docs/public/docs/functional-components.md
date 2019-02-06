# Functional Components
Slinky supports writing components as functional components, where the definition is simply a function from props to the elements to render. This style is also the foundation for [React Hooks](https://reactjs.org/docs/hooks-intro.html), an upcoming style of writing components that significantly reduces the boilerplate involved.

## Writing a Functional Component
To write a functional component, create an instance of the `FunctionalComponent` class.

```scala
case class Props(name: String)
val myComponent = FunctionalComponent[Props] { props =>
  h1(s"Hello, ${props.name}!")
}
```

Since it's often nice to have components be top-level members of packages, you can also create functional components by having an object extend the `FunctionalComponent` class:

```scala
case class Props(name: String)
object MyComponent extends FunctionalComponent[Props]({ props =>
  h1(s"Hello, ${props.name}!")
})
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
