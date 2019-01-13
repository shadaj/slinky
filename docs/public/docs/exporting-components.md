# Exporting Components
If introducing Slinky to an existing JavaScript codebase, you can export Slinky components so that they are accessible from React JSX code. This functionality is available for both component classes and functional components, building on top of Slinky's readers to convert JavaScript props into Scala objects.

To export a component, use the implicit conversion to `ReactComponentClass`. Instances of `ReactComponentClass` can be passed to JavaScript code and can be used as regular references to React components.

```scala
@react class MyComponent extends Component {
  case class Props(name: String)

  ...
}

object SlinkyComponents {
  @JSExportTopLevel("SlinkyComponents") val components: js.Dictionary[ReactComponentClass] = js.Dictionary(
    "MyComponent" -> MyComponent
  )
}
```

```jsx
import { SlinkyComponents } from "scalajs";
const { MyComponent } = SlinkyComponents;

...

<MyComponent name="Foo"/>
```
