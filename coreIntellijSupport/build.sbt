enablePlugins(SbtIdeaPlugin)

name := "slinky-core-ijext"

intellijPlugins += "org.intellij.scala".toPlugin

intellijPlugins += "com.intellij.java".toPlugin

packageMethod := PackagingMethod.Standalone()

patchPluginXml := pluginXmlOptions { xml =>
  xml.version = version.value
  xml.sinceBuild = (intellijBuild in ThisBuild).value
  xml.untilBuild = "999.9999.999"
}

val publishAutoChannel = taskKey[Unit]("publishAutoChannel")
publishAutoChannel := Def.taskDyn {
  val isDev = version.value.contains("+")
  if (isDev) {
    publishPlugin.toTask(" develop")
  } else {
    publishPlugin.toTask(" Stable")
  }
}.value
