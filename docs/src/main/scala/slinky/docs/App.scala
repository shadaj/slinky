package slinky.docs

import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js

import slinky.core.FunctionalComponent
import slinky.core.ReactComponentClass
import slinky.core.facade.Fragment
import slinky.core.CustomAttribute

import slinky.next.Head

// @JSImport("resources/index.module.css", JSImport.Default)
// @js.native
// object AppCSS extends js.Object

@react object App {
  // val css = AppCSS

  val charSet = CustomAttribute[String]("charSet")

  case class Props(Component: ReactComponentClass[js.Object], pageProps: js.Object)
  val component = FunctionalComponent[Props] { props =>
    Fragment(
      Head(
        meta(charSet := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
        meta(name := "theme-color", content := "#000000"),
        link(rel := "manifest", href := "/manifest.json"),
        link(rel := "shortcut icon", href := "/favicon.ico"),
        title(s"Slinky - Write React apps in Scala just like ES6")
      ),
      Navbar(()),
      div(style := js.Dynamic.literal(
        marginTop = "60px"
      ))(
        props.Component(props.pageProps)
      )
    )
  }

  object Next {
    import slinky.core.ReactComponentClass
    import scala.scalajs.js.annotation.JSExportTopLevel

    @JSExportTopLevel(name = "component", moduleID = "_app")
    def component(): ReactComponentClass[_] = App.component
  }
}
