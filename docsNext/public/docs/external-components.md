# External Components
Slinky makes it easy to use external components from the React community in an idiomatic and type-safe manner.

## Defining an External Component
To define an external component, you must specify the type of props the component will receive and provide a reference to the external component's definition (usually an ES6 class or function).

With Slinky's reader/writer API, you can use Scala types such as Seq, Option, Future and the values will automatically be converted to the respective JS types when creating an instance of the component (in this case js.Array, js.UndefOr, and js.Promise). In addition, Slinky supports passing Scala-implemented components into external components through the `ReactComponentClass` type.

So an external component for the Link component in [React Router](https://reacttraining.com/react-router/web/guides/quick-start) would look like this:

```scala
@JSImport("react-router-dom", JSImport.Default)
@js.native
object ReactRouterDOM extends js.Object {
  val Route: js.Object = js.native
}

@react object Route extends ExternalComponent {
  case class Props(path: String, component: ReactComponentClass[_], exact: Boolean = false)
  override val component = ReactRouterDOM.Route
}

// ...

Route(path = "/foo/", component = MySlinkyComponent)
```

Here, we use a CommonJS import to get access to the `Route` component class, but you could also grab the reference from js.Dynamic.global if you're not using a module bundling tool like Webpack.

## Propless External Components
Sometimes, external components may not take any props. In this case, you can use `ExternalComponentNoProps` to have a nicer API to construct instances of the component. With the `ExternalComponentNoProps`, the `@react` annotation should not be used since it does not have any code generation to perform when there are no props.

```scala
object Switch extends ExternalComponentNoProps {
  override val component = ReactRouterDOM.Switch
}

// ...

Switch()
```

## External Components with Attributes
Some external components may be wrappers around HTML elements, with props being passed down to be used as attributes around the elements. To create an interface to use such components, you can use `ExternalComponentWithAttributes`, which provides an API that takes additional attributes for a specified target element type.

By specifying a target element type, in this case `a.tag.type`, your code will be checked at compile time to make sure that the attributes you are setting make sense for the target type. If you are working with external components that do not have a specified target type, you can use the `*.tag.type` fallback target type instead, which allows any attributes to be passed in.

```scala
@react object NavLink extends ExternalComponentWithAttributes[a.tag.type] {
  case class Props(to: String, activeStyle: Option[js.Dynamic] = None, activeClassName: Option[String] = None)
  override val component = ReactRouterDOM.NavLink
}
```

With this definition, we could apply custom styling to a `NavLink` like this:
```scala
NavLink(to = "/somepage/")(
  style := js.Dynamic.literal(
    fontSize = "30px"
  )
)
```

If you have an external component that takes attributes but no special props, you can use the `ExternalComponentNoPropsWithAttributes` type, which handles a combination of the above two cases.

## External Components with Ref Types
Some external components also offer an imperative API available on the constructed component instance, which can be accessed by using `ref`s. To enable stronger typing of these ref APIs, Slinky includes `WithRefType` variants of `ExternalComponent` that take an additional type parameter for the `@js.native` facade that should be used to access the ref APIs.

For example:
```scala
@js.native trait MyRefApi extends js.Object {
  def someFunction(): Unit = js.native
}

@react object MyExternalComponent extends ExternalComponentWithRefType[MyRefApi] { ... }

MyExternalComponent(...).withRef(ref => {
  ref.someFunction()
})
```
