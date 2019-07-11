package slinky.core

import slinky.core.facade.{React, ReactElement, ReactRaw}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.{Dictionary, JSON}
import scala.language.higherKinds
import scala.language.experimental.macros

class Tag(@inline private final val name: String) {
  type tagType <: TagElement
  def apply(mods: TagMod[tagType]*): WithAttrs = {
    val inst = new WithAttrs(js.Array(name, js.Dynamic.literal()))
    
    mods.foreach {
      case a: AttrPair[_] =>
        if (a.value.asInstanceOf[js.UndefOr[js.Any]] != js.undefined) {
          inst.args(1).asInstanceOf[js.Dictionary[js.Any]](a.name) = a.value
        }
      case r =>
        inst.args.push(r.asInstanceOf[ReactElement])
    }
    
    inst
  }
}

final class CustomTag(name: String) extends Tag(name) {
  override type tagType = Nothing
}

trait Attr {
  type attrType
  type supports[T <: Tag] = AttrPair[attrType] => AttrPair[T#tagType]
}

abstract class TagElement {
  type RefType
}

final class CustomAttribute[T](@inline private val name: String) {
  @inline def :=(v: T) = new AttrPair[Any](name, v.asInstanceOf[js.Any])
  @inline def :=(v: Option[T]) = new AttrPair[Any](name, v.getOrElse(js.undefined).asInstanceOf[js.Any])
}

trait TagMod[-A] extends js.Object

import slinky.core.facade.ReactElementConversions
object TagMod extends ReactElementConversions

@js.native trait RefAttr[-T] extends js.Object
object RefAttr {
  @inline implicit def fromReact[T](in: slinky.core.facade.ReactRef[T]): RefAttr[T] = in.asInstanceOf[RefAttr[T]]
}

final class AttrPair[-A](@inline final val name: String,
                         @inline final val value: js.Any) extends TagMod[A]

trait LowPrioWithAttrs {
  implicit def runtimeBuild(withAttrs: WithAttrs): ReactElement = {
    if (withAttrs.args(0) == null) {
      throw new IllegalStateException("This tag has already been built into a ReactElement, and cannot be reused")
    }

    val ret = ReactRaw.createElement
      .applyDynamic("apply")(ReactRaw, withAttrs.args).asInstanceOf[ReactElement]

    withAttrs.args(0) = null

    ret
  }
}

final class WithAttrs(@inline private[core] val args: js.Array[js.Any]) extends AnyVal {
  def apply(children: ReactElement*): ReactElement = macro TagMacros.applyChildren
}

object WithAttrs extends LowPrioWithAttrs {
  def runtimeApplyChildren(withAttrs: WithAttrs, children: ReactElement*): ReactElement = {
    if (withAttrs.args(0) == null) {
      throw new IllegalStateException("This tag has already been built into a ReactElement, and cannot be reused")
    }

    children.foreach(c => withAttrs.args.push(c))
    WithAttrs.runtimeBuild(withAttrs)
  }

  implicit def build(withAttrs: WithAttrs): ReactElement = macro TagMacros.build
}

import scala.annotation.StaticAnnotation
class tagObject(name: String) extends StaticAnnotation
class attrAppliedConversion extends StaticAnnotation
class createAttrMethod(name: String, optional: Boolean) extends StaticAnnotation

object TagMacros {
  import scala.reflect.macros.blackbox

  // From https://github.com/wix/accord/blob/master/core/src/main/scala/com/wix/accord/transform/MacroHelper.scala
  // Workaround for https://issues.scala-lang.org/browse/SI-8500.
  def resetWithExistentialFix(c: blackbox.Context)(tree: c.Tree): c.Tree = {
    import c.universe._
    val transformed = new Transformer {
      override def transform(subtree: Tree): Tree = {
        subtree match {
          case typeTree: TypeTree
            if typeTree.tpe.dealias.typeArgs.nonEmpty && typeTree.tpe.dealias.typeArgs.forall {
              case arg if internal.isSkolem(arg.typeSymbol) && arg.typeSymbol.asInstanceOf[TypeSymbolApi].isExistential => true
              case _ => false
            } =>
    
            val tpe = typeTree.tpe.asInstanceOf[TypeRefApi]
            def existentialTypePara =
              internal.boundedWildcardType(internal.typeBounds(typeOf[Nothing], typeOf[Any]))
            TypeTree(internal.typeRef(tpe.pre, tpe.sym, List.fill(tpe.args.length)(existentialTypePara)))
          case o => super.transform(o)
        }
      }
    }.transform(tree.duplicate)
    c.untypecheck(transformed)
  }

