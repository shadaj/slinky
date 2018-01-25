package slinky.core

import slinky.core.annotations.react
import slinky.web.ReactDOM
import org.scalatest.FunSuite
import slinky.web.html._
import org.scalajs.dom

import scala.scalajs.js

object ExternalSimple extends ExternalComponentNoProps {
  override val component = "div"
}

@react object ExternalSimpleWithProps extends ExternalComponent {
  case class Props(a: Int)
  override val component = "div"
}

object ExternalSimpleWithAttributes extends ExternalComponentNoPropsWithAttributes[div.tag.type] {
  override val component = "div"
}

object ExternalSimpleWithWildcardAttributes extends ExternalComponentNoPropsWithAttributes[*.tag.type] {
  override val component = "div"
}

@react object ExternalDivWithProps extends ExternalComponent {
  case class Props(id: String)
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

  test("Can construct an external component with generated apply") {
    assertCompiles("""div(ExternalSimpleWithProps(a = 1))""")
  }

  test("Implicit macro to shortcut ExternalComponent can be invoked") {
    assertCompiles("""div(ExternalSimple())""")
  }

  test("Implicit macro to shortcut ExternalComponentWithAttributes can be invoked") {
    assertCompiles("""div(ExternalSimpleWithAttributes())""")
  }

  test("Can construct an external component taking Unit props with no arguments") {
    assertCompiles("""ExternalSimple()""")
  }

  test("Can construct an external component taking Unit props and attributes with no arguments") {
    assertCompiles("""ExternalSimpleWithAttributes(className := "hi")""")
  }

  test("Can construct an external component taking Unit props and attributes with some children") {
    assertCompiles("""ExternalSimpleWithAttributes(className := "hi")(div())""")
  }

  test("Cannot construct an external component taking div attributes with attributes for another tag") {
    assertDoesNotCompile("""ExternalSimpleWithAttributes(className := "hi", href := "foo")""")
  }

  test("Can construct an external component taking * attributes") {
    assertCompiles("""ExternalSimpleWithWildcardAttributes(className := "hi", href := "foo")""")
  }
}
