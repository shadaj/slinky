package me.shadaj.slinky.docs.homepage

import me.shadaj.slinky.core.{Component, StatelessComponent}
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.reactrouter.Link
import me.shadaj.slinky.web.html._

import scala.scalajs.js.Dynamic.literal

@react class Jumbotron extends StatelessComponent {
  type Props = Unit

  override def render(): ReactElement = {
    div(style := literal(
      marginTop = "60px",
      width = "100%",
      backgroundColor = "#282c34",
      padding = "30px",
      boxSizing = "border-box",
      display = "flex",
      flexDirection = "column"
    ))(
      img(
        style := literal(
          maxWidth = "100%",
          maxHeight = "45vh",
          display = "block",
          marginLeft = "auto",
          marginRight = "auto"
        ),
        src := SlinkyLogo.asInstanceOf[String]
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
        Link(to = "/docs/installation/")(style := literal(
          padding = "15px",
          backgroundColor = "#DC322F",
          color = "white",
          fontSize = "30px"
        ))(
          "Get Started"
        )
      )
    )
  }
}
