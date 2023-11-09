val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.9.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

{
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq(addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2"))
}

libraryDependencies ++= {
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq("org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0")
}

libraryDependencies ++= {
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq("org.scala-js" %% "scalajs-linker" % "1.0.1")
}

addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix"    % "0.10.4")
addSbtPlugin("com.dwijnand"              % "sbt-dynver"      % "4.1.1")
addSbtPlugin("com.jsuereth"              % "sbt-pgp"         % "2.1.1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"    % "0.4.4")
addSbtPlugin("org.jetbrains"             % "sbt-idea-plugin" % "3.21.1")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"    % "2.4.5")
addSbtPlugin("org.xerial.sbt"            % "sbt-sonatype"    % "3.9.18")
