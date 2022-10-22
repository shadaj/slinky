enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % "1.7.7"
      )
    case _ =>
      Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % "2.0.0"
      )
  }
}

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.14" % Test

Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

Test / unmanagedResourceDirectories += baseDirectory.value / "node_modules"

jsDependencies ++= Seq(
  ((ProvidedJS / "react/umd/react.development.js")
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
