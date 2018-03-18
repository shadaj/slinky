package slinky.native

import org.scalatest.FunSuite

class NativeStaticAPITest extends FunSuite {
  test("Can fire an alert") {
    Alert.alert("alert!")
  }
}
