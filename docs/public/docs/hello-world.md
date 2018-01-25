# Hello World!
Let's get started by writing a super simple Slinky app!

If you want to test this locally, take a look at the [Installation](/docs/installation/) documentation on how to set up your own Slinky project.

The simplest Slinky app, which renders "Hello, world!" to the screen, looks like this:
```scala
import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._

ReactDOM.render(
  h1("Hello, world!"),
  document.getElementById("root")
)
``` 

## Slinky Modules
Slinky is split up into many modules, which make it flexible to support a large variety of projects and environments. Here, we are using two modules: `core`, which contains wrappers around React and provides the base classes for creating components, and `web`, which has wrappers around ReactDOM and provides the tags API (covered in detailed [here](/docs/tag-api/)) for constructing HTML trees.
