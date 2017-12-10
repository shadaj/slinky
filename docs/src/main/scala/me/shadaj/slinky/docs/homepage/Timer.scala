package me.shadaj.slinky.docs.homepage //nodisplay

import me.shadaj.slinky.core.annotations.react //nodisplay
import me.shadaj.slinky.core.Component //nodisplay
import me.shadaj.slinky.core.facade.ReactElement //nodisplay
import me.shadaj.slinky.web.html._ //nodisplay
import org.scalajs.dom.window._ //nodisplay

@react class Timer extends Component {
  type Props = Unit
  case class State(seconds: Int)

  override def initialState = State(seconds = 0)

  def tick(): Unit = {
    setState(prevState =>
      State(prevState.seconds + 1))
  }

  private var interval = -1

  override def componentDidMount(): Unit = {
    interval = setInterval(tick: () => Unit, 1000)
  }

  override def componentWillUnmount(): Unit = {
    clearInterval(interval)
  }

  override def render(): ReactElement = {
    div(
      "Seconds: ", state.seconds.toString
    )
  }
}

//display:ReactDOM.render(Timer(), mountNode)
//run:Timer() //nodisplay