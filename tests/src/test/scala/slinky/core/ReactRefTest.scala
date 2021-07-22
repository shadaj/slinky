// package slinky.core

// import org.scalajs.dom
// import org.scalajs.dom.html

// import slinky.core.facade.React
// import slinky.web.ReactDOM
// import slinky.web.html.{div, ref}

// import scala.concurrent.Promise

// import org.scalatest.Assertion
// import org.scalatest.funsuite.AsyncFunSuite

// class ReactRefTest extends AsyncFunSuite {
//   test("Can pass in a ref object to an HTML tag and use it") {
//     val elemRef = React.createRef[html.Div]
//     ReactDOM.render(
//       div(ref := elemRef)("hello!"),
//       dom.document.createElement("div")
//     )

//     assert(elemRef.current.innerHTML == "hello!")
//   }

//   test("Can pass in a ref object to a Slinky component and use it") {
//     val promise: Promise[Assertion] = Promise()
//     val ref = React.createRef[TestForceUpdateComponent.Def]

//     ReactDOM.render(
//       TestForceUpdateComponent(() => promise.success(assert(true))).withRef(ref),
//       dom.document.createElement("div")
//     )

//     ref.current.forceUpdate()

//     promise.future
//   }

//   test("Can use forwardRef to pass down a ref to a lower element") {
//     val forwarded = React.forwardRef[String, html.Div](FunctionalComponent((props, rf) => {
//       div(ref := rf)(props)
//     }))

//     val divRef = React.createRef[html.Div]
//     ReactDOM.render(
//       forwarded("hello").withRef(divRef),
//       dom.document.createElement("div")
//     )

//     assert(divRef.current.innerHTML == "hello")
//   }
// }
