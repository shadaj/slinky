package slinky.native

import org.scalatest.funsuite.AnyFunSuite
import slinky.testrenderer.TestRenderer

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

class NativeComponentRenderTest extends AnyFunSuite {
  test("Can render a button component") {
    assert(
      !js.isUndefined(
        TestRenderer
          .create(
            Button(
              title = "foo",
              onPress = () => {}
            )("Hello!")
          )
          .toJSON()
      )
    )
  }

  test("Can render a text component") {
    assert(
      !js.isUndefined(
        TestRenderer
          .create(
            Text(
              "Hello!"
            )
          )
          .toJSON()
      )
    )
  }

  test("Can render a view component with children") {
    assert(
      !js.isUndefined(
        TestRenderer
          .create(
            View(
              View()
            )
          )
          .toJSON()
      )
    )
  }

  val testStyle = literal(
    fontSize = 20,
    color = "black"
  )

  test("Can render a view component with style") {
    assert(
      !js.isUndefined(
        TestRenderer
          .create(
            View(style = testStyle)()
          )
          .toJSON()
      )
    )
  }

  test("Can render an image component") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          Image(
            source = ImageURISource(
              uri = ""
            )
          )
        )
      )
    )
  }

  test("Can request prefetch of an image") {
    // note: this doesn't actually test that the image is prefetched, since
    // the mocking library replaces the method with a no-op
    Image.prefetch("bar")
  }

  test("Can render a text input") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          TextInput(
            value = "hello"
          )
        )
      )
    )
  }

  test("Can call clear() on a text input instance") {
    var clearedValue = false
    assert(
      !js.isUndefined(
        TestRenderer.create(
          TextInput(
            value = "hello"
          ).withRef { i =>
            i.clear()
            clearedValue = true
          }
        )
      )
    )

    assert(clearedValue)
  }

  test("Can render a ScrollView with children") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          ScrollView(
            TextInput(value = "hello")
          )
        )
      )
    )
  }

  test("Can render a ScrollView with style") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          ScrollView(style = testStyle)()
        )
      )
    )
  }

  test("Can call scrollTo() on a scroll view instance") {
    var scrolled = false
    assert(
      !js.isUndefined(
        TestRenderer.create(
          ScrollView.withRef { i =>
            i.scrollTo(ScrollTarget(x = 0, y = 50, animated = true))
            scrolled = true
          }(
            TextInput(value = "hello")
          )
        )
      )
    )

    assert(scrolled)
  }

  test("Can render a picker with items") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          Picker(
            Picker.Item(label = "abc", value = "abc"),
            Picker.Item(label = "abc2", value = "abc2")
          )
        )
      )
    )
  }

  /* react-native-mock-render does not support Slider yet
  test("Can render a slider") {
    assert(!js.isUndefined(TestRenderer.create(
      Slider(
        value = 0
      )
    )))
  }
   */

  test("Can render a switch") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          Switch(
            value = true
          )
        )
      )
    )
  }

  test("Can render a flatlist") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          FlatList[Int](
            data = Seq(1, 2),
            renderItem = {
              case RenderItemInfo(d, index, _) =>
                Text(d.toString)
            }
          )
        )
      )
    )
  }

  /* react-native-mock-render does not support SectionList yet
  test("Can render a sectionlist") {
    assert(!js.isUndefined(TestRenderer.create(
      SectionList(
        sections = Seq(
          Section(
            data = Seq(1, 2, 3),
            renderItem = (d: Int, index: Int, _: Separators) => {
              Text(d.toString)
            }
          )
        )
      )
    )))
  }
   */

  test("Can render an activity indicator") {
    assert(
      !js.isUndefined(
        TestRenderer.create(
          ActivityIndicator()
        )
      )
    )
  }
}
