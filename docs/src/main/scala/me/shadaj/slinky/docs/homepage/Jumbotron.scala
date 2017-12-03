package me.shadaj.slinky.docs.homepage

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.html._

import scala.scalajs.js.Dynamic.literal

@react class Jumbotron extends Component {
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
      )("Write React apps in Scala just like you would with ES6"),
      div(style := literal(
        display = "flex",
        alignItems = "center",
        flexDirection = "row",
        alignSelf = "center"
      ))(
        a(style := literal(
          padding = "15px",
          backgroundColor = "#DC322F",
          color = "white",
          fontSize = "30px"
        ))(
          "Get Started"
        ),
        a(style := literal(
          marginLeft = "15px",
          color = "#61dafb",
          fontSize = "30px",
          fontWeight = 300
        ))(
          "Take the Tutorial"
        )
      )
    )
  }
}
