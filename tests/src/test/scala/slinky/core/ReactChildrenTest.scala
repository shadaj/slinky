package slinky.core

import slinky.core.facade.{React, ReactChildren, ReactElement}
import slinky.web.html.div
import org.scalatest.FunSuite

import scala.scalajs.js

class ReactChildrenTest extends FunSuite {
  import React.Children._

  test("Can map over a single element") {
    assert(count(map((div(): ReactElement).asInstanceOf[ReactChildren], elem => elem)) == 1)
  }

  test("Can map over multiple elements") {
    assert(count(map(js.Array[ReactElement](div(), div()).asInstanceOf[ReactChildren], elem => elem)) == 2)
  }

  test("Can iterate with forEach over a single element") {
    var count = 0
    forEach((div(): ReactElement).asInstanceOf[ReactChildren], _ => count += 1)
    assert(count == 1)
  }

  test("Can iterate with forEach over multiple elements") {
    var count = 0
    forEach(js.Array[ReactElement](div(), div()).asInstanceOf[ReactChildren], _ => count += 1)
    assert(count == 2)
  }

  test("Can get count of a single element") {
    assert(count((div(): ReactElement).asInstanceOf[ReactChildren]) == 1)
  }

  test("Can get count of multiple elements") {
    assert(count(js.Array[ReactElement](div(), div()).asInstanceOf[ReactChildren]) == 2)
  }

  test("Can convert single element to array") {
    assert(toArray((div(): ReactElement).asInstanceOf[ReactChildren]).length == 1)
  }

  test("Can convert multiple elements to array") {
    assert(toArray(js.Array[ReactElement](div(), div()).asInstanceOf[ReactChildren]).length == 2)
  }
}
