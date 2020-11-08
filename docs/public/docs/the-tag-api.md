# The Tag API
When rendering HTML or SVG elements in Slinky, you're using the Slinky Tag API, which makes it possible to represent content trees in idiomatic Scala code.

If you've used libraries like ScalaTags before, you'll find the Slinky API very familiar, as it follows the same general tree-building style as other Scala tags libraries.

## Rendering Elements
Let's get started with rendering a simple HTML element!

First, we import all HTML tags from the `web` module:
```scala
import slinky.web.html._
```

Now, we can render the element:
```scala
div("hello")
```

Slinky tags always take `ReactElement`s as children, but these element instances can created in various ways with built in implicit conversions.
1) Other tags: `h1("I am a child element!")`
2) Rendering a React component: `MyComponent()`
3) A string: `"hello"`
4) Scala collection types containing other React Elements: `List("hello", "world")`

## Adding Attributes
In addition to containing other children, Slinky tags can be assigned attributes that will turn into HTML or SVG attributes at runtime

For example, we can set the HTML class of an element using the `className` attribute:
```scala
h1(className := "header") // turns into <h1 class="header"/>
```

When combined with children, attributes generally come first and children follow in a separate argument list:
```scala
h1(className := "header")(
  "Header child element 1",
  "Header child element 2"
)
```

When using the `data-` and `aria-` attributes, you can pass in the suffix as a string immediately following the `-`. For example, you could pass in a `data-columns` attribute as:
```scala
div(data-"columns" := "3")
```

### Event Listeners
To add event listeners to elements, you can pass in an attribute pair assigning an event to a handler function. In Slinky, the event value is based on a type from the [Scala.js DOM](https://github.com/scala-js/scala-js-dom) library (this may change in the near future, see [PR #53](https://github.com/shadaj/slinky/pull/53)).

```scala
input(onChange := (event => {
        println("the value of this input element was changed!")
      }))
```

Scala.js even handles the process of binding functions to the appropriate scope, so there's no need to worry about where the event handler is implemented!

### Optional Attributes

Slinky supports the use of the Option type to indicate where an attribute is optional. For example:

```scala
h1(className := Some("header"))
h2(className := None)
```
Would be rendered as:
```html
<h1 class="header"></h1>
<h2></h2>
```

### Styles
When attaching CSS styles to an element, Slinky follows the React API of having the `style` be a JavaScript object and provides an attribute that can be assigned to a `js.Dynamic` value. This different from other Scala tags libraries, which usually provide individual attributes for assigning style values.
```scala
h1(style := js.Dynamic.literal(
  fontSize = "30px"
))(
  "hello!"
)
```

### Special Attributes
Slinky supports the React special attributes `key` and `ref`.

`key` gives a hint of matching components in an array to React and is always a string.
```scala
div(key := "my-key")("hello")
```

`ref` allows you to gain access to an instance of the rendered DOM element. Slinky only supports the functional ref style, where the value of `ref` is a function that takes the DOM node instance.
```scala
div(ref := (elem => {  
  // ...something with the DOM element elem
}))("hello")
```
