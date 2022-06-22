package slinky.docs

import slinky.core.FunctionalComponent
import slinky.core.ReactComponentClass
import slinky.core.annotations.react

import scala.scalajs.js.annotation._

@react object TextComponent {
  type Props = Unit

  val component = FunctionalComponent[Props] { p =>
    "hello!"
  }

  object Next {
    @JSExportTopLevel(name = "default", moduleID = "TextComponent")
    val component: ReactComponentClass[_] = TextComponent.component
  }
}
