<p align="center"><img width="400" src="https://github.com/shadaj/slinky/raw/master/logo.png"/></p>
<p align="center"><i>Write Scala.js React apps just like you would in ES6</i></p>
<p align="center">
  <a href="https://travis-ci.org/shadaj/slinky">
    <img src="https://travis-ci.org/shadaj/slinky.svg?branch=master"/>
  </a>
  <a href="https://www.scala-js.org">
    <img src="https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg"/>
  </a>
  <img src="https://img.shields.io/maven-central/v/me.shadaj/slinky-core_sjs0.6_2.12.svg"/>
  <a href="https://gitter.im/shadaj/slinky?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge">
    <img src="https://badges.gitter.im/shadaj/slinky.svg"/>
  </a>
</p>

## Installation
Add the dependencies that match your application:
```scala
libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.1.1" // core React functionality, no React DOM
libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.1.1" // React DOM, HTML and SVG tags
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.1.1" // Hot loading with Webpack
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.1.1" // Interop with japgolly/scalajs-react

// optional, enables the @react macro annotation API
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
```

Slinky supports loading React via either CommonJS or as a global object. If loading as a global object, make sure React is available
as `window.React` and React DOM as `window.ReactDOM`.

## Writing Components
Writing React code in Slinky closely mirrors the layout of React code in ES6.

### Slinky
```scala
import me.shadaj.slinky.core.StatelessComponentWrapper
import me.shadaj.slinky.web.html._

object HelloMessage extends StatelessComponentWrapper {
  case class Props(name: String)

  @ScalaJSDefined
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def render() = {
      div(s"Hello ${props.name}")
    }
  }
}

// OR with the macro annotation enabled

import me.shadaj.slinky.core.{Component, react}
import me.shadaj.slinky.web.html._

@react class HelloMessage extends Component {
  case class Props(name: String)
  type State = Unit // this is a stateless component
  
  def initialState = () // no state

  def render() = {
    div(s"Hello ${props.name}")
  }
}
```

### ES6
```js
import React from 'react';

class HelloMessage extends React.Component {
  render() {
    return <div>Hello {this.props.name}</div>;
  }
}
```

To create stateful components, specify the `State` type, provide an initial state, and use your state in `render` via the `state` variable:
```scala
import me.shadaj.slinky.core.ComponentWrapper
import me.shadaj.slinky.web.html._

object HelloMessage extends ComponentWrapper {
  type Props = Unit // we have no props
  type State = Int // we use an Int directly for state, but we could also have used a case class

  @ScalaJSDefined
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def initialState = 0

    def render() = {
      a(onClick := (() => setState(state + 1)))(s"Clicks: ${state}")
    }
  }
}

// OR with the macro annotation enabled

import me.shadaj.slinky.core.{Component, react}
import me.shadaj.slinky.web.html._

@react class HelloMessage extends Component {
  type Props = Unit // we have no props
  type State = Int // we use an Int directly for state, but we could also have used a case class
  
  def initialState = 0 // no state

  def render() = {
    a(onClick := (() => setState(state + 1)))(s"Clicks: ${state}")
  }
}
```

## Tags
Slinky uses the same tag building syntax as ScalaTags and scalajs-react.

To create an HTML element, simply import the tags module and follow the same style as regular Scala tag libraries:
```scala
import me.shadaj.slinky.web.html._

a(href := "http://example.com")("Example")
```

Slinky also includes typing for detecting use of incompatible attributes at compile time:
```scala
import me.shadaj.slinky.web.html._

div(href := "http://candivshavehrefs.com") // compile error!
```

## External Components
One of Slinky's most powerful features is the ability to use external React components without any boilerplate. Setting
up an external component is just like creating a regular component:

```scala
import me.shadaj.slinky.core.{ExternalComponent, react}

import scala.scalajs.js

import org.scalajs.dom.html

// external component for react-three-renderer
@react // optional, generates an apply method that directly takes the props values
object React3 extends ExternalComponent {
  case class Props(mainCamera: String, width: Int, height: Int,
                   onAnimate: Option[() => Unit] = None, alpha: Boolean = false)

  override val component: js.Object = js.Dynamic.global.React3.asInstanceOf[js.Object]
}
```

With Slinky's built in typeclass derivation for converting between Scala and JavaScript types,
we can describe the properties of the external component using idiomatic Scala, using types like Option
that will be converted into a JS representation at runtime (in this case the value for Some and undefined for None).

## Credits
Much credit goes to existing Scala.js libraries for React, such as scalajs-react and SRI, which provided a lot of inspiration for Slinky's design. Credit also goes to scala-js-preact, which provided the inspiration for the `@react` macro annotation. 

Slinky logo is based on https://thenounproject.com/dianatomic/uploads/?i=40452
