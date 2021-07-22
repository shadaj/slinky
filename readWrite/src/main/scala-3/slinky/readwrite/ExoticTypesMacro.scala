package slinky.readwrite

import scala.quoted._

object ExoticTypesMacro {
  def makeNominalUnion[T: Type](using q: Quotes): Expr[NominalUnion[T]] = {
    import q.reflect._

    def flattenUnions(A: TypeRepr): List[TypeRepr] = A match {
      case OrType(lhs, rhs) => flattenUnions(lhs) ::: flattenUnions(rhs)
      case _ => List(A)
    }

    flattenUnions(TypeRepr.of[T].dealias) match {
      case Nil | _ :: Nil => report.throwError(s"Got non-union type ${TypeRepr.of[T]}")
      case uns => 
        val tupleType = uns.foldRight(TypeRepr.of[EmptyTuple])((tpe, tuple) => TypeRepr.of[*:].appliedTo(List(tpe, tuple)))
        val fullType = TypeRepr.of[NominalUnion.Aux].appliedTo(List(TypeRepr.of[T], tupleType))
        val term = '{ NominalUnion.Instance }.asTerm
        TypeApply(Select.unique(term, "asInstanceOf"), List(TypeTree.of(using fullType.asType))).asExprOf[NominalUnion[T]]
    }
  }
}