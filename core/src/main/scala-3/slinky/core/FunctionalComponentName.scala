package slinky.core

//import scala.language.experimental.macros
//import scala.reflect.macros.whitebox

final class FunctionalComponentName(val name: String) extends AnyVal
//object FunctionalComponentName {
//  implicit def get: FunctionalComponentName = macro FunctionalComponentNameMacros.impl
//}
//
//object FunctionalComponentNameMacros {
//  def impl(c: whitebox.Context): c.Expr[FunctionalComponentName] = {
//    import c.universe._
//
//    // from lihaoyi/sourcecode
//    def isSyntheticName(name: String) =
//      name == "<init>" || (name.startsWith("<local ") && name.endsWith(">")) || name == "component"
//
//    @scala.annotation.tailrec
//    def findNonSyntheticOwner(current: Symbol): Symbol =
//      if (isSyntheticName(current.name.decodedName.toString.trim)) {
//        findNonSyntheticOwner(current.owner)
//      } else {
//        current
//      }
//
//    c.Expr(
//      q"new _root_.slinky.core.FunctionalComponentName(${findNonSyntheticOwner(c.internal.enclosingOwner).name.decodedName.toString})"
//    )
//  }
//}
