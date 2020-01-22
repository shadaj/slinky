package slinky.docs

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-syntax-highlighter/light", JSImport.Default)
object SyntaxHighlighterComp extends js.Object

@js.native
@JSImport("react-syntax-highlighter/light", "registerLanguage")
object RegisterLanguageFunc extends js.Function2[String, js.Object, Unit] {
  override def apply(arg1: String, arg2: js.Object): Unit = js.native
}

@js.native
@JSImport("react-syntax-highlighter/languages/hljs/scala", JSImport.Default)
object ScalaHighlightLanguage extends js.Object

@react object SyntaxHighlighter extends ExternalComponent {
  RegisterLanguageFunc("scala", ScalaHighlightLanguage)
  val component = SyntaxHighlighterComp
  case class Props(language: String, style: js.Dictionary[js.Object])
}
