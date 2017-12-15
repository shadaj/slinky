package me.shadaj.slinky.docs

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.docs.homepage.SlinkyHorizontalLogo
import me.shadaj.slinky.reactrouter.Link
import me.shadaj.slinky.web.html._

import scala.scalajs.js

@react class Navbar extends Component {
  type Props = Unit

  val linkStyle = js.Dynamic.literal(
    color = "white",
    fontSize = "21px",
    fontWeight = 300,
    marginLeft = "30px",
    letterSpacing = "1.3px",
    textDecoration = "none"
  )

  val smallLinkStyle = js.Dynamic.literal(
    color = "white",
    fontSize = "15px",
    fontWeight = 100,
    marginLeft = "30px",
    textDecoration = "none"
  )

  def render(): ReactElement = {
    header(style := js.Dynamic.literal(
      width = "100%",
      position = "fixed",
      top = 0,
      left = 0,
      backgroundColor = "#20232a"
    ))(
      div(
        style := js.Dynamic.literal(
          display = "flex",
          height = "60px",
          flexDirection = "row",
          alignItems = "center",
          maxWidth = "1400px",
          marginLeft = "auto",
          marginRight = "auto"
        )
      )(
        div(
          style := js.Dynamic.literal(
            display = "flex",
            height = "100%",
            alignItems = "center",
            width = "20%"
          )
        )(
          Link(to = "/")(
            style := js.Dynamic.literal(
              marginRight = "50px"
            )
          )(
            img(
              height := "50",
              src := SlinkyHorizontalLogo.asInstanceOf[String]
            )
          )
        ),
        div(
          style := js.Dynamic.literal(
            display = "flex",
            height = "100%",
            alignItems = "center",
            width = "60%"
          )
        )(
          Link(to = "/docs/installation/")(style := linkStyle)(
            "Docs"
          )
        ),
        div(
          style := js.Dynamic.literal(
            display = "flex",
            height = "100%",
            alignItems = "center",
            width = "20%"
          )
        )(
          a(
            href := "https://gitter.im/shadaj/slinky",
            style := smallLinkStyle
          )(
            "Community"
          ),
          a(
            href := "https://github.com/shadaj/slinky/releases",
            style := smallLinkStyle
          )(
            "v0.1.1"
          ),
          a(
            href := "https://github.com/shadaj/slinky",
            style := smallLinkStyle
          )(
            "GitHub"
          )
        )
      )
    )
  }
}
