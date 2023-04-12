enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.9" % Test
libraryDependencies += ("org.scala-js" %%% "scalajs-fake-insecure-java-securerandom" % "1.0.0" % Test).cross(CrossVersion.for3Use2_13)

Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

Test / scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(
  Option(System.getenv("ES2015_ENABLED")).map(_ == "true").getOrElse(false)
)) }

Test / unmanagedResourceDirectories += baseDirectory.value / "node_modules"

jsDependencies ++= Seq(
  (ProvidedJS / "react/umd/react.development.js"
    minified "react/umd/react.production.min.js" commonJSName "React") % Test,
  (ProvidedJS / "react-dom/umd/react-dom.development.js"
    minified "react-dom/umd/react-dom.production.min.js" dependsOn "react/umd/react.development.js" commonJSName "ReactDOM") % Test,
  (ProvidedJS / "react-dom/umd/react-dom-test-utils.development.js"
    minified "react-dom/umd/react-dom-test-utils.production.min.js" dependsOn "react-dom/umd/react-dom.development.js" commonJSName "ReactTestUtils") % Test,
  (ProvidedJS / "react-dom/umd/react-dom-server.browser.development.js"
    minified "react-dom/umd/react-dom-server.browser.production.min.js" dependsOn "react-dom/umd/react-dom.development.js" commonJSName "ReactDOMServer") % Test
)
