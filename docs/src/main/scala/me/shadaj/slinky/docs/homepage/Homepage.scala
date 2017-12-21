package me.shadaj.slinky.docs.homepage

import me.shadaj.slinky.core.{Component, StatelessComponent, StatelessComponentWrapper}
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.docs.{MainPageContent, Navbar}
import me.shadaj.slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.JSImport

@JSImport("resources/slinky-logo-horizontal.svg", JSImport.Default)
@js.native
object SlinkyHorizontalLogo extends js.Object

@JSImport("resources/slinky-logo.svg", JSImport.Default)
@js.native
object SlinkyLogo extends js.Object

@react class Homepage extends StatelessComponent {
  type Props = Unit

  def render() = {
    div(
      Jumbotron(),
      MainPageContent(
        div(style := literal(
          width = "100%",
          overflow = "auto",
          marginTop = "40px"
        ))(
          div(style := literal(
            width = "100%",
            minWidth = "800px",
            display = "flex",
            flexDirection = "row",
            justifyContent = "space-around",
          ))(
            div(style := literal(width = "33%"))(
              h3(style := literal(fontWeight = 100))("Just like ES6"),
              p("Slinky has a strong focus on mirroring the ES6 API. This means that any documentation or examples for ES6 React can be easily applied to your Scala code."),
              p("There are no new patterns involved with using Slinky. Just write React apps like you would in any other language!")
            ),
            div(style := literal(width = "33%"))(
              h3(style := literal(fontWeight = 100))("Complete Interop"),
              p("Slinky provides straightforward APIs for using external components. Simply define the component's properties using standard Scala types and you're good to go!"),
              p("In addition, Slinky components can be used from JavaScript code, thanks to a built in Scala to JS mappings. This means that your favorite libraries like React Router work out of the box with Slinky!")
            ),
            div(style := literal(width = "33%"))(
              h3(style := literal(fontWeight = 100))("First-Class Dev Experience"),
              p("Writing web applications with Scala doesn't have to feel like a degraded development experience. Slinky comes ready with full integration with familiar tools like Webpack and React DevTools."),
              p("Slinky also comes with built-in support for hot-loading via Webpack, allowing you to make your code-test-repeat flow even faster!")
            )
          )
        ),
        hr(style := literal(
          height = "1px",
          marginBottom = "-1px",
          border = "none",
          borderBottom = "1px solid #ececec",
          marginTop = "40px"
        )),
        Examples()
      )
    )
  }
}
