package slinky.docs

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}
import scala.scalajs.LinkingInfo

import slinky.docs.homepage.Homepage
import slinky.history.History
import slinky.web.{ReactDOM, ReactDOMServer}
import slinky.hot
import slinky.reactrouter._
import slinky.universalanalytics.UniversalAnalytics
import slinky.web.html.{div, style}

import org.scalajs.dom
import org.scalajs.dom.History

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def setupAnalytics(): History = {
    val visitor = UniversalAnalytics("UA-54128141-3", js.Dynamic.literal(https = true))
    val history = History.createBrowserHistory()
    visitor.pageview(js.Dynamic.literal(
      dp = dom.window.location.pathname,
      dr = dom.document.referrer
    )).send()
    history.listen(() => {
      visitor.pageview(dom.window.location.pathname).send()
    })

    history
  }

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

    setupAnalytics()

    ReactDOM.render(
      Router(history = setupAnalytics())(
        div(
          Navbar(),
          div(style := js.Dynamic.literal(
            marginTop = "60px"
          ))(
            Switch(
              Route("/", Homepage, exact = true),
              Route("/docs/*", DocsPage),
              Route("*", Homepage)
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
              Route("/docs/*", DocsPage),
              Route("*", Homepage)
            )
          )
        )
      )
    )
  }

  @JSExportTopLevel("entrypoint.hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")

    setupAnalytics()

    ReactDOM.hydrate(
      Router(history = setupAnalytics())(
        div(
          Navbar(),
          div(style := js.Dynamic.literal(
            marginTop = "60px"
          ))(
            Switch(
              Route("/", Homepage, exact = true),
              Route("/docs/*", DocsPage),
              Route("*", Homepage)
            )
          )
        )
      ),
      container
    )
  }
}
