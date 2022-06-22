package slinky.docs

import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.docs.MainPageContent
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.JSImport

import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.ReactComponentClass
import slinky.core.facade.Fragment
import slinky.core.ExternalComponentNoProps
import slinky.core.CustomAttribute

import slinky.next.Head

@JSImport("resources/index.module.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

@react object App {
  val css = AppCSS

  val charSet = CustomAttribute[String]("charSet")

  case class Props(Component: ReactComponentClass[Unit])
  val component = FunctionalComponent[Props] { props =>
    Fragment(
      Head(
        style(dangerouslySetInnerHTML := js.Dynamic.literal(__html =
          """body {
            |  margin: 0;
            |  padding: 0;
            |  font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Fira Sans,Droid Sans,Helvetica Neue,sans-serif;
            |  line-height: 1;
            |  font-weight: 400;
            |  -webkit-font-smoothing: antialiased;
            |}
            |h3 {
            |  font-size: 25px;
            |  font-weight: 700;
            |  margin-top: 0;
            |}
            |
            |code {
            |  font-family: source-code-pro,Menlo,Monaco,Consolas,Courier New,monospace;
            |  padding: 0;
            |}
            |
            |code.code-block {
            |  background-color: #282c34;
            |}
            |
            |p {
            |  font-size: 17px;
            |  line-height: 28.9px
            |}
            |
            |pre {
            |  margin: 0 0 17px;
            |}
            |
            |h1 {
            |  font-size: 60px;
            |}
            |
            |h2 {
            |  font-size: 35px;
            |}
            |
            |a {
            |  text-decoration: none;
            |}
            |
            |table {
            |  border-collapse: collapse;
            |  width: 100%;
            |}
            |
            |td, th {
            |  border-top: 1px solid #ddd;
            |  padding: 8px;
            |  text-align: left;
            |}""".stripMargin
        ))
      ),
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
        // props.Component(())
      )
    )
  }

  object Next {
    import slinky.core.ReactComponentClass
    import scala.scalajs.js.annotation.JSExportTopLevel

    @JSExportTopLevel(name = "default", moduleID = "_app")
    val component: ReactComponentClass[_] = App.component
  }
}
