package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{ComponentInstance, React}
import me.shadaj.slinky.core.AttrMod

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.scalajs.js

class BuildingComponentWithTagMods[P, E](e: ExternalComponentWithTagMods, props: P, mods: Seq[AttrMod[E]]) {
  def apply(children: ComponentInstance*)(implicit writer: Writer[P]): ComponentInstance = {
    val written = writer.write(props)

    mods.foreach { m =>
      written.asInstanceOf[js.Dynamic].updateDynamic(m.attr.name)(m.attr.value)
    }

    React.createElement(e.component, written, children: _*)
  }
}

object BuildingComponentWithTagMods {
  implicit def make[P, E]: BuildingComponentWithTagMods[P, E] => ComponentInstance = macro BuildingComponentWithTagModsMacros.makeImpl[P, E]
}

final class MakerWithTagMods[P: Writer, E] extends (BuildingComponentWithTagMods[P, E] => ComponentInstance) {
  override def apply(v1: BuildingComponentWithTagMods[P, E]): ComponentInstance = {
    v1()
  }
}

object BuildingComponentWithTagModsMacros {
  // SUPER SKETCHY INTELLIJ HACK
  def makeImpl[P: c.WeakTypeTag, E](c: Context): c.Expr[BuildingComponentWithTagMods[P, E] => ComponentInstance] = {
    import c.universe._
    c.Expr[BuildingComponentWithTagMods[P, E] => ComponentInstance](c.typecheck(q"new _root_.me.shadaj.slinky.core.MakerWithTagMods[${implicitly[WeakTypeTag[P]]}, ${implicitly[WeakTypeTag[E]]}]"))
  }
}

abstract class ExternalComponentWithTagMods {
  type Props
  type Element

  val component: js.Object

  def apply(p: Props, tagMods: AttrMod[Element]*): BuildingComponentWithTagMods[Props, Element] = {
    new BuildingComponentWithTagMods(this, p, tagMods)
  }
}
