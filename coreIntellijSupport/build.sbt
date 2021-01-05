enablePlugins(SbtIdeaPlugin)

name := "slinky-core-ijext"

intellijPluginName := "Slinky IntelliJ Support"

intellijBuild := "203.6682.168" // 2020.3

intellijPlugins += "org.intellij.scala".toPlugin

packageMethod := PackagingMethod.Standalone()

val publishAutoChannel = taskKey[Unit]("publishAutoChannel")
publishAutoChannel := Def.taskDyn {
  val isDev = version.value.contains("+")
  if (isDev) {
    publishPlugin.toTask(" develop")
  } else {
    publishPlugin.toTask(" Stable")
  }
}.value
