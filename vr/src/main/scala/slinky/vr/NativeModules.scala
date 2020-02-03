package slinky.vr

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-360", "NativeModules")
object NativeModules extends js.Object {
  @js.native
  object VideoModule extends js.Object {
    def createPlayer(name: String): Unit                  = js.native
    def destroyPlayer(name: String): Unit                 = js.native
    def play(name: String, options: js.Object): Unit      = js.native
    def pause(name: String): Unit                         = js.native
    def resume(name: String): Unit                        = js.native
    def stop(name: String): Unit                          = js.native
    def seek(name: String, timeMs: Int): Unit             = js.native
    def setParams(name: String, options: js.Object): Unit = js.native
  }
}
