package slinky.core

import scala.scalajs.js
import org.scalatest.funsuite.AsyncFunSuite
import org.scalajs.dom.document
import org.scalajs.dom.Element

import slinky.core.facade.{React, SetStateHookCallback, ReactRef, ReactElement}

import slinky.core.facade.Hooks._
import slinky.web.ReactDOM
import slinky.web.html._

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

  test("Can use callback returned by setState as a plain function") {
    val container = document.createElement("div")
    var stateSetter: Option[String => Unit] = None

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
    stateSetter.foreach(_("olleh"))

    promise.future
  }

  test("Can use callback returned by setState as a plain transform function") {
    val container = document.createElement("div")
    var stateSetter: Option[(String => String) => Unit] = None

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
    stateSetter.foreach(transform => transform(s => s.reverse))

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

  test("useEffect hook unsubscribe function is called on unmount when it is a js.Function") {
    val container = document.createElement("div")

    val promise: Promise[Assertion] = Promise()
    val cleanup: js.Function0[Unit] = () => {
      promise.success(assert(true))
    }

    val component = FunctionalComponent[Int] { props =>
      useEffect(() => {
        cleanup
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
    val component = FunctionalComponent[Unit] { _ =>
      val ctx = useContext(context)
      ctx
    }

    ReactDOM.render(
      context.Provider("hello from context!")(component(())),
      container
    )

    assert(container.innerHTML == "hello from context!")
  }

  test("useReducer gets reducer value and dispatch works") {
    val container = document.createElement("div")
    var doDispatch: Int => Unit = null
    val promise: Promise[Assertion] = Promise()

    val component = FunctionalComponent[Unit] { props =>
      val (state, dispatch) = useReducer((_: String, a: Int) => {
        a.toString
      }, "")

      doDispatch = dispatch

      if (state == "123") {
        promise.success(assert(true))
      }

      state
    }

    ReactDOM.render(
      component(()),
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
      component(()),
      container
    )

    assert(container.innerHTML == "123")
  }

  test("useCallback maintains reference equality unless dependency changes") {
    val container = document.createElement("div")

    var callbackRef: () => String = null

    val component = FunctionalComponent[(Int, String)] { props =>
      val callback = useCallback(() => {
        props._2 // purposefully wrong (so that we can assert that untracked dependency retains old value)
      }, Seq(props._1))

      callbackRef = callback
      callback()
    }

    ReactDOM.render(component((1, "one")), container)
    assert(container.innerHTML == "one")
    assert(callbackRef != null)

    var prevCallbackRef = callbackRef
    ReactDOM.render(component((1, "one_")), container)
    assert(container.innerHTML == "one")
    assert(callbackRef eq prevCallbackRef) // note that we use eq to check for reference equality

    prevCallbackRef = callbackRef
    ReactDOM.render(component((2, "two")), container)
    assert(callbackRef ne prevCallbackRef) // note that we use ne to check for reference inequality
    assert(container.innerHTML == "two")
  }

  test("useCallback with arguments maintains reference equality unless dependency changes") {
    val container = document.createElement("div")

    var callbackRef: Boolean => String = null
    
    val component = FunctionalComponent[(Int, String)] { props =>
      val callback = useCallback((value: Boolean) => {
        props._2 // purposefully wrong (so that we can assert that untracked dependency retains old value)
      }, Seq(props._1))

      callbackRef = callback
      callback(true)
    }

    ReactDOM.render(component((1, "one")), container)
    assert(container.innerHTML == "one")
    assert(callbackRef != null)

    var prevCallbackRef = callbackRef
    ReactDOM.render(component((1, "one_")), container)
    assert(container.innerHTML == "one")
    assert(callbackRef eq prevCallbackRef) // note that we use eq to check for reference equality

    prevCallbackRef = callbackRef
    ReactDOM.render(component((2, "two")), container)
    assert(callbackRef ne prevCallbackRef) // note that we use ne to check for reference inequality
    assert(container.innerHTML == "two")
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

  test("useImperativeHandle allows for customizing ref") {
    val container = document.createElement("div")

    trait RefHandle {
      def foo: Int
    }

    val component = React.forwardRef(FunctionalComponent { (props: String, ref: ReactRef[RefHandle]) =>
      useImperativeHandle(ref, () => {
        new RefHandle {
          def foo = 123
        }
      })
      "": ReactElement // FIXME - implicit conversion from string seems to not trigger in Scala 3
    })

    val refReceiver = React.createRef[RefHandle]
    ReactDOM.render(component("first").withRef(refReceiver), container)
    assert(refReceiver.current.foo == 123)
  }

  test("useImperativeHandle does not run if dependency does not change") {
    val container = document.createElement("div")

    trait RefHandle {
      def foo: Int
    }

    var memoedValue = 123
    val component = React.forwardRef(FunctionalComponent { (props: String, ref: ReactRef[RefHandle]) =>
      useImperativeHandle(ref, () => {
        val curMemoedValue = memoedValue
        new RefHandle {
          def foo = curMemoedValue
        }
      }, Seq(props))
      "": ReactElement // FIXME - implicit conversion from string seems to not trigger in Scala 3
    })

    val refReceiver = React.createRef[RefHandle]
    ReactDOM.render(component("first").withRef(refReceiver), container)
    assert(refReceiver.current.foo == 123)

    memoedValue = 456
    ReactDOM.render(component("first").withRef(refReceiver), container)
    assert(refReceiver.current.foo == 123)

    ReactDOM.render(component("second").withRef(refReceiver), container)
    assert(refReceiver.current.foo == 456)
  }

  test("useLayoutEffect hook fires after mount") {
    val container = document.createElement("div")

    val promise: Promise[Assertion] = Promise()
    val component = FunctionalComponent[Int] { props =>
      val divRef = useRef[Element](null)
      useLayoutEffect(() => {
        promise.success(assert(divRef.current.innerHTML == "hello"))
      })

      div(ref := divRef)("hello")
    }
    
    ReactDOM.render(component(1), container)

    promise.future
  }
}
