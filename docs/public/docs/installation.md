# Installation
It's super easy to get started with using Slinky, whether you're working on a new project or adding Slinky to your existing work.

## Creating a New Project
The easiest way to create a new project is to use [Create React Scala App](https://github.com/shadaj/slinky).

## Adding to an Existing Project
Add the dependencies that match your application:
```scala
libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.1.1" // core React functionality, no React DOM
libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.1.1" // React DOM, HTML and SVG tags
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.1.1" // Hot loading, requires react-proxy package
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.1.1" // Interop with japgolly/scalajs-react

// optional, enables the @react macro annotation API
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
```

Slinky supports loading React via either CommonJS or as a global object. If loading as a global object, make sure React is available
as `window.React` and React DOM as `window.ReactDOM`.