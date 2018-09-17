package slinky.docs

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}
import scala.scalajs.LinkingInfo
import slinky.docs.homepage.Homepage
import slinky.history.History
import slinky.web.{ReactDOM, ReactDOMServer}
import slinky.hot
import slinky.reactrouter._
import slinky.analytics.ReactGA
import slinky.web.html._
import org.scalajs.dom
import org.scalajs.dom.History
import slinky.core.CustomAttribute
import slinky.core.facade.ReactElement
import slinky.reacthelmet.Helmet

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def setupAnalytics(): History = {
    ReactGA.initialize("UA-54128141-3")
    val history = History.createBrowserHistory()

    ReactGA.pageview(dom.window.location.pathname)

    history.listen(() => {
      ReactGA.pageview(dom.window.location.pathname)
    })

    history
  }

  def insideRouter: ReactElement = {
    val charSet = new CustomAttribute[String]("charSet")
    div(
      Helmet(
        meta(charSet := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
        meta(name := "theme-color", content := "#000000"),
        link(rel := "manifest", href := "/manifest.json"),
        link(rel := "shortcut icon", href := "/favicon.ico"),
        title(s"Slinky - Write React apps in Scala just like ES6"),
        style(`type` := "text/css")(IndexCSS.toString)
      ),
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

    ReactDOM.render(
      Router(history = setupAnalytics())(
        insideRouter
      ),
      container
    )
  }

  var isSSR = false

  @JSExportTopLevel("entrypoint.ssr")
  def ssr(path: String): String = {
    isSSR = true
    TrackSSRDocs.publicSSR = js.Dictionary.empty

    val reactTree = ReactDOMServer.renderToString(
      StaticRouter(location = path, context = js.Dynamic.literal())(
        insideRouter
      )
    )

    s"""<!DOCTYPE html>
       |<html>
       |  <head>
       |    ${dom.document.getElementsByTagName("head")(0).innerHTML}
       |  </head>
       |  <body>
       |    <div id="root">
       |      $reactTree
       |    </div>
       |    <script type="text/javascript">window.publicSSR = ${js.JSON.stringify(TrackSSRDocs.publicSSR)}</script>
       |    <script async src="/slinky-docs-opt-bundle.js"></script>
       |  </body>
       |</html>""".stripMargin
  }

  @JSExportTopLevel("entrypoint.hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")

    ReactDOM.hydrate(
      Router(history = setupAnalytics())(
        insideRouter
      ),
      container
    )
  }
}
