package slinky.core

import scala.scalajs.js
import scala.quoted._
import slinky.readwrite.Reader
import scala.scalajs.LinkingInfo

trait StateReaderProvider extends js.Object
object StateReaderProvider {
 def impl(using q: Quotes): Expr[StateReaderProvider] = {
  import q.reflect._
  val module = Symbol.spliceOwner.owner.owner
  val stateType = TypeIdent(module.memberType("State")).tpe
  val instance = Implicits.search(TypeRepr.of[Reader].appliedTo(List(stateType)))
  instance match {
    case fail: ImplicitSearchFailure => report.throwError(fail.explanation)
    case s: ImplicitSearchSuccess => 
      '{
        if (!LinkingInfo.productionMode) null
        else ${s.tree.asExpr}.asInstanceOf[StateReaderProvider]
      }
  }
 }

 implicit inline def get: StateReaderProvider = ${impl}
}
