val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.6.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

{
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq(addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2"))
}

libraryDependencies ++= {
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq("org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.0.0")
}

{
  if (scalaJSVersion.startsWith("0.6.")) addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler-sjs06" % "0.19.0")
  else Seq(addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0"))
}

libraryDependencies ++= {
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq("org.scala-js" %% "scalajs-linker" % "1.0.1")
}

addSbtPlugin("com.jsuereth"   % "sbt-pgp"         % "2.1.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype"    % "3.9.12")
addSbtPlugin("com.dwijnand"   % "sbt-dynver"      % "4.1.1")
addSbtPlugin("org.jetbrains"  % "sbt-idea-plugin" % "3.13.9")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"    % "2.4.6")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"    % "0.9.34")
