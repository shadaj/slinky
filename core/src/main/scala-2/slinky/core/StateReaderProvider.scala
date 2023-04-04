package slinky.core

import scala.scalajs.js

import scala.reflect.macros.whitebox

trait StateReaderProvider extends js.Object
object StateReaderProvider {
  def impl(c: whitebox.Context): c.Expr[StateReaderProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(
      q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Reader[comp.State] = null"
    ) // scalafix:ok
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type], silent = false)
    c.Expr(
      q"if (_root_.scala.scalajs.LinkingInfo.productionMode) null else $tpcls.asInstanceOf[_root_.slinky.core.StateReaderProvider]"
    )
  }

  implicit def get: StateReaderProvider = macro impl
}
