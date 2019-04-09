enablePlugins(ScalaJSPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8-RC2" % Test

jsDependencies += RuntimeDOM % Test

dependencyOverrides += "org.webjars.npm" % "js-tokens" % "3.0.2" % Test

jsDependencies ++= Seq(
  "org.webjars.npm" % "react" % "16.8.3" % Test / "umd/react.development.js"
    minified "umd/react.production.min.js" commonJSName "React",
  "org.webjars.npm" % "react-dom" % "16.8.3" % Test / "umd/react-dom.development.js"
    minified "umd/react-dom.production.min.js" dependsOn "umd/react.development.js" commonJSName "ReactDOM",
  "org.webjars.npm" % "react-dom" % "16.8.3" % Test / "umd/react-dom-test-utils.development.js"
    minified "umd/react-dom-test-utils.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactTestUtils",
  "org.webjars.npm" % "react-dom" % "16.8.3" % Test / "umd/react-dom-server.browser.development.js"
    minified  "umd/react-dom-server.browser.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactDOMServer"
)

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
