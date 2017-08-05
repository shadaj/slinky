enablePlugins(ScalaJSPlugin)

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.1" % Test

jsDependencies += "org.webjars.npm" % "react" % "15.6.1" % Test / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React"
jsDependencies += "org.webjars.npm" % "react-dom" % "15.6.1" % Test / "react-dom.js" minified "react-dom.min.js" commonJSName "ReactDOM" dependsOn "react-with-addons.js"
jsDependencies += RuntimeDOM % Test

jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
