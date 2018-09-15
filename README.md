<p align="center"><img width="400" src="https://github.com/shadaj/slinky/raw/master/logo.png"/></p>
<p align="center"><i>Write Scala.js React apps just like you would in ES6</i></p>
<p align="center">
  <a href="https://travis-ci.org/shadaj/slinky">
    <img src="https://travis-ci.org/shadaj/slinky.svg?branch=master"/>
  </a>
  <a href="https://www.scala-js.org">
    <img src="https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg"/>
  </a>
  <img src="https://img.shields.io/maven-central/v/me.shadaj/slinky-core_sjs0.6_2.12.svg"/>
  <a href="https://gitter.im/shadaj/slinky?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge">
    <img src="https://badges.gitter.im/shadaj/slinky.svg"/>
  </a>
</p>

# Get started at [slinky.shadaj.me](https://slinky.shadaj.me)

## Contributing
Slinky is split up into several submodules:
+ `core` contains the React.js facades and APIs for creating components and interfaces to external components
+ `web` contains bindings to React DOM and definitions for the HTML/SVG tag API
+ `native` contains bindings to React Native and external component definitions for native UI elements
+ `vr` contains bindings to React 360 and external component definitions for VR UI elements
+ `readWrite` contains the `Reader` and `Writer` typeclasses used to persist state for hot reloading
+ `hot` contains the entrypoint for enabling hot-reloading
+ `scalajsReactInterop` implements automatic conversions between Slinky and Scala.js React types
+ `testRenderer` contains bindings to `react-test-renderer` for unit testing components
+ `coreIntellijSupport` contains IntelliJ-specific support for the `@react` macro annotation
+ `tests` contains the unit tests for the above modules (except native and vr which have local tests)
+ `docs` and `docsMacros` contains the documentation site, which is a Slinky app itself

To run the main unit tests, run `sbt tests/test`.
