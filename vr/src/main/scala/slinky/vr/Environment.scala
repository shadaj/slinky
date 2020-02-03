package slinky.vr

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-360", "Environment")
object Environment extends js.Object {
  def clearBackground(): Unit                                                                 = js.native
  def setBackgroundImage(url: js.Object, options: js.UndefOr[js.Object] = js.undefined): Unit = js.native
  def setBackgroundVideo(handle: String): Unit                                                = js.native
}
