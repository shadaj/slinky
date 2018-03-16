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
      )("Hello!")
    ).toJSON()))
  }

  test("Can render a text component") {
    assert(!js.isUndefined(TestRenderer.create(
      Text(
        "Hello!"
      )
    ).toJSON()))
  }

  test("Can render a view component with children") {
    assert(!js.isUndefined(TestRenderer.create(
      View(
        View()
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

  test("Can request prefetch of an image") {
    // note: this doesn't actually test that the image is prefetched, since
    // the mocking library replaces the method with a no-op
    Image.prefetch("bar")
  }

  test("Can render a text input") {
    assert(!js.isUndefined(TestRenderer.create(
      TextInput(
        value = "hello"
      )
    )))
  }

  test("Can call clear() on a text input instance") {
    var clearedValue = false
    assert(!js.isUndefined(TestRenderer.create(
      TextInput(
        value = "hello"
      ).withRef { i =>
        i.clear()
        clearedValue = true
      }
    )))

    assert(clearedValue)
  }
}
