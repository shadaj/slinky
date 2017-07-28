package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.ComponentInstance

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

package object html extends internal.tags with internal.tagsApplied with internal.attrs {
  implicit def stringToInstance(s: String): ComponentInstance = {
    s.asInstanceOf[ComponentInstance]
  }

  implicit def seqInstanceToInstance[T](s: Iterable[T])(implicit cv: T => ComponentInstance): ComponentInstance = {
    s.map(cv).toJSArray.asInstanceOf[ComponentInstance]
  }

  implicit def component2Instance[A](component: HtmlComponent[A]): ComponentInstance = {
    HtmlComponent.create(
      component.name,
      component.attrs.map(m => (m.name, m.value)).toMap.toJSDictionary,
      component.children
    )
  }

  implicit def optionToMod[T, A](option: Option[T])
                                (implicit cvt: T => HtmlComponentMod[A]): HtmlComponentMod[A] = {
    new SeqMod[A](option.map(cvt))
  }

  implicit def instance2Mod[T, A](instance: T)
                                 (implicit cvt: T => ComponentInstance): HtmlComponentMod[A] = {
    new ChildMod(cvt(instance))
  }

  implicit def attr2Mod[T, A](instance: T)
                             (implicit cvt: T => AttrPair[A]): HtmlComponentMod[A] = {
    new AttrMod[A](cvt(instance))
  }
}
