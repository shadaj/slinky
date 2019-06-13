package slinky.docs

import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.docs.homepage.SlinkyHorizontalLogo
import slinky.reactrouter.Link
import slinky.web.html._

import scala.scalajs.js

@react object Navbar {
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

  val component = FunctionalComponent[Unit](_ => {
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
          justifyContent = "space-between",
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
            minWidth = "150px"
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
            marginRight = "auto"
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
            marginRight = "20px"
          ),
          className := "sidebar-right"
        )(
          a(
            href := "https://gitter.im/shadaj/slinky",
            style := smallLinkStyle
          )(
            "Community"
          ),
          a(
            href := "https://github.com/shadaj/slinky/blob/master/CHANGELOG.md",
            style := smallLinkStyle
          )(
            "v0.6.1"
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
  })
}
