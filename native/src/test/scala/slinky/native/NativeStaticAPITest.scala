package slinky.native

import org.scalatest.FunSuite

import scala.scalajs.js

class NativeStaticAPITest extends FunSuite {
  test("Can fire an alert") {
    Alert.alert("alert!")
  }

  test("Can read clipboard value") {
    assert(!js.isUndefined(Clipboard.getString))
  }

  test("Can write clipboard value") {
    Clipboard.setString("bar")
  }
}
