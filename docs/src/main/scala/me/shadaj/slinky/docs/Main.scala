package me.shadaj.slinky.docs

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}
import scala.scalajs.LinkingInfo
import me.shadaj.slinky.core._
import me.shadaj.slinky.docs.homepage.Homepage
import me.shadaj.slinky.web.{ReactDOM, ReactDOMServer}
import me.shadaj.slinky.hot
import me.shadaj.slinky.reactrouter.{BrowserRouter, Route, StaticRouter, Switch}
import me.shadaj.slinky.web.html.{div, style}
import org.scalajs.dom

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  @JSExportTopLevel("entrypoint.main")
  def main(): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    ReactDOM.render(
      BrowserRouter(
        div(
          Navbar(),
          div(style := js.Dynamic.literal(
            marginTop = "60px"
          ))(
            Switch(
              Route("/", Homepage, exact = true),
              Route("/docs/*", DocsPage)
            )
          )
        )
      ),
      container
    )
  }

  @JSExportTopLevel("entrypoint.ssr")
  def ssr(path: String): String = {
    ReactDOMServer.renderToString(
      StaticRouter(location = path, context = js.Dynamic.literal())(
        div(
          Navbar(),
          div(style := js.Dynamic.literal(
            marginTop = "60px"
          ))(
            Switch(
              Route("/", Homepage, exact = true),
              Route("/docs/*", DocsPage)
            )
          )
        )
      )
    )
  }

  @JSExportTopLevel("entrypoint.hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")

    ReactDOM.hydrate(
      BrowserRouter(
        div(
          Navbar(),
          div(style := js.Dynamic.literal(
            marginTop = "60px"
          ))(
            Switch(
              Route("/", Homepage, exact = true),
              Route("/docs/*", DocsPage)
            )
          )
        )
      ),
      container
    )
  }
}
