# Writing Components
Writing React components in Slinky is just like writing them with the native ES6 API.

Slinky components must define the type of their React props and implement a render method. Within the component, you can access the props using the `props` variable.
```scala
@react class HelloName extends Component {
  case class Props(name: String)
  
  def render = {
    h1(s"Hello ${props.name}")
  }
}
```

## Adding State
To make your component stateful, first define your state type and initial state
```scala
case class State(buttonPresses: Int)

def initialState = State(0)
```

Then you can access the state with `state` and use `setState` to update your component's state. Slinky supports all variants of `setState`, such as the one that takes the previous state as a function parameter.

So a full component would look something like this:

```scala
@react class MyComponent extends Component {
  type Props = Unit // no props
  case class State(buttonPresses: Int)
  
  def initialState = State(0)
  
  def render = {
    div(
      h1(s"Clicked ${state.buttonPresses} times!"),
      button(onClick := (_ => {
        setState(State(state.buttonPresses + 1))
      }))(
        "Click Me!"
      )
    )
  }
}
```

## Component Styles
With Slinky 0.2.0, the `@react` macro annotation was introduced to reduce the boilerplate involved with creating components. Most examples in the documentation will use the macro annotation, but it is always possible to use the no-annotation API with just a few changes.

If you have a component that looks like this:
```scala
@react class MyComponent extends Component {
  case class Props(...)
  case class State(...)
  
  def initialState = ...
  
  def render = {
    ...
  }
}
```

The macro annotation converts this code into something like this:
```scala
object MyComponent extends ComponentWrapper {
  case class Props(...)
  case class State(...)
  
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def initialState = ...
    
    def render = {
      ...
    }
  }
}
```
