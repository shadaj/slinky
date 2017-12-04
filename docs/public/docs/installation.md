# Installation
It's super easy to get started with using Slinky, whether you're working on a new project or adding Slinky to your existing work.

## Creating a New Project
The easiest way to create a new project is to use [Create React Scala App](https://github.com/shadaj/create-react-slinky-app.g8). This template creates a starter project with a default Webpack-based build configuration that enables hot reloading and bundling into a production site.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-scala-app.g8
```

Follow the prompts, and you'll be all set up with a Scala-based React application with Slinky! Check out the [repo](https://github.com/shadaj/create-react-slinky-app.g8) for more details on what's included with the starter project.

## Adding to an Existing Project
Since Slinky is distributed just like any other Scala.js library, it's very easy to integrate into an existing project.

Add the dependencies that match your application:
```scala
libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.1.1" // core React functionality, no React DOM
libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.1.1" // React DOM, HTML and SVG tags
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.1.1" // Hot loading, requires react-proxy package
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.1.1" // Interop with japgolly/scalajs-react

// optional, but recommended; enables the @react macro annotation API
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
```

Slinky supports loading React via either CommonJS or as a global object. If loading as a global object, make sure React is available
as `window.React` and React DOM as `window.ReactDOM`.

While Slinky can be in a simple Scala.js app with no bundler, we highly recommend that you use [webpack](https://webpack.js.org/) for bundling your application with its dependencies. The SBT plugin [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/) automates much of the process around configuring webpack, and is very useful for adding webpack to an SBT build setup.