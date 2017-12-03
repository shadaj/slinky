package me.shadaj.slinky.docs

import me.shadaj.slinky.core.TagMod
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.html.{className, div, style}

import scala.scalajs.js.Dynamic.literal

object MainPageContent {
  def apply(children: ReactElement*): ReactElement = {
    div(className := "article", style := literal(
      maxWidth = "1260px",
      marginLeft = "auto",
      marginRight = "auto",
      padding = "15px",
      marginTop = "40px"
    ))(
      children.map(v => v: TagMod[div.tag.type]): _*
    )
  }
}