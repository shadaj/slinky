package me.shadaj.slinky.docs

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.{Fragment, ReactElement}
import me.shadaj.slinky.remarkreact.{ReactRenderer, Remark}
import me.shadaj.slinky.web.html._
import org.scalajs.dom.raw.XMLHttpRequest

import scala.scalajs.js
import js.Dynamic.literal

@react class RemarkCode extends Component {
  case class Props(children: Seq[String])

  // from the reactjs.org theme
  val prismColors = js.Dictionary[js.Object](
    "hljs-comment" -> literal(color = "#999999"),
    "hljs-keyword" -> literal(color = "#c5a5c5"),
    "hljs-built_in" -> literal(color = "#5a9bcf"),
    "hljs-string" -> literal(color = "#8dc891"),
    "hljs-variable" -> literal(color = "#d7deea"),
    "hljs-title" -> literal(color = "#79b6f2"),
    "hljs-type" -> literal(color = "#FAC863"),
    "hljs-meta" -> literal(color = "#FAC863"),
    "hljs-strong" -> literal(fontWeight = 700),
    "hljs-emphasis" -> literal(fontStyle = "italic"),
    "hljs" -> literal(
      backgroundColor = "#282c34",
      color = "#ffffff",
      fontSize = "15px",
      lineHeight = "20px",
      padding = "20px",
      overflow = "auto"
    ),
    "code[class*=\"language-\"]" -> literal(
      backgroundColor = "#282c34",
      color = "#ffffff"
    )
  )

  override def render(): ReactElement = {
    if (props.children.head.contains('\n')) {
      div(className := "code-block", style := literal(
        borderRadius = "10px",
        overflow = "hidden"
      ))(
        SyntaxHighlighter(language = "scala", style = prismColors)(
          props.children.head
        )
      )
    } else {
      code(props.children.head)
    }
  }
}

@react class RemarkH2 extends Component {
  case class Props(children: Seq[String])

  override def render(): ReactElement = {
    Fragment(
      hr(style := literal(
        height = "1px",
        marginBottom = "-1px",
        border = "none",
        borderBottom = "1px solid #ececec",
        marginTop = "40px"
      )),
      h2(props.children.head)
    )
  }
}

@react class DocsPage extends Component {
  type Props = js.Dynamic
  type State = Option[String]

  val tree: Map[String, List[(String, String)]] = Map(
    "Quick Start" -> List(
      "Installation" -> "/docs/installation"
    )
  )

  def docsFilePath = {
    val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
    s"/docs/${matchString.reverse.dropWhile(_ == '/').reverse}.md"
  }

  override def initialState: Option[String] = {
    if (js.typeOf(js.Dynamic.global.window.getPublic) != "undefined") {
      Some(js.Dynamic.global.window.getPublic(docsFilePath).asInstanceOf[String])
    } else if (js.typeOf(js.Dynamic.global.window.publicSSR) != "undefined") {
      js.Dynamic.global.window.publicSSR.asInstanceOf[js.Dictionary[String]].get(docsFilePath)
    } else {
      None
    }
  }

  override def componentDidMount(): Unit = {
    if (state.isEmpty) {
      val xhr = new XMLHttpRequest
      xhr.onload = _ => {
        setState(Some(xhr.responseText))
      }

      xhr.open("GET", docsFilePath)
      xhr.send()
    }
  }

  override def render(): ReactElement = {
    MainPageContent(
      div(style := literal(
        display = "flex",
        flexDirection = "row"
      ))(
        div(style := literal(
          width = "calc(100% - 300px)"
        ))(
          if (state.isDefined) {
            Remark().use(ReactRenderer, literal(
              remarkReactComponents = literal(
                h2 = RemarkH2.componentConstructor,
                code = RemarkCode.componentConstructor
              )
            )).processSync(state.get).contents
          } else {
            h1("Loading")
          }
        ),
        div(style := literal(
          width = "300px"
        ))(
          div(
            style := literal(
              position = "fixed",
              top = "60px",
              height = "calc(100vh - 60px)",
              backgroundColor = "#f7f7f7",
              borderLeft = "1px solid #ececec",
              marginLeft = "20px",
              padding = "20px",
              paddingTop = "40px",
              paddingRight = "1000px",
              boxSizing = "border-box"
            )
          )(
            "hello!"
          )
        )
      )
    )
  }
}
