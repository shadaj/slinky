enablePlugins(SbtPlugin)
enablePlugins(ScalaJSPlugin)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.19.0")

ThisBuild / organization := "me.shadaj"

name := "sbt-slinky"

sbtPlugin := true

libraryDependencies += "org.scalameta" %% "scalameta" % "4.13.6" cross CrossVersion.for2_13Use3

version := "1.0.0"
