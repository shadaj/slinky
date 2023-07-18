package slinky.core

import scala.scalajs.js

import scala.reflect.macros.whitebox

trait StateWriterProvider extends js.Object
object StateWriterProvider {
  def impl(c: whitebox.Context): c.Expr[StateWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(
      q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Writer[comp.State] = null"
    ) // scalafix:ok
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type], silent = false)
    c.Expr(
      q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateWriterProvider]"
    )
  }

  implicit def get: StateWriterProvider = macro impl
}
