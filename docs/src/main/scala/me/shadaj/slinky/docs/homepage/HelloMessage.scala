package me.shadaj.slinky.docs.homepage //nodisplay

import me.shadaj.slinky.core.Component //nodisplay
import me.shadaj.slinky.core.annotations.react //nodisplay
import me.shadaj.slinky.core.facade.ReactElement //nodisplay
import me.shadaj.slinky.web.html._ //nodisplay

@react class HelloMessage extends Component {
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
