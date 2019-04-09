# Installation
It's super easy to get started with using Slinky, whether you're working on a new project or adding Slinky to your existing work.

## Creating a New Project
The easiest way to create a new project is to use [Create React Scala App](https://github.com/shadaj/create-react-scala-app.g8). This template creates a starter project with a default Webpack-based build configuration that enables hot reloading and bundling into a production site.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-scala-app.g8
```

Follow the prompts, and you'll be all set up with a Scala-based React application with Slinky! Check out the [repo](https://github.com/shadaj/create-react-scala-app.g8) for more details on what's included with the starter project.

## Adding to an Existing Project
Since Slinky is distributed just like any other Scala.js library, it's very easy to integrate into an existing project.

Add the dependencies that match your application as well as required Scala.js compiler options:
```scala
libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.6.0" // core React functionality, no React DOM
libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.6.0" // React DOM, HTML and SVG tags
libraryDependencies += "me.shadaj" %%% "slinky-native" % "0.6.0" // React Native components
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.0" // Hot loading, requires react-proxy package
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.6.0" // Interop with japgolly/scalajs-react

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

// optional, but recommended; enables the @react macro annotation API
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
// if using Scala 2.13.0-RC1, instead use
scalacOptions += "-Ymacro-annotations"
```

Slinky supports loading React via either CommonJS or as a global object. If loading as a global object, make sure React is available
as `window.React` and React DOM as `window.ReactDOM`.

While Slinky can be in a simple Scala.js app with no bundler, we highly recommend that you use [webpack](https://webpack.js.org/) for bundling your application with its dependencies. The SBT plugin [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/) automates much of the process around configuring webpack, and is very useful for adding webpack to an SBT build setup.

## IntelliJ Support
Starting with Slinky 0.5.0, the `@react` macro annotation is implemented with Macro Paradise to ensure compatibility with future versions of Scala, so a small extra step is required to enable IDE support in IntelliJ (version 2018.3 or higher is required). After loading, your project, uncheck `Enable loading external extensions` in `Settings > Languages & Frameworks > Scala > Extensions`, hit apply, re-check it, and hit apply again. After applying these settings and refreshing your SBT project again, IntelliJ will pop up with a request to enable Slinky support.

## Credits
Much credit goes to existing Scala.js libraries for React, such as [scalajs-react](https://github.com/japgolly/scalajs-react), which provided a lot of inspiration for Slinky's design. Credit also goes to [scala-js-preact](https://github.com/LMnet/scala-js-preact), which provided the inspiration for the `@react` macro annotation. 

Slinky logo is based on [https://thenounproject.com/dianatomic/uploads/?i=40452](https://thenounproject.com/dianatomic/uploads/?i=40452)
