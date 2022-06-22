# Context
Slinky supports the [new context API](https://reactjs.org/docs/context.html) introduced in React 16.3.

## Creating a Context Object
To start using the new context API, first create a context object, which contains the components needed to provide and consume context. The `createContext` method in Slinky takes a type parameter so that the context value is statically typed.

```scala
val myContext = React.createContext[String]("default-context-value")
```

## Providing Context
Now that you have a context object, you can use the `Provider` component to provide context in a React tree.

```scala
div(
  myContext.Provider(value = "hello!")(
    ...
  )
)
```

By using the `Provider` component, all elements beneath it will now have access to the `hello!` value provided.

## Consuming Context
To consume context, use the `Consumer` component and pass in a function that takes the context value and returns a React element.

```scala
myContext.Consumer { value =>
  h1(s"the context value is $value")
}
```
