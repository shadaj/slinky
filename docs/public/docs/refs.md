# Refs
Slinky supports the [new refs API](https://reactjs.org/docs/refs-and-the-dom.html) introduced in React 16.3.

## Creating a Ref Object
To start using the new ref API, first create a ref object, which you can use as a ref property instead of a callback. The `createRef` method in Slinky takes a type parameter so that the ref type is statically typed.

## Refs on HTML Elements
To create a ref for use with an HTML tag, type the ref to store the appropriate elemtn type from the `scala-js-dom` library.

```scala
val myRef = React.createRef[html.Div]

div(ref := myRef)

// somewhere else...
myRef.current.innerHTML
```

## Refs on Slinky Components
If you want to place a ref on a Slinky component, type the ref to store the `Def` type inside your component.

```scala
@react class MyComponent {
  def foo(): Unit = ...
  ...
}

val myRef = React.createRef[MyComponent.Def]

MyComponent(...).withRef(myRef)

// somewhere else...
myRef.current.foo()
```
