package slinky.core.annotations

import slinky.core.facade.ReactElement
import slinky.web.ReactDOM
import org.scalajs.dom

import scala.concurrent.Promise
import scala.scalajs.js

import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite

class ReactAnnotatedComponentTest extends AsyncFunSuite {
  test("setState given function is applied") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponent(i => promise.success(assert(i == 1))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("setState callback function is run") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponent(i => promise.success(assert(i == 1))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Can construct a component and provide key") {
    val element: ReactElement = TestComponent(_ => ()).withKey("test")
    assert(element.asInstanceOf[js.Dynamic].key.toString == "test")
  }

  test("Can construct a component with macro apply and provide key") {
    val element: ReactElement = TestComponentCaseClass(a = 1).withKey("test")
    assert(element.asInstanceOf[js.Dynamic].key.toString == "test")
  }

  test("Can construct a component taking Unit props with no arguments") {
    val element: ReactElement = NoPropsComponent()
    assert(!js.isUndefined(element.asInstanceOf[js.Dynamic]))
  }

  test("Can construct a component taking Unit props with refs and key") {
    val element: ReactElement = NoPropsComponent.withKey("hi").withRef((_: js.Object) => {})
    assert(element.asInstanceOf[js.Dynamic].key.toString == "hi")
    assert(!js.isUndefined(element.asInstanceOf[js.Dynamic].ref))
  }

  test("Force updating a component by its ref works") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestForceUpdateComponent(() => promise.success(assert(true))).withRef(ref => {
        ref.forceUpdate()
      }),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Error boundary component catches an exception in its children") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      ErrorBoundaryComponent(true, (_, _) => {
        promise.success(assert(true))
      }),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Error boundary component works fine with no errors") {
    var sawError = false

    ReactDOM.render(
      ErrorBoundaryComponent(false, (_, _) => {
        sawError = true
      }),
      dom.document.createElement("div")
    )

    assert(!sawError)
  }

  test("getSnapshotBeforeUpdate is run and returned value is passed to componentDidUpdate") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      TestComponentForSnapshot(i => promise.success(assert(i == 123))),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("getDerivedStateFromProps results in state being calculated based on props") {
    val promise: Promise[Assertion] = Promise()

    ReactDOM.render(
      DerivedStateComponent(
        123, i => promise.success(assert(i == 123))
      ),
      dom.document.createElement("div")
    )

    promise.future
  }

  test("Can use curried apply for components with children") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithChildren(0)("hello", "bye"),
      targetNode
    )

    assert(targetNode.innerHTML == "hellobye")
  }

  test("Can use direct apply for components with only a children prop") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithOnlyChildren(0, 1, 2),
      targetNode
    )

    assert(targetNode.innerHTML == "012")
  }

  test("Can use curried apply for components with non-vararg children") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithNonVarargChildren(0)(List("a", "b", "c")),
      targetNode
    )

    assert(targetNode.innerHTML == "abc")
  }

  test("Can use variable ReactElementContainer types within components") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithVariableReactElementContainers(),
      targetNode
    )
    assert(targetNode.innerHTML == "<div>abcdef<h1>i</h1><i>j</i>l</div>")
  }

  test("Can use inline ReactElementContainer types within components") {
    val targetNode = dom.document.createElement("div")
    ReactDOM.render(
      ComponentWithInlineReactElementContainers(),
      targetNode
    )
    assert(targetNode.innerHTML == "<div>abcdef<h1>i</h1><i>j</i>l</div>")
  }
}