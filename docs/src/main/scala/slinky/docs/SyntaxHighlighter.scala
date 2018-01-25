package slinky.docs

import slinky.core.ExternalComponent
import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.|

@js.native
@JSImport("react-syntax-highlighter", JSImport.Default)
object SyntaxHighlighterComp extends js.Object

@react object SyntaxHighlighter extends ExternalComponent {
  val component = SyntaxHighlighterComp
  case class Props(language: String, style: js.Dictionary[js.Object])
}
