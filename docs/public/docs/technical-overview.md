# Technical Overview
This document explains the core features of Slinky and the technical decisions made when implementing them.

Slinky’s core is formed by the component API. Building on it, macro annotations simplify writing new components. Through the concept of typeclasses, Slinky supports interoperability with components from JavaScript ecosystem. Furthermore, Slinky features hot-loading to improve development cycle.

## Component API
Slinky provides a component API that allows developers to write type-safe React components with minimal boilerplate. With the native ECMAScript 6 (ES6) API used by most React developers, components are defined in ES6 classes that implement a standard set of component lifecycle methods. Slinky mirrors this style, allowing developers to define their own component classes in Scala that are later transformed to JavaScript when interfacing with React. In addition to providing a familiar API, this strategy ensures that Slinky can support new React features in the future, since at its core Slinky uses the same API that JavaScript developers use.

To make components type-safe in the data they handle, Slinky requires the component implementation class to be defined inside a Scala object that contains the dependent-object-types `Props` and `State`. Most of the time, `Props` and `State` are defined as case classes, the primary way to model immutable data in Scala, but can also be defined to alias external types.

With this API, a simple component looks like this:
```scala
object MyComponent extends ComponentWrapper {
  case class Props(name: String)
  case class State(currentIndex: Int)
  
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def initialState = State(currentIndex = 0)
    
    def render = {
      h1(props.name, s"index is ${state.currentIndex}")
    }
  }

  def apply(name: String) = {
    this.apply(Props(name = String))
  }
}
```

The `ComponentWrapper` class defines the abstract `Props` and `State` types as well as the `Definition` type, which is a layer on top of React’s Component class. At runtime, the `Def` class is converted into a React Component, which can be constructed to add to an HTML tree. This process is handled by the apply method in ComponentWrapper, which takes the properties to pass into the constructed component and returns a `ReactElement`, which is the type used to represent constructed instances of components as well as other React tree types such as HTML trees, React portals, and React fragments.

## Macro Annotation Components
Although the `ComponentWrapper` API handles construction of components with a straightforward interface, there is still some boilerplate involved in creating a JavaScript class that can be passed into React’s API. To alleviate this effort involved in creating a new component, Slinky includes the `@react` macro annotation API.

One of Scala’s most powerful features is the ability to plug into compile phases and add custom behavior, such as modification of ASTs or generating custom files (which projects like Scala.js and Scala Native take advantage of to target platforms other than the JVM). Scala.meta is a library that gives developers a high-level API for code transformations, tapping into an early phase of the compiler to automatically run these transformations at compile time. Slinky uses this library to generate the classes for components, allowing developers to write their code with no boilerplate. To request Slinky to generate these classes, developers simply need to annotate their component code with `@react`, which is detected at compile time to call Slinky’s AST transformer.

With this API, the component above now gets reduced and better matches the equivalent JavaScript code:
```scala
@react class MyComponent extends Component {
  case class Props(name: String)
  case class State(currentIndex: Int)
  
  def initialState = State(currentIndex = 0)
    
  def render = {
    h1(props.name, s"index is ${state.currentIndex}")
  }
}
```

In addition to being run by the Scala compiler, IDEs like IntelliJ IDEA also integrate with custom macro annotations like `@react`, so developers get code assistance based on the final generated code. Slinky strives to make developer experience as smooth as possible. To this effect, it ensures that its annotation processing is compatible with current IDEs, even when it requires significant engineering effort specific to individual developer tools.

## Readers and Writers
Some React libraries, like `react-router` (which handles multi-page React websites), accept component classes as properties, instead of more common component instances. Traditional React interfaces for Scala.js only store the Scala representations of props and state, which are incompatible to what JavaScript libraries accept and makes libraries like `react-router` unusable because they expect components to accept pure JavaScript props and state. To tackle this issue, Slinky includes the concept of Readers and Writers, which provide automatic conversions between Scala and JavaScript types.

The Reader/Writer API builds upon typeclasses, a concept popularized by the Haskell programming language that makes it easy to define and implement behavior tied to specific types. Typeclasses can be defined for a variety of extensions like serialization, where individual typeclass instances handle the serialization of specific types. The powerful idea of typeclasses is that they can be composed, with complex typeclasses based on simpler ones for primitive types.

Slinky includes the `Reader` and `Writer` typeclasses, which respectively handler reading JavaScript values into Scala types and writing Scala values into what a JavaScript library would expect. In addition to providing conversions for primitive types, Slinky also handles conversions between the idiomatic representations of shared concepts between the two languages, such as JavaScript `Promise`s and Scala `Future`s.

Coming back to the concept of generating code at compile time with macros, Slinky also includes macros (powered by [Magnolia](http://magnolia.work/)) that automatically generate typeclasses for case classes. So a case class like this:

```scala
case class Props(a: Int, b: String, c: Future[String])
```

can have typeclasses recursively generated like this (not legal Scala code; shortened to illustrate the core ideas)

```scala
def readerFor[T]: Reader[T] = // derives typeclass for T

// readerFor[Props] expands to
new Reader[Props] {
  def read(jsValue: js.Object): Props = {
    Props(
      a = readerFor[Int].read(jsValue.a),
      b = readerFor[String].read(jsValue.b),
      c = readerFor[Future[String]].read(jsValue.c)
    )
  }
}
```

This process of generating readers, called typeclass derivation, recursively finds and uses the typeclasses for each field in the type, making it possible to automatically generate typeclasses for complex types by recursively breaking down the type. With this API, Slinky can read JavaScript values passed in from external JavaScript code and provide that data to the developer as Scala types they are familiar with.

Because typeclasses can be generated without any end-user code, interfaces to external JavaScript components look very simple, with Slinky doing the work internally to convert the Scala types in the user’s code to the JavaScript data expected by the external component.

```scala
@react object Route extends ExternalComponent {
  case class Props(path: String,
                   component: ReactComponentClass,
                   exact: Boolean = false)
  override val component = ReactRouterDOM.Route
}
```

With this API, a user simply needs to define the props type and provide a reference to the React component to render, and Slinky handles the rest!

## Hot Loading
Typically, developers implement features and fix bugs by making a small change, seeing its effect and making another change, and so on. Improving developer productivity for this workflow requires minimizing time it takes for each iteration. To aid developers in this aspect, Slinky implements one of React’s most popular features--hot loading. Hot loading makes it possible for developers to have very efficient cycles of development and testing, since they can modify a single component and see the results without having to reload the webpage and lose all the data being stored in memory. Combined with JavaScript tools like Webpack, Slinky easily integrates into this flow, making it possible to edit Scala code for React components and see the results instantaneously instead of having to wait for a webpage reload and navigating back to the component being tested.

In Slinky, enabling the hot-loading features is a one liner, but many changes go on behind the scenes to set up the application for preserving its state across multiple instances of Scala.js code

```scala
hot.initialize()
```

Slinky’s design decisions surrounding readers and writers make it possible to implement this feature. When Scala.js code initializes, all of the object instances are valid for only that execution, and cannot be used from other versions of that Scala.js code. This poses a problem as the website must be reloaded every time the Scala code changes so there are no conflicts between original objects and what the new version expects. But with the Reader/Writer API in Slinky, we can solve this by sharing data across successive versions of code through the written JavaScript versions. With this storage format, an updated version of Scala.js code can read that data into its own instances of Scala objects. The result is fast development cycles where a developer can see results of code changes instantaneously.
