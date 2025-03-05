package slinky.docs

import slinky.web.html.{className, div, style}
import slinky.core.facade.ReactElement
import slinky.core.annotations.react
import slinky.core.FunctionalComponent

import scala.scalajs.js.Dynamic.literal

@react object MainPageContent {
  val component = FunctionalComponent[Seq[ReactElement]] { children =>
    div(
      className := "article",
      style := literal(
        maxWidth = "calc(min(1400px, 100vw - 80px))",
        marginLeft = "auto",
        marginRight = "auto",
        marginBottom = "15px",
        padding = "5px"
      )
    ).apply(
      children: _*
    )
  }
}
