<p align="center"><img width="400" src="https://github.com/shadaj/slinky/raw/main/logo.png"/></p>
<p align="center"><i>Write Scala.js React apps just like you would in ES6</i></p>
<p align="center">
  <a href="https://github.com/shadaj/slinky/actions?query=branch%3Amain">
    <img src="https://github.com/shadaj/slinky/actions/workflows/sbt.yml/badge.svg?branch=main"/>
  </a>
  <a href="https://www.scala-js.org">
    <img src="https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg"/>
  </a>
  <a href="https://mvnrepository.com/artifact/me.shadaj/slinky-core">
    <img src="https://img.shields.io/maven-central/v/me.shadaj/slinky-core_sjs1_2.13"/>
  </a>
</p>

# Get started at [slinky.dev](https://slinky.dev)

## What is Slinky?
Slinky is a framework for writing React apps in Scala with an experience just like using ES6.

Slinky lets you:
+ Write React components in Scala with an API that mirrors vanilla React
+ Implement interfaces to other React libraries with automatic conversions between Scala and JS types
+ Write apps for React Native, React 360, and Electron, including the ability to share code with web apps
+ Develop apps iteratively with included hot-reloading support

## Contributing
Slinky is split up into several submodules:
+ `core` contains the React.js facades and APIs for creating components and interfaces to external components
+ `web` contains bindings to React DOM and definitions for the HTML/SVG tag API
+ `reactrouter` contains bindings to React Router
+ `history` contains a facade for the HTML5 history API
+ `native` contains bindings to React Native and external component definitions for native UI elements
+ `vr` contains bindings to React 360 and external component definitions for VR UI elements
+ `readWrite` contains the `Reader` and `Writer` typeclasses used to persist state for hot reloading
+ `hot` contains the entrypoint for enabling hot-reloading
+ `scalajsReactInterop` implements automatic conversions between Slinky and Scala.js React types
+ `testRenderer` contains bindings to `react-test-renderer` for unit testing components
+ `coreIntellijSupport` contains IntelliJ-specific support for the `@react` macro annotation
+ `tests` contains the unit tests for the above modules (except native and vr which have local tests)
+ `docs` and `docsMacros` contains the documentation site, which is a Slinky app itself

To run the main unit tests, first install the dependencies by running `npm install` inside the `tests` folder, then from the base folder run `sbt tests/test`. Similarly for React Native tests, run `npm install` inside the `native` folder, then from the base folder run `sbt native/test`.

Note to IntelliJ IDEA users. When you try to import Slinky SBT definition in IDEA and encounter an exception like
 `java.nio.file.NoSuchFileException: /Users/someuser/.slinkyPluginIC/sdk/192.6817.14/plugins`, you should
try to download required IntelliJ files for plugin subproject manually before importing:

```shell
sbt coreIntellijSupport/updateIntellij
```

And then import the project again.
