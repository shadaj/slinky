package me.shadaj.slinky.example

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

import me.shadaj.slinky.core._
import me.shadaj.slinky.core.facade.ReactElement
import me.shadaj.slinky.web.ReactDOM
import me.shadaj.slinky.web.html._
import me.shadaj.slinky.hot
import me.shadaj.slinky.scalajsreact.Converters._

import org.scalajs.dom.{Event, document, html}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}

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

      def render(): ReactElement = {
       val maybeChild = if (props.name == "parent foo") {
         Some(div(key := "I have a key!", data-"foo" := "bar")(
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
            onChange := ((e) => {
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
          (1 to state.size).map(n => div(key := n.toString)(s"$n - $state")),
          Hello("simple-react")
        )
      }
    }
  }

  def main(): Unit = {
    hot.initialize()
    if (js.isUndefined(js.Dynamic.global.reactContainer)) {
      js.Dynamic.global.reactContainer = document.createElement("div")
      document.body.appendChild(js.Dynamic.global.reactContainer.asInstanceOf[html.Element])
    }

    ReactDOM.render(
      Foo(Foo.Props("parent foo", Seq("lolz"))),
      js.Dynamic.global.reactContainer.asInstanceOf[html.Element]
    )
  }
}
