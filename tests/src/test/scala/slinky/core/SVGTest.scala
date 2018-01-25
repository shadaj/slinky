package slinky.core

import slinky.core.facade.ReactElement
import org.scalatest.FunSuite
import slinky.web.svg._

import scala.scalajs.js

class SVGTest extends FunSuite {
  test("Can specify key attribute for SVG element") {
    val instance: ReactElement = circle(key := "1")

    assert(instance.asInstanceOf[js.Dynamic].key.asInstanceOf[String] == "1")
  }
}
