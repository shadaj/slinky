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
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.1.1" // Hot loading, requires react-proxy package
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.1.1" // Interop with japgolly/scalajs-react

// optional, enables the @react macro annotation API
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
```

Slinky supports loading React via either CommonJS or as a global object. If loading as a global object, make sure React is available
as `window.React` and React DOM as `window.ReactDOM`.

## Writing Components
Writing React code in Slinky closely mirrors the layout of React code in ES6. Slinky components can be used by calling the `apply` method and passing in an instance of `Props`.

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

// elsewhere...
HelloMessage(HelloMessage.Props("World"))
```

### ES6
```js
import React from 'react';

class HelloMessage extends React.Component {
  render() {
    return <div>Hello {this.props.name}</div>;
  }
}

// elsewhere...
<HelloMessage name="World"/>
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
import me.shadaj.slinky.core.ExternalComponent

import scala.scalajs.js

import org.scalajs.dom.html

// external component for react-three-renderer
object React3 extends ExternalComponent {
  case class Props(mainCamera: String, width: Int, height: Int,
                   onAnimate: Option[() => Unit] = None, alpha: Boolean = false)

  override val component: js.Object = js.Dynamic.global.React3.asInstanceOf[js.Object]
}
```

With Slinky's built in typeclass derivation for converting between Scala and JavaScript types,
we can describe the properties of the external component using idiomatic Scala, using types like Option
that will be converted into a JS representation at runtime (in this case the value for Some and undefined for None).

## `@react` Macro Annotation (experimental)
The experimental `@react` macro annotation (available in versions > 0.1.1) makes it possible to directly write the class containing component logic and have Slinky generate
the companion object for constructing component instances. The macro annotation also now generates special `apply` methods when your Props is a case class
so that constructing Scala components looks more similar to JSX, with the Props values directly taken as parameters of the `apply`.

As an example of migrating an existing component to the new macro annotation style, take a simple component that displays a header:
```scala
import me.shadaj.slinky.core.WrapperComponent
import me.shadaj.slinky.web.html._

object HelloMessage extends WrapperComponent {
  case class Props(name: String)
  type State = Unit

  @ScalaJSDefined
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def render() = {
      div(s"Hello ${props.name}")
    }
  }
}
``` 

to use the new macro annotation style, we essentially extract out the definition class, move the `Props` and `State` types into the class, and extend `Component` instead of `Definition`:
```scala
import me.shadaj.core.{Component, react}
import me.shadaj.slinky.web.html._

@react class HelloMessage extends Component {
  case class Props(name: String)
  type State = Unit
  
  def render() = {
    div(s"Hello ${props.name}")
  }
}
```

To use this component, we now have a new option for constructing it directly passing in the Props values
```scala
HelloMessage(HelloMessage.Props("Shadaj")) // old style
HelloMessage("Shadaj") // now possible!
HelloMessage(name = "Shadaj") // now possible, closest to JSX
```

The `@react` annotation is also available for external components. For external components, the annotation generates the new `apply` method style in the same style as Scala components.
```scala
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.ExternalComponent

@react object React3 extends ExternalComponent {
  case class Props(mainCamera: String, width: Int, height: Int,
                   onAnimate: Option[() => Unit] = None, alpha: Boolean = false)

  override val component: js.Object = js.Dynamic.global.React3.asInstanceOf[js.Object]
}
```

this makes it possible to construct the external component as
```scala
React3(mainCamera = "camera", width = 800, height = 800)
```

## Credits
Much credit goes to existing Scala.js libraries for React, such as scalajs-react and SRI, which provided a lot of inspiration for Slinky's design. Credit also goes to scala-js-preact, which provided the inspiration for the `@react` macro annotation. 

Slinky logo is based on https://thenounproject.com/dianatomic/uploads/?i=40452
