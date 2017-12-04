package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactElement}
import me.shadaj.slinky.readwrite.Writer

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.|

case class BuildingComponent[E](c: String | js.Object, props: js.Object, key: String = null, ref: js.Object => Unit = null, mods: Seq[AttrPair[E]] = Seq.empty) {
  def apply(tagMod: AttrPair[E], tagMods: AttrPair[E]*): BuildingComponent[E] = copy(mods = mods ++ (tagMod +: tagMods))

  def withKey(key: String): BuildingComponent[E] = copy(key = key)
  def withRef(ref: js.Object => Unit): BuildingComponent[E] = copy(ref = ref)

  def apply(children: ReactElement*): ReactElement = {
    val written = props.asInstanceOf[js.Dictionary[js.Any]]

    if (key != null) {
      written("key") = key
    }

    if (ref != null) {
      written("ref") = ref: js.Function1[js.Object, Unit]
    }

    mods.foreach { m =>
      written(m.name) = m.value
    }

    React.createElement(c, written, children: _*)
  }
}

object BuildingComponent {
  implicit def make[E]: BuildingComponent[E] => ReactElement = _.apply(Seq.empty: _*)
}

abstract class ExternalComponent extends ExternalComponentWithAttributes[Nothing]

abstract class ExternalComponentWithAttributes[E <: TagElement] {
  type Props
  type Element = E

  val component: String | js.Object

  def apply(p: Props)(implicit writer: Writer[Props]): BuildingComponent[E] = {
    // no need to take key or ref here because those can be passed in through attributes
    new BuildingComponent(component, writer.write(p), null, null, Seq.empty)
  }
}

abstract class ExternalComponentNoProps extends ExternalComponentNoPropsWithAttributes[Nothing]

abstract class ExternalComponentNoPropsWithAttributes[E <: TagElement] {
  val component: String | js.Object

  def apply(mod: AttrPair[E], tagMods: AttrPair[E]*): BuildingComponent[E] = BuildingComponent(component, js.Dynamic.literal(), mods = mod +: tagMods)

  def withKey(key: String): BuildingComponent[E] = BuildingComponent(component, js.Dynamic.literal(), key = key)
  def withRef(ref: js.Object => Unit): BuildingComponent[E] = BuildingComponent(component, js.Dynamic.literal(), ref = ref)

  def apply(children: ReactElement*): ReactElement = {
    React.createElement(component, js.Dynamic.literal().asInstanceOf[js.Dictionary[js.Any]], children: _*)
  }
}