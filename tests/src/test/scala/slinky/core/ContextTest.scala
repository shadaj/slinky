package slinky.core

import org.scalatest.FunSuite
import slinky.core.facade.React
import slinky.web.ReactDOM
import org.scalajs.dom.document
import slinky.web.html.div

class ContextTest extends FunSuite {
  test("Can provide and read a simple context value") {
    val context = React.createContext[Int]("myContext")
    var gotValue = 0

    ReactDOM.render(
      context.Provider(value = 2)(
        context.Consumer { value =>
          gotValue = value
          div()
        }
      ),
      document.createElement("div")
    )

    assert(gotValue == 2)
  }

  test("Can provide and read a case class context value") {
    case class Data(foo: Int)
    val context = React.createContext[Data]("myContext")
    var gotValue = 0

    ReactDOM.render(
      context.Provider(value = Data(3))(
        context.Consumer { value =>
          gotValue = value.foo
          div()
        }
      ),
      document.createElement("div")
    )

    assert(gotValue == 3)
  }
}
