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

  test("useContext hook gets context value") {
    val container = document.createElement("div")
    val context = React.createContext("")
    val component = FunctionalComponent[Unit] { props =>
      val ctx = useContext(context)
      ctx
    }

    ReactDOM.render(
      context.Provider("hello from context!")(component()),
      container
    )

    assert(container.innerHTML == "hello from context!")
  }

  test("useReducer gets reducer value and dispatch works") {
    val container = document.createElement("div")
    var doDispatch: Int => Unit = null
    val promise: Promise[Assertion] = Promise()

    val component = FunctionalComponent[Unit] { props =>
      val (state, dispatch) = useReducer((s: String, a: Int) => {
        a.toString
      }, "")

      doDispatch = dispatch

      if (state == "123") {
        promise.success(assert(true))
      }

      state
    }

    ReactDOM.render(
      component(),
      container
    )
    
    doDispatch(123)

    promise.future
  }

  test("useReducer can have lazy init") {
    val container = document.createElement("div")

    val component = FunctionalComponent[Unit] { props =>
      val (state, dispatch) = useReducer((s: String, a: Int) => {
        a.toString
      }, 123, (init: Int) => init.toString)

      state
    }

    ReactDOM.render(
      component(),
      container
    )

    assert(container.innerHTML == "123")
  }

  test("useCallback produces callable function") {
    val container = document.createElement("div")

    var called = false
    
    val component = FunctionalComponent[Unit] { props =>
      val callback = useCallback(() => {
        called = true
      }, Seq.empty)

      callback()

      ""
    }

    ReactDOM.render(
      component(),
      container
    )

    assert(called)
  }

  test("useMemo only recalculates when watched objects change") {
    val container = document.createElement("div")

    var memoedValue = "first"
    val component = FunctionalComponent[Int] { props =>
      useMemo(() => {
        memoedValue
      }, Seq(props))
    }
    
    ReactDOM.render(component(1), container)
    assert(container.innerHTML == "first")

    memoedValue = "second"
    ReactDOM.render(component(1), container)
    assert(container.innerHTML == "first")

    memoedValue = "third"
    ReactDOM.render(component(2), container)
    assert(container.innerHTML == "third")
  }

  test("useRef allows a ref to be tracked across renders") {
    val container = document.createElement("div")

    val component = FunctionalComponent[String] { props =>
      val ref = useRef[String]("")

      if (ref.current == "") ref.current = props

      ref.current
    }
    
    ReactDOM.render(component("first"), container)
    assert(container.innerHTML == "first")

    ReactDOM.render(component("second"), container)
    assert(container.innerHTML == "first")
  }
}
