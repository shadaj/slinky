package slinky.docs.homepage //nodisplay

import slinky.core.StatelessComponent
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement //nodisplay
//nodisplay
//nodisplay
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
