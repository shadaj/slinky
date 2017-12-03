package me.shadaj.slinky.docs

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.html._
import sourcecode.Text

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

@react class CodeExampleInternal extends Component {
  case class Props(codeText: String, demoElement: ReactElement)

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
      lineHeight = "20px"
    ),
    "code[class*=\"language-\"]" -> literal(
      backgroundColor = "#282c34",
      color = "#ffffff"
    )
  )

  override def render(): ReactElement = {
    div(style := literal(
      width = "100%",
      display = "flex",
      borderRadius = "10px",
      overflow = "hidden",
      border = "1px solid rgb(236, 236, 236)",
      height = "100%"
    ))(
      div(style := literal(
        width = "65%",
        height = "100%"
      ))(
        div(style := literal(
          width = "100%",
          display = "block",
          backgroundColor = "rgb(32, 35, 42)",
          padding = "10px",
          boxSizing = "border-box"
        ))(
          b(style := literal(color = "#999"))("SCALA CODE")
        ),
        div(style := literal(
          width = "100%",
          display = "block",
          padding = "10px",
          backgroundColor = "#282c34",
          boxSizing = "border-box",
          height = "calc(100% - 36px)",
          overflow = "auto"
        ))(
          SyntaxHighlighter(language = "scala", style = prismColors)(
            props.codeText
          )
        )
      ),
      div(style := literal(
        width = "35%",
        boxSizing = "border-box",
        borderLeft = "1px solid rgb(236, 236, 236)"
      ))(
        div(style := literal(
          backgroundColor = "rgb(236, 236, 236)",
          padding = "10px",
          boxSizing = "border-box"
        ))(
          b(style := literal(color = "rgb(109, 109, 109)"))("RESULT")
        ),
        div(style := literal(
          overflow = "auto",
          padding = "10px",
          boxSizing = "border-box"
        ))(props.demoElement)
      )
    )
  }
}

object CodeExample {
  def apply(exampleLocation: String): ReactElement = macro CodeExampleImpl.text
}