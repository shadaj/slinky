enablePlugins(ScalaJSPlugin)

import org.scalajs.linker.interface.ModuleSplitStyle

name := "slinky-docs-next"

scalaJSLinkerConfig ~= {
  _.withModuleKind(ModuleKind.ESModule)
    .withModuleSplitStyle(ModuleSplitStyle.SmallestModules)
}

Compile / fastLinkJS / scalaJSLinkerOutputDirectory := target.value / "next-modules"
Compile / fullLinkJS / scalaJSLinkerOutputDirectory := target.value / "next-modules"
