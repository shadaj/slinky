package slinky.readwrite

import scala.reflect.macros.whitebox

trait MacroReaders {
  implicit def deriveReader[T]: Reader[T] = macro MacroReadersImpl.derive[T]
}

class MacroReadersImpl(_c: whitebox.Context) extends GenericDeriveImpl(_c) {
  import c.universe._

  val typeclassType: c.universe.Type = typeOf[Reader[_]]

  def deferredInstance(forType: c.universe.Type, constantType: c.universe.Type) =
    q"new _root_.slinky.readwrite.DeferredReader[$forType, $constantType]"

  def maybeExtractDeferred(tree: c.Tree): Option[c.Tree] =
    tree match {
      case q"new _root_.slinky.readwrite.DeferredReader[$_, $t]()" =>
        Some(t)
      case q"new slinky.readwrite.DeferredReader[$_, $t]()" =>
        Some(t)
      case _ => None
    }

  def createModuleTypeclass(tpe: c.universe.Type, moduleReference: c.Tree): c.Tree =
    q"""new _root_.slinky.readwrite.Reader[$tpe] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $tpe = {
            $moduleReference
          }
        }"""

  def createCaseClassTypeclass(clazz: c.Type, params: Seq[Seq[Param]]): c.Tree = {
    val paramsTrees = params.map(_.map { p =>
      p.transformIfVarArg {
        p.default.map { d =>
          q"if (_root_.scala.scalajs.js.isUndefined(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName})) $d else ${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
        }.getOrElse {
          q"${getTypeclass(p.tpe)}.read(o.asInstanceOf[_root_.scala.scalajs.js.Dynamic].${p.name.toTermName}.asInstanceOf[_root_.scala.scalajs.js.Object])"
        }
      }
    })

    q"""new _root_.slinky.readwrite.Reader[$clazz] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
            new $clazz(...$paramsTrees)
          }
        }"""
  }

  def createValueClassTypeclass(clazz: c.Type, param: Param): c.Tree =
    q"""new _root_.slinky.readwrite.Reader[$clazz] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $clazz = {
            new $clazz(${getTypeclass(param.tpe)}.read(o))
          }
        }"""

  def createSealedTraitTypeclass(traitType: c.Type, subclasses: Seq[c.Symbol]): c.Tree = {
    val cases = subclasses.map(sub => cq"""${sub.name.toString} => ${getTypeclass(sub.asType.toType)}.read(o)""")

    q"""new _root_.slinky.readwrite.Reader[$traitType] {
          def forceRead(o: _root_.scala.scalajs.js.Object): $traitType = {
            o.asInstanceOf[_root_.scala.scalajs.js.Dynamic]._type.asInstanceOf[_root_.java.lang.String] match {
              case ..$cases
              case _ => _root_.slinky.readwrite.Reader.fallback[$traitType].read(o)
            }
          }
        }"""
  }

  def createFallback(forType: c.Type) = q"_root_.slinky.readwrite.Reader.fallback[$forType]"
}
