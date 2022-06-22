enablePlugins(ScalaJSPlugin)

import org.scalajs.linker.interface.ModuleSplitStyle

name := "slinky-docs-next"

scalaJSLinkerConfig ~= {
  _.withModuleKind(ModuleKind.ESModule)
    .withModuleSplitStyle(ModuleSplitStyle.SmallestModules)
}

Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "pages"
Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "pages"
