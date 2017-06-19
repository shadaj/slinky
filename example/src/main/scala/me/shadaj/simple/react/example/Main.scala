package me.shadaj.simple.react.example

import me.shadaj.simple.react.core.{Component, WithRaw}
import me.shadaj.simple.react.core.fascade.{ComponentInstance, ReactDOM}
import me.shadaj.simple.react.core.html._
import org.scalajs.dom.{Event, document, html}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.ScalaJSDefined

object Main extends JSApp {
  object Foo extends Component {
    case class Props(name: String) extends WithRaw
    type State = String

    @ScalaJSDefined
    class Def(jsProps: js.Object) extends Definition(jsProps) {
      def initialState = props.name

      override def componentDidMount(): Unit = {
        println("mounted!")
      }

      override def componentDidUpdate(prevProps: Props, prevState: String): Unit = {
        println("raw: " + raw(prevProps).value)
        println("updated!")
      }

      def render(): ComponentInstance = {
       val maybeChild = if (props.name == "parent foo") {
         Some(div(key := "I have a key!")(
           Foo(Foo.Props("child foo")),
           "here's a string rendered by the parent!"
         ))
       } else None

        div(
          style := js.Dynamic.literal(
            "marginLeft" -> 20
          )
        )(
          state,
          input(
            `type` := "foo",
            onChange := ((e: Event) => {
              println(e.target.asInstanceOf[html.Input].value)
              setState(e.target.asInstanceOf[html.Input].value)
            }),
            className := "foo",
            style := js.Dynamic.literal(
              "color" -> (if (state.contains(" ")) "red" else "green")
            ),
            value := state
          ),
          div(className := "foo")(props.name),
          maybeChild,
          (1 to state.size).map(n => div(s"$n - $state"))
        )
      }
    }
  }

  def main(): Unit = {
    val container = document.createElement("div")
    document.body.appendChild(container)

    ReactDOM.render(Foo(Foo.Props("parent foo")), container)
  }
}
