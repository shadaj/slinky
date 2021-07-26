package slinky.core

import scala.scalajs.js
import scala.quoted._
import slinky.readwrite.Writer

// same as PropsWriterProvider except it always returns the typeclass instead of nulling it out in fullOpt mode
trait ExternalPropsWriterProvider extends js.Object
object ExternalPropsWriterProvider {
 def impl(using q: Quotes): Expr[ExternalPropsWriterProvider] = {
  import q.reflect._
  val module = Symbol.spliceOwner.owner.owner
  val stateType = TypeIdent(module.memberType("Props")).tpe
  val instance = Implicits.search(TypeRepr.of[Writer].appliedTo(List(stateType)))
  instance match {
    case fail: ImplicitSearchFailure => report.throwError(fail.explanation)
    case s: ImplicitSearchSuccess => 
      '{
        ${s.tree.asExpr}.asInstanceOf[ExternalPropsWriterProvider]
      }
  }
 }

 implicit inline def get: ExternalPropsWriterProvider = ${impl}
}
