package slinky.docs.homepage //nodisplay
//nodisplay
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.window._
import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement //nodisplay

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