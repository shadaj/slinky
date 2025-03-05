package slinky.docs

import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.docs.homepage.SlinkyHorizontalLogo
import slinky.next.{Image, Link}
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
    fontWeight = 200,
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
          minHeight = "60px",
          flexDirection = "row",
          alignItems = "center",
          justifyContent = "space-between",
          maxWidth = "calc(min(1400px, 100vw - 80px))",
          marginLeft = "auto",
          marginRight = "auto",
          overflowX = "auto"
        )
      )(
        div(
          style := js.Dynamic.literal(
            display = "flex",
            height = "100%",
            alignItems = "center",
            minWidth = "150px",
          )
        )(
          Link(href = "/")(
            a(style := js.Dynamic.literal(
              marginRight = "50px"
            ))(
              Image(src = SlinkyHorizontalLogo, layout = "raw", priority = true, loader = (a: js.Dynamic) => a.src)(
                style := js.Dynamic.literal(
                  height = "50px",
                  width = "auto"
                )
              )
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
          Link(href = "/docs/installation/")(a(style := linkStyle)(
            "Docs"
          ))
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
          Link(
            href = "/docs/resources/"
          )(a(style := smallLinkStyle)(
            "Resources"
          )),
          a(
            href := "https://github.com/shadaj/slinky/blob/main/CHANGELOG.md",
            style := smallLinkStyle
          )(
            "v0.7.5"
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