  def tagApply(c: blackbox.Context)(mods: c.Tree*): c.Tree = {
    import c.universe._

    val isUnderscoreStar = if (mods.size == 1) {
      mods.head match {
        case Typed(_, Ident(typeNames.WILDCARD_STAR)) =>
          true
        case _ => false
      }
    } else false

    val argsName = TermName(c.freshName())
    val propsName = TermName(c.freshName())

    val tagName = c.prefix.tree.symbol.annotations.find(_.tree.tpe =:= typeOf[tagObject]).get.tree.children.tail.head
    
    if (isUnderscoreStar) {
      val Typed(starred, Ident(typeNames.WILDCARD_STAR)) = mods.head
      q"""(() => {
        val $propsName = _root_.scala.scalajs.js.Dictionary.empty[_root_.scala.scalajs.js.Any]
        val $argsName = _root_.scala.scalajs.js.Array[_root_.scala.scalajs.js.Any]($tagName, $propsName)

        ${resetWithExistentialFix(c)(starred)}.foreach { e =>
          (e: Any) match {
            case a: _root_.slinky.core.AttrPair[_] =>
              $propsName(a.name) = a.value
            case r =>
              $argsName.push(r.asInstanceOf[_root_.slinky.core.facade.ReactElement])
          }
        }

        new _root_.slinky.core.WithAttrs($argsName)
      })()"""
    } else {
      val modsApplied = mods.map { m =>
        def getActualType(tpe: Type, isRetyped: Boolean = false): Type = {
          if (tpe =:= typeOf[ReactElement]) {
          typeOf[ReactElement]
          } else if (tpe.typeSymbol == typeOf[AttrPair[_]].typeSymbol) {
            typeOf[AttrPair[_]]
          } else {
            if (isRetyped) {
              tpe
            } else {
              getActualType(c.typecheck(c.untypecheck(m)).tpe, true)
            }
          }
        }

        val actualType = getActualType(m.tpe)

        if (actualType =:= typeOf[ReactElement]) {
          q"$argsName.push(${resetWithExistentialFix(c)(m)})"
        } else if (actualType =:= typeOf[AttrPair[_]]) {
          def extractNameValue(tree: Tree): Option[(Tree, Tree, Boolean)] = {
            tree match {
              case q"${met}($in)" if met.symbol.annotations.exists(_.tree.tpe =:= typeOf[attrAppliedConversion]) =>
                val annot =  met.symbol.annotations.find(_.tree.tpe =:= typeOf[attrAppliedConversion]).get
                extractNameValue(in)
              case q"${met}($value)" if met.symbol.annotations.exists(_.tree.tpe =:= typeOf[createAttrMethod]) =>
                val annot =  met.symbol.annotations.find(_.tree.tpe =:= typeOf[createAttrMethod]).get
                val Seq(attrName, optional) = annot.tree.children.tail
                val isOptional = optional match {
                  case q"true" => true
                  case q"false" => false
                }
                Some((attrName, value, isOptional))
              case q"${met}($value)(..$_)" if met.symbol.annotations.exists(_.tree.tpe =:= typeOf[createAttrMethod]) =>
                val annot =  met.symbol.annotations.find(_.tree.tpe =:= typeOf[createAttrMethod]).get
                val Seq(attrName, optional) = annot.tree.children.tail
                val isOptional = optional match {
                  case q"true" => true
                  case q"false" => false
                }
                Some((attrName, value, isOptional))
              case o =>
                None
            }
          }

          extractNameValue(m).map { case (name, value, optional) =>
            if (optional) {
              q"${resetWithExistentialFix(c)(value)}.foreach(v => $propsName($name) = v)"
            } else {
              q"$propsName($name) = ${resetWithExistentialFix(c)(value)}"
            }
          }.getOrElse {
            val mName = TermName(c.freshName())
            q"""
            val $mName = ${resetWithExistentialFix(c)(m)}
            if (${mName}.value.asInstanceOf[_root_.scala.scalajs.js.UndefOr[_root_.scala.scalajs.js.Any]] != _root_.scala.scalajs.js.undefined) {
              $propsName(${mName}.name) = ${mName}.value
            }
            """
          }
        } else {
          q"""
          ($m) match {
            case a: _root_.slinky.core.AttrPair[_] =>
              if (a.value.asInstanceOf[_root_.scala.scalajs.js.UndefOr[_root_.scala.scalajs.js.Any]] != _root_.scala.scalajs.js.undefined) {
                $propsName(a.name) = a.value
              }
            case r =>
              $argsName.push(r.asInstanceOf[_root_.slinky.core.facade.ReactElement])
          }
          """
        }
      }

      q"""(() => {
        val $propsName = _root_.scala.scalajs.js.Dictionary.empty[_root_.scala.scalajs.js.Any];
        val $argsName = _root_.scala.scalajs.js.Array[_root_.scala.scalajs.js.Any]($tagName, $propsName);
        ..$modsApplied
        new _root_.slinky.core.WithAttrs($argsName)
      })()"""
    }
  }

  def applyChildren(c: blackbox.Context)(children: c.Tree*): c.Tree = {
    import c.universe._

    val isUnderscoreStar = if (children.size == 1) {
      children.head match {
        case Typed(_, Ident(typeNames.WILDCARD_STAR)) =>
          true
        case _ => false
      }
    } else false

    resetWithExistentialFix(c)(c.prefix.tree) match {
      case q"((() => { ..$pre; new _root_.slinky.core.WithAttrs($i) }).apply(): $_)" if !isUnderscoreStar =>
        val retName = TermName(c.freshName())

        q"""(() => {
          ..$pre
          ..${children.map(child => q"$i.push(${resetWithExistentialFix(c)(child)})")}
          _root_.slinky.core.facade.ReactRaw.createElement.applyDynamic("apply")(
            _root_.slinky.core.facade.ReactRaw, $i
          ).asInstanceOf[_root_.slinky.core.facade.ReactElement]
        })()"""
      case o =>
        q"_root_.slinky.core.WithAttrs.runtimeApplyChildren($o, ..$children)"
    }
  }

  def build(c: blackbox.Context)(withAttrs: c.Tree): c.Tree = {
    import c.universe._
    resetWithExistentialFix(c)(withAttrs) match {
      case q"((() => { ..$pre; new _root_.slinky.core.WithAttrs($i) }).apply(): $_)" =>
        val retName = TermName(c.freshName())

        q"""(() => {
          ..$pre
          _root_.slinky.core.facade.ReactRaw.createElement.applyDynamic("apply")(
            _root_.slinky.core.facade.ReactRaw, $i
          ).asInstanceOf[_root_.slinky.core.facade.ReactElement]
        })()"""
      case o =>
        q"_root_.slinky.core.WithAttrs.runtimeBuild($o)"
    }
  }
}
