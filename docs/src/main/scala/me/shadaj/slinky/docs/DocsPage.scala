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

object DocsTree {
  val tree: Map[String, List[(String, String)]] = Map(
    "Quick Start" -> List(
      "Installation" -> "/docs/installation/",
      "Hello World!" -> "/docs/hello-world/",
      "The Tag API" -> "/docs/the-tag-api/",
      "Writing Components" -> "/docs/writing-components/"
    )
  )
}

import DocsTree._

@react class DocsPage extends Component {
  type Props = js.Dynamic
  case class State(selectedGroup: String, document: Option[String])

  def docsFilePath(props: js.Dynamic) = {
    val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
    s"/docs/${matchString.reverse.dropWhile(_ == '/').reverse}.md"
  }

  override def initialState: State = {
    val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
    val group = tree.find(_._2.exists(_._2 == s"/docs/$matchString")).get._1

    if (js.typeOf(js.Dynamic.global.window.getPublic) != "undefined") {
      State(group, Some(js.Dynamic.global.window.getPublic(docsFilePath(props)).asInstanceOf[String]))
    } else if (js.typeOf(js.Dynamic.global.window.publicSSR) != "undefined") {
      State(group, js.Dynamic.global.window.publicSSR.asInstanceOf[js.Dictionary[String]].get(docsFilePath(props)))
    } else {
      State(group, None)
    }
  }

  override def componentDidMount(): Unit = {
    if (state.document.isEmpty) {
      val xhr = new XMLHttpRequest
      xhr.onload = _ => {
        setState(state.copy(document = Some(xhr.responseText)))
      }

      xhr.open("GET", docsFilePath(props))
      xhr.send()
    }
  }

  override def componentDidUpdate(prevProps: Props, prevState: State): Unit = {
    if (docsFilePath(props) != docsFilePath(prevProps)) {
      val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
      val group = tree.find(_._2.exists(_._2 == s"/docs/$matchString")).get._1

      val xhr = new XMLHttpRequest
      xhr.onload = _ => {
        setState(State(group, Some(xhr.responseText)))
      }

      xhr.open("GET", docsFilePath(props))
      xhr.send()
    }
  }

  def requestOpen(group: String): Function0[Unit] = {
    new Function0[Unit] {
      override def apply(): Unit = {
        setState(state.copy(selectedGroup = group))
      }
    }
  }

  override def render(): ReactElement = {
    div(className := "article fill-right", style := literal(
      marginTop = "40px",
      paddingLeft = "15px",
      boxSizing = "border-box"
    ))(
      div(style := literal(
        display = "flex",
        flexDirection = "row"
      ))(
        div(style := literal(
          width = "calc(100% - 300px)"
        ))(
          div(style := literal(maxWidth = "1400px"))(
            state.document.map { t =>
              Remark().use(ReactRenderer, literal(
                remarkReactComponents = literal(
                  h2 = RemarkH2.componentConstructor,
                  code = RemarkCode.componentConstructor
                )
              )).processSync(t).contents
            }
          )
        ),
        div(style := literal(
          width = "300px",
          marginLeft = "20px"
        ))(
          div(
            style := literal(
              position = "fixed",
              top = "60px",
              height = "calc(100vh - 60px)",
              backgroundColor = "#f7f7f7",
              borderLeft = "1px solid #ececec",
              paddingTop = "40px",
              paddingRight = "1000px",
              boxSizing = "border-box"
            )
          )(
            nav(style := literal(
              position = "relative",
              paddingLeft = "20px",
              width = "300px"
            ))(
              tree.keys.toList.map { group =>
                DocsGroup(
                  name = group,
                  isOpen = group == state.selectedGroup,
                  onRequestOpen = requestOpen(group),
                  children = tree(group)
                ).withKey(group)
              }
            )
          )
        )
      )
    )
  }
}
