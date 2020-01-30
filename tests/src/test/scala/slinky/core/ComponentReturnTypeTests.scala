package slinky.core

import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.ReactDOM
import slinky.web.html._
import org.scalajs.dom

import org.scalatest.funsuite.AnyFunSuite

class ComponentReturnTypeTests extends AnyFunSuite {
  def testElement(elem: ReactElement): Unit = {
    assert((div(elem): ReactElement) != null) // test use in another element
    ReactDOM.render(div(elem), dom.document.createElement("div")) // test rendering to DOM
    ()
  }

  test("Components can return - arrays") {
    testElement(Seq(h1("a"), h1("b")))
  }

  test("Components can return - strings") {
    testElement("hello")
  }

  test("Components can return - numbers") {
    testElement(1)
    testElement(1D)
    testElement(1F)
  }

  test("Components can return - portals") {
    testElement(ReactDOM.createPortal(null, dom.document.createElement("div")))
  }

  test("Components can return - null") {
    testElement(null)
  }

  test("Components can return - booleans") {
    testElement(true)
    testElement(false)
  }

  test("Components can return - options") {
    testElement(Some(h1("hi")))
    testElement(Some(NoPropsComponent()))
    testElement(None)
  }

  test("Components can return - fragments") {
    testElement(Fragment(h1("hi")))
  }
}
