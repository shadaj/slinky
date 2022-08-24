package slinky.docs

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-syntax-highlighter", "Light")
object SyntaxHighlighterComp extends js.Object {
  def registerLanguage(arg1: String, arg2: js.Object): Unit = js.native
}

@js.native
@JSImport("react-syntax-highlighter/dist/cjs/languages/hljs/scala", JSImport.Default)
object ScalaHighlightLanguage extends js.Object

@react object SyntaxHighlighter extends ExternalComponent {
  SyntaxHighlighterComp.registerLanguage("scala", ScalaHighlightLanguage)
  val component = SyntaxHighlighterComp
  case class Props(language: String, style: js.Dictionary[js.Object])
}
