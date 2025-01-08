enablePlugins(SbtPlugin)
enablePlugins(ScalaJSPlugin)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.17.0")

name := "sbt-slinky"

sbtPlugin := true

scalaVersion := "2.12.19"

libraryDependencies += "org.scalameta" %%% "scalameta" % "4.12.1"

version := "1.0.0"