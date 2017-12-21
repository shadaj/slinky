package me.shadaj.slinky.docs

import me.shadaj.slinky.core.StatelessComponent
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.reactrouter.NavLink
import me.shadaj.slinky.web.html._

import scala.scalajs.js.Dynamic.literal

@react class DocsGroup extends StatelessComponent {
  case class Props(name: String, isOpen: Boolean, onRequestOpen: Function0[Unit], children: List[(String, String)])

  override def render(): ReactElement = {
    div(style := literal(width = "100%"))(
      button(style := literal(
        cursor = "pointer",
        backgroundColor = "transparent",
        marginTop = "10px",
        border = "none",
        fontSize = "18px",
        textTransform = "uppercase",
        fontWeight = "700",
        padding = "0",
        width = "100%",
        textAlign = "left",
        outline = "none"
      ), onClick := props.onRequestOpen)(
        div(style := literal(
          color = if (props.isOpen) "rgb(26, 26, 26)" else "rgb(109, 109, 109)"
        ))(props.name + (if (props.isOpen) "" else "     ▼"))
      ),
      ul(style := literal(display = if (props.isOpen) "block" else "none", listStyle = "none", padding = "0"))(
        props.children.zipWithIndex.map { case ((name, link), index) =>
          li(key := index.toString, style := literal(marginTop = "5px", marginBottom = "10px"))(
            NavLink(link, Some(literal(fontWeight = 700)), None)(
              style := literal(
                color = "rgb(26, 26, 26)",
                backgroundColor = "transparent",
                borderBottom = "none"
              )
            )(name)
          )
        }
      )
    )
  }
}
