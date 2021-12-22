package slinky.core

import scala.quoted._

final class FunctionalComponentName(val name: String) extends AnyVal
object FunctionalComponentName {
  inline implicit def get: FunctionalComponentName = ${FunctionalComponentNameMacros.impl}
}

object FunctionalComponentNameMacros {
 def impl(using q: Quotes): Expr[FunctionalComponentName] = {
  import q.reflect._

  // from lihaoyi/sourcecode
  def isSyntheticName(name: String) =
    name == "<init>" || (name.startsWith("<local ") && name.endsWith(">")) || name == "component" || name == "macro" || name == "$anonfun"

  @scala.annotation.tailrec
  def findNonSyntheticOwnerName(current: Symbol): String =
    if (isSyntheticName(current.name.trim)) {
      findNonSyntheticOwnerName(current.owner)
    } else {
      current.name.trim.stripSuffix("$")
    }

  val name = Expr[String](findNonSyntheticOwnerName(Symbol.spliceOwner))
  '{
    new FunctionalComponentName(${name})
  }
 }
}
