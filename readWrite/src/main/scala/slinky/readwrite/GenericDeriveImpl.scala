package slinky.readwrite

import scala.collection.mutable
import scala.reflect.macros.whitebox

abstract class GenericDeriveImpl(val c: whitebox.Context) { self =>
  import c.universe._

  case class Param(name: Name, origTpe: Type, default: Option[Tree]) {
    val tpe: Type = origTpe match {
      case TypeRef(_, sym, args) if sym == definitions.RepeatedParamClass =>
        appliedType(symbolOf[Seq[_]], args)
      case _ => origTpe
    }

    def transformIfVarArg(tree: Tree): Tree = {
      origTpe match {
        case TypeRef(_, sym, _) if sym == definitions.RepeatedParamClass =>
          q"$tree: _*"
        case _ => tree
      }
    }
  }

  val typeclassType: Type
  def deferredInstance(forType: Type, constantType: Type): Tree
  def maybeExtractDeferred(tree: Tree): Option[Tree]
  def createModuleTypeclass(tpe: Type, moduleReference: Tree): Tree
  def createCaseClassTypeclass(clazz: Type, params: Seq[Seq[Param]]): Tree
  def createValueClassTypeclass(clazz: Type, param: Param): Tree
  def createSealedTraitTypeclass(traitType: Type, subclasses: Seq[Symbol]): Tree
  def createFallback(forType: Type): Tree

  private lazy val typeclassSymbol = typeclassType.typeSymbol

  private def replaceDeferred(saveReferencesTo: mutable.Set[String]): Transformer = {
    new Transformer {
      override def transform(tree: Tree): Tree = maybeExtractDeferred(tree) match {
        case Some(t) =>
          val referenced = t.tpe.asInstanceOf[ConstantType].value.value.asInstanceOf[String]
          saveReferencesTo.add(referenced)
          q"${TermName(referenced)}"
        case None =>
          super.transform(tree)
      }
    }
  }

  def getTypeclass(forType: Type): Tree = {
    c.inferImplicitValue(appliedType(typeclassSymbol, forType))
  }

  private val currentMemo = {
    GenericDeriveImpl.derivationMemo.get()
  }

  private val currentOrder = {
    GenericDeriveImpl.derivationOrder.get()
  }

  private def withMemoNone[T](tpe: Type)(thunk: => T): T = {
    val orig = currentMemo.get((getClass.getSimpleName, tpe.toString))
    currentMemo((getClass.getSimpleName, tpe.toString)) = None
    try {
      thunk
    } finally {
      if (orig.isDefined) {
        currentMemo((getClass.getSimpleName, tpe.toString)) = orig.get
      } else {
        currentMemo.remove((getClass.getSimpleName, tpe.toString))
      }
      ()
    }
  }

  private def memoTree[T](tpe: Type, symbol: Symbol)(tree: => Tree): Tree = {
    val fresh = c.freshName()
    currentMemo((getClass.getSimpleName, tpe.toString)) = Some(fresh)
    currentOrder.enqueue((this, fresh, tpe, tree))
    deferredInstance(
      tpe,
      c.internal.constantType(Constant(fresh))
    )
  }

