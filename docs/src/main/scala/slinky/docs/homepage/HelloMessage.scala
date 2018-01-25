package slinky.docs.homepage //nodisplay

import slinky.core.StatelessComponent //nodisplay
import slinky.core.annotations.react //nodisplay
import slinky.core.facade.ReactElement //nodisplay
import slinky.web.html._ //nodisplay

@react class HelloMessage extends StatelessComponent {
  case class Props(name: String)

  override def render(): ReactElement = {
    div("Hello ", props.name)
  }
}

//display:ReactDOM.render(
//display:  HelloMessage(name = "Taylor"),
//display:  mountNode
//display:)

//run:HelloMessage(name = "Taylor") //nodisplay
