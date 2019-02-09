package slinky.core

import org.scalatest.AsyncFunSuite
import org.scalajs.dom.document

import slinky.core.facade.{React, SetStateHookCallback}
import slinky.core.facade.Hooks._
import slinky.web.ReactDOM

import org.scalatest.Assertion
import scala.concurrent.Promise
import scala.util.Try

class HooksComponentTest extends AsyncFunSuite {
  implicit override def executionContext = scala.concurrent.ExecutionContext.Implicits.global

  test("Can render a functional component with useState hook") {
    val container = document.createElement("div")
    val component = FunctionalComponent[Int] { props =>
      val (state, setState) = useState("hello")
      state + props
    }
    
    ReactDOM.render(component(1), container)

    assert(container.innerHTML == "hello1")
  }

  test("Can call setState with direct value on a functional component with useState hook") {
    val container = document.createElement("div")
    var stateSetter: Option[SetStateHookCallback[String]] = None

    val promise: Promise[Assertion] = Promise()
    val component = FunctionalComponent[Int] { props =>
      val (state, setState) = useState("hello")
      stateSetter = Some(setState)

      if (state == "bye") {
        promise.success(assert(true))
      }

      state + props
    }
    
    ReactDOM.render(component(1), container)
    stateSetter.get.apply("bye")

    promise.future
  }

  test("Can call setState with transformed value on a functional component with useState hook") {
    val container = document.createElement("div")
    var stateSetter: Option[SetStateHookCallback[String]] = None

    val promise: Promise[Assertion] = Promise()
    val component = FunctionalComponent[Int] { props =>
      val (state, setState) = useState("hello")
      stateSetter = Some(setState)

      if (state == "olleh") {
        promise.success(assert(true))
      }

      state + props
    }
    
    ReactDOM.render(component(1), container)
    stateSetter.get.apply(_.reverse)

    promise.future
  }

  test("useEffect hook fires after render") {
    val container = document.createElement("div")

    val promise: Promise[Assertion] = Promise()
    val component = FunctionalComponent[Int] { props =>
      useEffect(() => {
        promise.success(assert(true))
      })

      props
    }
    
    ReactDOM.render(component(1), container)

    promise.future
  }

  test("useEffect hook fires only when watched objects change") {
    val container = document.createElement("div")

    val promise: Promise[Assertion] = Promise()
    var firstEffectOccured = false
    val component = FunctionalComponent[Int] { props =>
      useEffect(() => {
        if (firstEffectOccured) {
          promise.complete(Try(assert(props == 2)))
        }

        firstEffectOccured = true
      }, Seq(props))

      props
    }
    
    ReactDOM.render(component(1), container)
    ReactDOM.render(component(1), container)
    ReactDOM.render(component(2), container)

    promise.future
  }

  test("useEffect hook unsubscribe function is called on unmount") {
    val container = document.createElement("div")

    val promise: Promise[Assertion] = Promise()
    val component = FunctionalComponent[Int] { props =>
      useEffect(() => {
        () => {
          promise.success(assert(true))
        }
      }, Seq(props))

      props
    }
    
    ReactDOM.render(component(1), container)
    ReactDOM.unmountComponentAtNode(container)

    promise.future
  }
}
