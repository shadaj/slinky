package me.shadaj.simple.react.example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.shadaj.simple.react.core.{Component, Reader, WithRaw, Writer}
import me.shadaj.simple.react.core.fascade.{ComponentInstance, ReactDOM}
import me.shadaj.simple.react.core.html._
import me.shadaj.simple.react.example.Main.Foo.Props
import me.shadaj.simple.react.scalajsreact.Converters._
import org.scalajs.dom.{Event, document, html}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.ScalaJSDefined

object Main extends JSApp {
  val Hello =
    ScalaComponent.builder[String]("Hello")
      .render_P(name => <.div(
        "This is a component from scalajs-react being used in ", name,
        div(
          "and this is from simple-react inside the scalajs-react component!"
        )
      ))
      .build

  object Foo extends Component {
    case class Props(name: String, bar: Seq[String])
    type State = String

    @ScalaJSDefined
    class Def(jsProps: js.Object) extends Definition(jsProps) {
      def initialState: String = props.name

      override def componentDidMount(): Unit = {
        println("mounted!")
      }

      override def shouldComponentUpdate(nextProps: Props, nextState: String): Boolean = {
        props != nextProps || state != nextState
      }

      override def componentDidUpdate(prevProps: Props, prevState: String): Unit = {
        println("componentDidUpdate: " + prevProps)
      }

      def render(): ComponentInstance = {
       val maybeChild = if (props.name == "parent foo") {
         Some(div(key := "I have a key!")(
           Foo(Foo.Props("child foo", Seq.empty)),
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
          <.h2(s"this was rendered by scalajs-react!"),
          maybeChild,
          (1 to state.size).map(n => div(s"$n - $state")),
          Hello("simple-react")
        )
      }
    }
  }

  def main(): Unit = {
    val container = document.createElement("div")
    document.body.appendChild(container)

    ReactDOM.render(Foo(Foo.Props("parent foo", Seq("lolz"))), container)
  }
}
