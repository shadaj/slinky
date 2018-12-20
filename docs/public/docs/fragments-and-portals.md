# Fragments and Portals
Slinky supports the special fragment and portal element types that were introduced in React 16.

## Fragments
[Fragments](https://reactjs.org/docs/fragments.html) make it possible to return multiple elements from a component. To create a fragment simply return a list of elements in your `render` method.

```scala
@react class MyComponent extends StatelessComponent {
  case class Props()
  
  def render = {
    List(
      h1("a"),
      h2("b"),
      h3("c")
    )
  }
}
```

Additionally, Slinky supports the `Fragment` component [introduced in React 16.2](https://reactjs.org/blog/2017/11/28/react-v16.2.0-fragment-support.html).

```scala
import slinky.core.facade.Fragment

@react class MyComponent extends StatelessComponent {
  case class Props()
  
  def render = {
    Fragment(
      h1("a"),
      h2("b"),
      h3("c")
    )
  }
}
```

## Portals
[Portals](https://reactjs.org/docs/portals.html) are another special element type introduced in React 16 that make it possible to render React content in a different location in the DOM than it would normally go. This is useful for components like modals, which often need to be placed at a higher level in the DOM than where the component is placed.

To construct a portal, use the method in `ReactDOM`:

```scala
import org.scalajs.dom.document

val containerDOMNode = document.createElement("button")

// ...

import slinky.web.ReactDOM

div(  
  // ...,
  ReactDOM.createPortal(
    h1("hello!"),
    containerDOMNode
  )
)
```

This will result in the `h1` tag being rendered into the containerDOMNode `button` tag instead of inside the parent `div` tag.
