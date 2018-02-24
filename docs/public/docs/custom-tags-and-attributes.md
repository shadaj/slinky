# Custom Tags and Attributes
While Slinky's web module comes with a standard set of HTML and SVG tags and attributes, you may need to create custom tags and attributes for non-standard elements or web components.

## Custom Tags
To create a custom tag, you can use the `CustomTag` class. Simply construct this class, and use the variable it's stored in as a regular Slinky tag. Custom tags are untyped in relation to attribute support, so you can use existing Slinky attributes with them.

```scala
val myCustomTag = new CustomTag("my-custom-element")

div(
  myCustomTag(href := "foo")("hello!")
)
```

results in

```html
<div>
  <my-custom-element href="foo">hello!</my-custom-element>
</div>
```

## Custom Attributes
To create a custom attribute, you can use the `CustomAttribute` class. Just like `CustomTag`, you can use the construct the class and use the variable it's stored in as a regular attribute. `CustomAttribute` takes a type parameter for statically typing the value of the attribute, but is untyped in relation to tag support so can be used with existing Slinky tags and custom tags.

```scala
val myCustomAttr = new CustomAttribute[String]("custom-href")
div(myCustomAttr := "foo")
```

results in

```html
<div custom-href="foo"></div>
```
