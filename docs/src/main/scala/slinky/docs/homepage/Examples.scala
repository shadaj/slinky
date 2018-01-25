package slinky.docs.homepage

import slinky.core.{Component, StatelessComponent}
import slinky.core.facade.ReactElement
import slinky.docs.CodeExample
import slinky.web.html._
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement

import scala.scalajs.js.Dynamic.literal

@react class Examples extends StatelessComponent {
  type Props = Unit

  override def render(): ReactElement = {
    div(
      div(style := literal(
        display = "flex",
        flexDirection = "row",
        justifyContent = "space-between",
        width = "100%",
        minHeight = "350px",
        marginTop = "35px"
      ))(
        div(style := literal(
          width = "30%"
        ))(
          h3("A Simple Component"),
          p(
            "Just like React, Slinky components implement a ",
            code("render()"),
            "method that returns what to display based on the input data, but also define a ", code("Props"), " type that defines the input data shape. ",
            "Slinky comes with a tags API for constructing HTML trees that gives a similar experience to other Scala libraries like ScalaTags but also includes additional type-safety requirements. ",
            "Input data that is passed into the component can be accessed by ", code("render()"), " via ", code("props")
          )
        ),
        div(style := literal(width = "65%", maxHeight = "450px"))(
          CodeExample("slinky.docs.homepage.HelloMessage")
        )
      ),
      div(style := literal(
        display = "flex",
        flexDirection = "row",
        justifyContent = "space-between",
        width = "100%",
        minHeight = "350px",
        marginTop = "35px"
      ))(
        div(style := literal(
          width = "30%"
        ))(
          h3("A Stateful Component"),
          p(
            "Slinky components, just like React components, can maintain internal state data (accessed with ", code("state"), ").",
            " When a component's state data changes after an invocation of ", code("setState"), ", the rendered markup will be update by re-invoking ", code("render()"), "."
          )
        ),
        div(style := literal(width = "65%", maxHeight = "450px"))(
          CodeExample("slinky.docs.homepage.Timer")
        )
      ),
      div(style := literal(
        display = "flex",
        flexDirection = "row",
        justifyContent = "space-between",
        width = "100%",
        minHeight = "350px",
        marginTop = "35px"
      ))(
        div(style := literal(
          width = "30%"
        ))(
          h3("An Application"),
          p(
            "Using ", code("props"), " and ", code("state"), ", we can put together a small Todo application. This example uses state to track the current list of items as well as the text that the user has entered."
          )
        ),
        div(style := literal(width = "65%", maxHeight = "450px"))(
          CodeExample("slinky.docs.homepage.TodoApp")
        )
      )
    )
  }
}
