package me.shadaj.slinky.universalanalytics

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("universal-analytics", JSImport.Default)
@js.native
object UniversalAnalytics extends js.Object {
  def apply(id: String, options: js.Object = js.Dynamic.literal()): Visitor = js.native
}

@js.native trait Visitor extends js.Object {
  def pageview(path: String): Sendable = js.native
}

@js.native trait Sendable extends js.Object {
  def send(): Unit = js.native
}