package slinky.core

import scala.scalajs.js

import scala.reflect.macros.whitebox

// same as PropsWriterProvider except it always returns the typeclass instead of nulling it out in fullOpt mode
trait ExternalPropsWriterProvider extends js.Object
object ExternalPropsWriterProvider {
  def impl(c: whitebox.Context): c.Expr[ExternalPropsWriterProvider] = {
    import c.universe._
    val compName = c.internal.enclosingOwner.owner.asClass
    val q"$_; val x: $typedReaderType = null" = c.typecheck(
      q"@_root_.scala.annotation.unchecked.uncheckedStable val comp: $compName = null; val x: _root_.slinky.readwrite.Writer[comp.Props] = null"
    ) // scalafix:ok
    val tpcls = c.inferImplicitValue(typedReaderType.tpe.asInstanceOf[c.Type])
    c.Expr(q"$tpcls.asInstanceOf[_root_.slinky.core.ExternalPropsWriterProvider]")
  }

  implicit def get: ExternalPropsWriterProvider = macro impl
}
