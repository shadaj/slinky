import _root_.io.github.davidgregory084._

enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.17" % Test
libraryDependencies += ("org.scala-js" %%% "scalajs-fake-insecure-java-securerandom" % "1.0.0")
  .cross(CrossVersion.for3Use2_13)

Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

Test / scalaJSLinkerConfig ~= {
  _.withESFeatures(
    _.withUseECMAScript2015(
      Option(System.getenv("ES2015_ENABLED")).map(_ == "true").getOrElse(false)
    )
  )
}

Test / unmanagedResourceDirectories += baseDirectory.value / "node_modules"

jsDependencies ++= Seq(
  ((ProvidedJS / "text-enc/lib/encoding.js")
    .minified("text-enc/lib/encoding.js")
    .commonJSName("TextEnc")) % Test,
  ((ProvidedJS / "react/umd/react.development.js")
    .dependsOn("text-enc/lib/encoding.js")
    .minified("react/umd/react.production.min.js")
    .commonJSName("React")) % Test,
  ((ProvidedJS / "react-dom/umd/react-dom.development.js")
    .minified("react-dom/umd/react-dom.production.min.js")
    .dependsOn("react/umd/react.development.js")
    .commonJSName("ReactDOM")) % Test,
  ((ProvidedJS / "react-dom/umd/react-dom-test-utils.development.js")
    .minified("react-dom/umd/react-dom-test-utils.production.min.js")
    .dependsOn("react-dom/umd/react-dom.development.js")
    .commonJSName("ReactTestUtils")) % Test,
  ((ProvidedJS / "react-dom/umd/react-dom-server.browser.development.js")
    .minified("react-dom/umd/react-dom-server.browser.production.min.js")
    .dependsOn("react-dom/umd/react-dom.development.js")
    .commonJSName("ReactDOMServer")) % Test
)

// The Scala 3 tests still have a bunch of warnings that need fixing such as https://github.com/shadaj/slinky/issues/643
// before CiMode can be used.
tpolecatOptionsMode := (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((3, _)) => DevMode
  case _            => CiMode
})

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, _)) => Seq("-P:scalajs:nowarnGlobalExecutionContext")
  case _            => Seq.empty
})

// Unit statements are prevalent in the tests. There is no way to suppress them:
// See https://github.com/typelevel/sbt-tpolecat/issues/134.
Test / tpolecatExcludeOptions += ScalacOptions.warnNonUnitStatement
