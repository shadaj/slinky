package slinky.next

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native trait Router extends js.Object {
  val query: js.Dynamic = js.native
}

@JSImport("next/router", JSImport.Namespace)
@js.native
object Router extends js.Object {
  def useRouter(): Router = js.native
}
