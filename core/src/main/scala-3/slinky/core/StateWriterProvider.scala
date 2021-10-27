package slinky.core

import scala.scalajs.js
import scala.quoted._
import slinky.readwrite.Writer
import scala.scalajs.LinkingInfo

trait StateWriterProvider extends js.Object
object StateWriterProvider {
 def impl(using q: Quotes): Expr[StateWriterProvider] = {
  import q.reflect._
  val module = Symbol.spliceOwner.owner.owner
  val stateType = TypeIdent(module.memberType("State")).tpe
  val instance = Implicits.search(TypeRepr.of[Writer].appliedTo(List(stateType)))
  instance match {
    case fail: ImplicitSearchFailure => report.throwError(fail.explanation)
    case s: ImplicitSearchSuccess => 
      '{
        if (LinkingInfo.productionMode) null
        else ${s.tree.asExpr}.asInstanceOf[StateWriterProvider]
      }
  }
 }

 implicit inline def get: StateWriterProvider = ${impl}
}
