package slinky.readwrite

import scala.collection.mutable
import scala.reflect.macros.whitebox

abstract class GenericDeriveImpl(val c: whitebox.Context) {
  import c.universe._

  case class Param(name: Name, tpe: Type)

  def constructTypeclassType(forType: Type): Tree
  def deferredInstance(forType: Type, constantType: Type): Tree
  def maybeExtractDeferred(tree: Tree): Option[Tree]
  def createModuleTypeclass(tpe: Type, moduleReference: Tree): Tree
  def createCaseClassTypeclass(clazz: Type, params: Seq[Seq[Param]]): Tree
  def createValueClassTypeclass(clazz: Type, param: Param): Tree
  def createSealedTraitTypeclass(traitType: Type, subclasses: Seq[Symbol]): Tree
  def createFallback(forType: Type): Tree

  def replaceDeferred: Transformer = {
    new Transformer {
      override def transform(tree: Tree): Tree = maybeExtractDeferred(tree) match {
        case Some(t) =>
          q"${TermName(t.tpe.asInstanceOf[ConstantType].value.value.asInstanceOf[String])}"
        case None =>
          super.transform(tree)
      }
    }
  }

  def getTypeclass(forType: Type): Tree = {
    c.untypecheck(replaceDeferred.transform(
      c.inferImplicitValue(c.typecheck(constructTypeclassType(forType), mode = c.TYPEmode).tpe)
    ))
  }

  private def currentMemo = {
    GenericDeriveImpl.derivationMemo.get().getOrElseUpdate(
      this.getClass.getSimpleName, mutable.Map.empty
    )
  }

  def withMemoValue[T](symbol: Symbol, value: Option[String])(thunk: => T): T = {
    val orig = currentMemo.get(symbol.fullName)
    currentMemo(symbol.fullName) = value
    try {
      thunk
    } finally {
      if (orig.isDefined) {
        currentMemo(symbol.fullName) = orig.get
      } else {
        currentMemo.remove(symbol.fullName)
      }
    }
  }

  def derive[T](implicit tTag: WeakTypeTag[T]): Tree = {
    val symbol = tTag.tpe.typeSymbol

    if (currentMemo.get(symbol.fullName).contains(None)) {
      c.abort(c.enclosingPosition, "Skipping derivation macro when getting regular implicit")
    } else if (currentMemo.get(symbol.fullName).flatten.isDefined) {
      deferredInstance(
        tTag.tpe,
        c.internal.constantType(Constant(currentMemo(symbol.fullName).get))
      )
    } else if (symbol.isParameter) {
      c.abort(c.enclosingPosition, "")
    } else {
      val regularImplicit = withMemoValue(symbol, None) {
        c.inferImplicitValue(
          c.typecheck(constructTypeclassType(tTag.tpe), mode = c.TYPEmode).tpe,
          silent = true
        )
      }

      if (regularImplicit.isEmpty) {
        if (symbol.isModuleClass) {
          createModuleTypeclass(tTag.tpe, c.parse(symbol.asClass.module.fullName))
        } else if (symbol.isClass && symbol.asClass.isCaseClass) {
          val constructor = symbol.asClass.primaryConstructor
          val paramsLists = constructor.asMethod.paramLists
          val retName = c.freshName()
          val substituteMap = symbol.asClass.typeParams.zip(tTag.tpe.typeArgs).toMap
          withMemoValue(symbol, Some(retName)) {
            val params: Seq[Seq[Param]] = paramsLists.map(_.map { p =>
              Param(p.name, substituteMap.getOrElse(p.typeSignature.typeSymbol, p.typeSignature))
            })

            q"""{
              var ${TermName(retName)}: ${constructTypeclassType(tTag.tpe)} = null
              ${TermName(retName)} = ${createCaseClassTypeclass(tTag.tpe, params)}
              ${TermName(retName)}
            }"""
          }
        } else if (symbol.isClass && tTag.tpe <:< typeOf[AnyVal]) {
          val actualValue = symbol.asClass.primaryConstructor.asMethod.paramLists.head.head
          val retName = c.freshName()
          val substituteMap = symbol.asClass.typeParams.zip(tTag.tpe.typeArgs).toMap

          val param = Param(actualValue.name, substituteMap.getOrElse(actualValue.typeSignature.typeSymbol, actualValue.typeSignature))

          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: ${constructTypeclassType(tTag.tpe)} = null
              ${TermName(retName)} = ${createValueClassTypeclass(tTag.tpe, param)}

              ${TermName(retName)}
            }"""
          }
        } else if (symbol.isClass && symbol.asClass.isSealed && symbol.asType.toType.typeArgs.isEmpty) {
          def getSubclasses(clazz: ClassSymbol): Set[Symbol] = {
            // from magnolia
            val children = clazz.knownDirectSubclasses
            val (abstractTypes, concreteTypes) = children.partition(_.isAbstract)

            abstractTypes.map(_.asClass).flatMap(getSubclasses(_)) ++ concreteTypes
          }

          val retName = c.freshName()

          withMemoValue(symbol, Some(retName)) {
            q"""{
              var ${TermName(retName)}: ${constructTypeclassType(tTag.tpe)} = null
              ${TermName(retName)} = ${createSealedTraitTypeclass(tTag.tpe, getSubclasses(symbol.asClass).toSeq)}

              ${TermName(retName)}
            }"""
          }
        } else {
          createFallback(tTag.tpe)
        }
      } else {
        regularImplicit
      }
    }
  }
}

object GenericDeriveImpl {
  private[GenericDeriveImpl] val derivationMemo = new ThreadLocal[mutable.Map[String, mutable.Map[String, Option[String]]]] {
    override def initialValue(): mutable.Map[String, mutable.Map[String, Option[String]]] = mutable.Map.empty
  }
}
