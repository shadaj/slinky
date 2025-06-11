val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.19.0")

addCompilerPlugin(("org.scalameta" % "semanticdb-scalac" % "4.13.6").cross(CrossVersion.full))

addSbtPlugin("org.scala-js"   % "sbt-scalajs"        % scalaJSVersion)
addSbtPlugin("org.scala-js"   % "sbt-jsdependencies" % "1.0.2")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"       % "0.14.2")
addSbtPlugin("com.github.sbt" % "sbt-dynver"         % "5.1.0")
addSbtPlugin("com.github.sbt" % "sbt-pgp"            % "2.3.1")
addSbtPlugin("org.typelevel"  % "sbt-tpolecat"       % "0.5.2")
addSbtPlugin("org.jetbrains"  % "sbt-idea-plugin"    % "3.24.0")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.4.5")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype"       % "3.12.2")

libraryDependencies ++= Seq(
  "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0",
  "org.scala-js" %% "scalajs-linker"           % scalaJSVersion
)

dependsOn(ProjectRef(file("../sbtPlugin"), "sbtplugin"))
