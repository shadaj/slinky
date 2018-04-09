package slinky.core

import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement
import org.scalatest.{Assertion, AsyncFunSuite}
import slinky.core.facade.React
import slinky.web.ReactDOM
import slinky.web.html.{div, ref}

import scala.concurrent.Promise

class ReactRefTest extends AsyncFunSuite {
  test("Can pass in a ref object to an HTML tag and use it") {
    val elemRef = React.createRef[Element]
    ReactDOM.render(
      div(ref := elemRef)("hello!"),
      dom.document.createElement("div")
    )

    assert(elemRef.current.asInstanceOf[HTMLElement].innerHTML == "hello!")
  }

  test("Can pass in a ref object to a Slinky component and use it") {
    val promise: Promise[Assertion] = Promise()
    val ref = React.createRef[TestForceUpdateComponent.Def]

    ReactDOM.render(
      TestForceUpdateComponent(() => promise.success(assert(true))).withRef(ref),
      dom.document.createElement("div")
    )

    ref.current.forceUpdate()

    promise.future
  }

  test("Can use forwardRef to pass down a ref to a lower element") {
    val forwarded = React.forwardRef[String]((props, rf) => {
      div(ref := rf)(props)
    })

    val divRef = React.createRef[Any]
    ReactDOM.render(
      forwarded("hello").withRef(divRef),
      dom.document.createElement("div")
    )

    assert(divRef.current.asInstanceOf[HTMLElement].innerHTML == "hello")
  }
}
