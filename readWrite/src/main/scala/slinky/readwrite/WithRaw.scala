package slinky.readwrite

import scala.scalajs.js

trait WithRaw {
  def raw: js.Object = this.asInstanceOf[js.Dynamic].__slinky_raw.asInstanceOf[js.Object]
}
