# Interop with scalajs-react
If you're using Slinky in an application that's already using [scalajs-react](https://github.com/japgolly/scalajs-react), Slinky comes with the `slinky-scalajsreact-interop` module for crossing over between the two styles of writing React code.

```scala
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.7.3"
```

To use this module, simply import the implicit conversions between Slinky and scalajs-react types.

```scala
import slinky.scalajsreact.Converters._
```

Then use the converters `.toSlinky` and `.toScalaJSReact` to convert React elements from each library to the other.

This makes it possible to use Slinky components from scalajs-react and vice-versa. For example, the following code can now work:

```scala
val ScalajsReactComponent =
  ScalaComponent.builder[String]("Hello")
    .render_P(name => <.div( // a scalajs-react tag here
      "Hello, ", name,
      "This is a component from scalajs-react",
      div( // a Slinky tag being used here
        "and this is from Slinky inside the scalajs-react component!"
      ).toScalaJSReact
    ))
    .build
    
@react class SlinkyComponent extends StatelessComponent {
  case class Props(name: String)

  def render(): ReactElement = {
    ScalajsReactComponent(props.name).toSlinky
  }
}

SlinkyComponent("Interop works!")
```
