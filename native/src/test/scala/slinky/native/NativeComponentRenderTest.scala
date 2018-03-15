package slinky.native

import org.scalatest.FunSuite
import slinky.testrenderer.TestRenderer

import scala.scalajs.js

class NativeComponentRenderTest extends FunSuite {
  test("Can render a button component") {
    assert(!js.isUndefined(TestRenderer.create(
      Button(
        title = "foo",
        onPress = () => {}
      )(
        "Hello!"
      )
    ).toJSON()))
  }

  test("Can render a text component") {
    assert(!js.isUndefined(TestRenderer.create(
      Text()(
        "Hello!"
      )
    ).toJSON()))
  }

  test("Can render a view component with children") {
    assert(!js.isUndefined(TestRenderer.create(
      View()(
        View()()
      )
    ).toJSON()))
  }

  test("Can render an image component") {
    assert(!js.isUndefined(TestRenderer.create(
      Image(
        source = ImageURISource(
          uri = ""
        )
      )
    )))
  }
}
