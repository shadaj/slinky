# Abstracting Over Tags
Slinky comes with a strongly-typed API for creating tag trees that not only checks attribute value types but also verifies that attributes are compatible with the tag they are assigned to. With Slinky 0.3.0, this API has been extended to make it possible to abstract over individual tag types while preserving the ability to assign attributes in a type-safe manner.

To start, let's define a method that creates an instance of a specified tag with a single child "Hello":
```scala
def createTag[T <: Tag](tag: T): ReactElement = {
  tag.apply("Hello")
}
```

Here we take a type parameter `T` to track the type of tag we are rendering, and then use the `tag` value's apply method to construct the tag. To use this method, we can call it and pass in the tag we want to render as a parameter (the type parameter will be inferred).

```scala
div(
  createTag(h1)
) // renders <div><h1>Hello</h1></div>
```

We can also assign attributes to the tag, but before we do that we first need to prove that the passed-in tag supports the attribute we want to assign. We can do this by adding `: insert_attribute_here.supports` at the end of the type parameters block to specify that we want the tag `T` to support the attribute we want to assign. If we wanted to assign `className`, for example, we can add `className.supports`:
```scala
def createTag[T <: Tag : className.supports](tag: T): ReactElement = {
  tag.apply(className := "my-css-class")("Hello")
}
```

Using this version of `createTag` is the same as above; just pass in the tag to render and it will be rendered with the appropriate attributes and children.
