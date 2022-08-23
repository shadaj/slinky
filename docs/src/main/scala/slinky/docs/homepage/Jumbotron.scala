package slinky.docs.homepage

import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import slinky.next.Image

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import slinky.next.Link

@react object Jumbotron {
  val component = FunctionalComponent[Unit](_ => {
    div(style := literal(
      marginTop = "60px",
      width = "100%",
      backgroundColor = "#282c34",
      padding = "30px",
      boxSizing = "border-box",
      display = "flex",
      flexDirection = "column"
    ))(
      Image(src = SlinkyLogo, layout = "raw", priority = true, loader = (a: js.Dynamic) => a.src)(
        style := literal(
          maxWidth = "100%",
          maxHeight = "45vh",
          display = "block",
          marginLeft = "auto",
          marginRight = "auto"
        )
      ),
      h2(
        style := literal(
          color = "white",
          fontSize = "40px",
          display = "block",
          textAlign = "center",
          marginTop = "0px"
        )
      )("Write React apps in Scala just like you would in ES6"),
      div(style := literal(
        display = "flex",
        alignItems = "center",
        flexDirection = "row",
        alignSelf = "center"
      ))(
        Link(href = "/docs/installation/")(a(style := literal(
          padding = "15px",
          backgroundColor = "#DC322F",
          color = "white",
          fontSize = "30px"
        ))(
          "Get Started"
        )
      ))
    )
  })
}
