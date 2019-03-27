package slinky.core

import scala.scalajs.js
import org.scalajs.dom

import slinky.core.annotations.react
import slinky.core.facade.{React, ReactElement}
import slinky.web.ReactDOM
import slinky.web.html._

import org.scalatest.FunSuite

object ExternalSimple extends ExternalComponentNoProps {
  override val component = "div"
}

object ExternalSimpleWithAttributes extends ExternalComponentNoPropsWithAttributes[div.tag.type] {
  override val component = "div"
}

object ExternalSimpleWithWildcardAttributes extends ExternalComponentNoPropsWithAttributes[*.tag.type] {
  override val component = "div"
}

@react object ExternalSimpleWithProps extends ExternalComponent {
  case class Props(a: Int)
  override val component = "div"
}

@react object ExternalDivWithPropsAndAttributes extends ExternalComponentWithAttributes[div.tag.type] {
  case class Props(id: String)
  override val component = "div"
}

@react object ExternalDivWithProps extends ExternalComponent {
  case class Props(id: String)
  override val component = "div"
}

@react object ExternalDivWithAllDefaulted extends ExternalComponent {
  case class Props(id: String = "foo")
  override val component = "div"
}

class ExternalComponentTest extends FunSuite {
  test("Rendering an external component results in appropriate props") {
    val rendered = ReactDOM.render(
      ExternalDivWithProps(id = "test"),
      dom.document.createElement("div")
    )

    assert(rendered.asInstanceOf[js.Dynamic].id.asInstanceOf[String] == "test")
  }

  test("Can use a ref with an macro-based external component") {
    val ref = React.createRef[js.Object]
    ReactDOM.render(
      ExternalDivWithProps(id = "test").withRef(ref),
      dom.document.createElement("div")
    )

    assert(ref.current.asInstanceOf[js.Dynamic].id.asInstanceOf[String] == "test")
  }

  test("Cannot reuse half-built external component") {
    val halfBuilt = ExternalDivWithProps(id = "test")
    val fullyBuilt: ReactElement = halfBuilt.withKey("abc")

    assertThrows[IllegalStateException] {
      val fullyBuilt2: ReactElement = halfBuilt.withKey("abc2")
    }
  }

  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    div(ExternalSimple())
  }

  test("Implicit macro to shortcut ExternalComponentWithAttributes can be invoked") {
    div(ExternalSimpleWithAttributes())
  }

  test("Can construct an external component taking Unit props with no arguments") {
    ExternalSimple()
  }

  test("Can construct an external component taking Unit props and attributes with no arguments") {
    ExternalSimpleWithAttributes(className := "hi")
  }

  test("Can construct an external component taking onClick/ref attribute with no arguments") {
    ExternalSimpleWithAttributes(
      onClick := (e => { e.target: dom.html.Div }),
      ref := (e => { e: dom.html.Div})
    )
  }

  test("Can construct an external component taking Unit props and attributes with some children") {
    ExternalSimpleWithAttributes(className := "hi")(div())
  }

  test("Cannot construct an external component taking div attributes with attributes for another tag") {
    assertDoesNotCompile("""ExternalSimpleWithAttributes(className := "hi", href := "foo")""")
  }

  test("Can construct an external component taking * attributes") {
    ExternalSimpleWithWildcardAttributes(className := "hi", href := "foo")
  }

  test("Can construct an external component with generated apply") {
    div(ExternalSimpleWithProps(a = 1))
  }

  test("Can construct an external component with default parameters") {
    div(ExternalDivWithAllDefaulted())
  }
}
