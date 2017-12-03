package me.shadaj.slinky.remarkreact

import me.shadaj.slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("remark", JSImport.Default)
object Remark extends js.Function0[js.Object] {
  override def apply(): RemarkInstance = js.native
}

@js.native
trait RemarkRenderer[T] extends js.Object

@js.native
trait RemarkInstance extends js.Object {
  def use[T](renderer: RemarkRenderer[T]): RemarkInstanceWithRenderer[T] = js.native
  def use[T](renderer: RemarkRenderer[T], options: js.Object): RemarkInstanceWithRenderer[T] = js.native
}

@js.native
trait RemarkInstanceWithRenderer[T] extends js.Object {
  def processSync(text: String): RemarkResult[T] = js.native
}

@js.native
trait RemarkResult[T] extends js.Object {
  val contents: T = js.native
}

@js.native
@JSImport("remark-react", JSImport.Default)
object ReactRenderer extends RemarkRenderer[ReactElement]