  final def derive[T](implicit tTag: WeakTypeTag[T]): Tree = {
    val symbol = tTag.tpe.typeSymbol

    if (currentMemo.get((getClass.getSimpleName, tTag.tpe.toString)).contains(None)) {
      c.abort(c.enclosingPosition, "Skipping derivation macro when getting regular implicit")
    } else if (currentMemo.get((getClass.getSimpleName, tTag.tpe.toString)).flatten.isDefined) {
      deferredInstance(
        tTag.tpe,
        c.internal.constantType(Constant(currentMemo((getClass.getSimpleName, tTag.tpe.toString)).get))
      )
    } else {
      val isRoot = currentMemo.isEmpty
      val regularImplicit = withMemoNone(tTag.tpe) {
        c.inferImplicitValue(
          appliedType(typeclassSymbol, tTag.tpe),
          silent = true
        )
      }

      val deriveTree = if (regularImplicit.isEmpty) {
        if (symbol.isParameter) {
          c.abort(c.enclosingPosition, "Cannot derive a typeclass for a type parameter")
        } else if (symbol.isModuleClass && c.typecheck(c.parse(symbol.asClass.module.fullName), silent = true).nonEmpty) {
          createModuleTypeclass(tTag.tpe, c.parse(symbol.asClass.module.fullName))
        } else if (symbol.isClass && symbol.asClass.isCaseClass && symbol.asType.typeParams.size == tTag.tpe.typeArgs.size) {
          val constructor = symbol.asClass.primaryConstructor
          val companion = symbol.asClass.companion
          if (companion != NoSymbol) {
            companion.info.decl(TermName("apply")).alternatives.foreach(_.asMethod.typeSignature)
          }

          val paramsLists = constructor.asMethod.paramLists
          memoTree(tTag.tpe, symbol) {
            val params: Seq[Seq[Param]] = paramsLists.map(_.zipWithIndex.map { case (p, i) =>
              val transformedValueType = p.typeSignatureIn(tTag.tpe).resultType
              Param(
                p.name,
                transformedValueType.substituteTypes(symbol.asType.typeParams, tTag.tpe.typeArgs),
                if (p.asTerm.isParamWithDefault && companion != NoSymbol) {
                  val defaultTermName = "apply$default$" + (i + 1)
                  Some(q"$companion.${TermName(defaultTermName)}")
                } else None
              )
            })

            createCaseClassTypeclass(tTag.tpe, params)
          }
        } else if (symbol.isClass && tTag.tpe <:< typeOf[AnyVal]) {
          val actualValue = symbol.asClass.primaryConstructor.asMethod.paramLists.head.head
          val param = Param(actualValue.name, actualValue.typeSignatureIn(tTag.tpe).resultType, None)

          memoTree(tTag.tpe, symbol)(createValueClassTypeclass(tTag.tpe, param))
        } else if (symbol.isClass && symbol.asClass.isSealed && symbol.asType.toType.typeArgs.isEmpty) {
          def getSubclasses(clazz: ClassSymbol): Set[Symbol] = {
            // from magnolia
            val children = clazz.knownDirectSubclasses
            val (abstractTypes, concreteTypes) = children.partition(_.isAbstract)

            abstractTypes.map(_.asClass).flatMap(getSubclasses) ++ concreteTypes
          }

          memoTree(tTag.tpe, symbol) {
            createSealedTraitTypeclass(tTag.tpe, getSubclasses(symbol.asClass).toSeq)
          }
        } else {
          memoTree(tTag.tpe, symbol) {
            c.echo(c.enclosingPosition, s"Using fallback derivation for type ${tTag.tpe} (derivation: ${getClass.getSimpleName})")
            createFallback(tTag.tpe)
          }
        }
      } else {
        if (isRoot) {
          regularImplicit
        } else {
          memoTree(tTag.tpe, symbol) {
            regularImplicit
          }
        }
      }

      if (isRoot) {
        val saveReferences = mutable.Set.empty[String]
        val seenImpls = mutable.Set.empty[GenericDeriveImpl]

        def replaceDeferredAllTypeclasses(tree: Tree): Tree = {
          seenImpls.foldLeft(tree) { (tree, impl) =>
            impl.replaceDeferred(saveReferences).transform(tree.asInstanceOf[impl.c.universe.Tree])
              .asInstanceOf[Tree]
          }
        }

        val unwrappedOrder = currentOrder.dequeueAll(_ => true).map { case (impl, name, tpe, t) =>
          seenImpls.add(impl)
          val typeclassTree = c.untypecheck(replaceDeferredAllTypeclasses(t.asInstanceOf[Tree]))

          if (saveReferences.contains(name)) {
            (
              Some(q"var ${TermName(name)}: ${appliedType(impl.typeclassSymbol.asInstanceOf[Symbol], tpe.asInstanceOf[Type])} = null"),
              q"${TermName(name)} = $typeclassTree"
            )
          } else {
            (
              None,
              q"val ${TermName(name)}: ${appliedType(impl.typeclassSymbol.asInstanceOf[Symbol], tpe.asInstanceOf[Type])} = $typeclassTree"
            )
          }
        }

        currentMemo.clear()

        q"{ ..${unwrappedOrder.flatMap(_._1)}; ..${unwrappedOrder.map(_._2)}; ${replaceDeferredAllTypeclasses(deriveTree)} }"
      } else {
        deriveTree
      }
    }
  }
}

object GenericDeriveImpl {
  private[GenericDeriveImpl] val derivationMemo = {
    new ThreadLocal[mutable.Map[(String, String), Option[String]]] {
      override def initialValue() = mutable.Map.empty
    }
  }

  private[GenericDeriveImpl] val derivationOrder = {
    new ThreadLocal[mutable.Queue[(GenericDeriveImpl, String, whitebox.Context#Type, whitebox.Context#Tree)]] {
      override def initialValue() = mutable.Queue.empty
    }
  }
}
