package slinky.testrenderer

import slinky.core.ReactComponentClass
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}

@js.native
trait TestRenderer extends js.Object {
  def toJSON(): js.Object                 = js.native
  def toTree(): js.Object                 = js.native
  def update(element: ReactElement): Unit = js.native
  def unmount(): Unit                     = js.native
  def getInstance(): js.Object            = js.native
  val root: TestInstance                  = js.native
}

@js.native
@JSImport("react-test-renderer", JSImport.Default)
object TestRenderer extends js.Object {
  def create(element: ReactElement): TestRenderer = js.native
  def act(callback: js.Function0[js.Any]): Unit   = js.native
}

@js.native
trait TestInstance extends js.Object {
  def find(test: js.Function1[TestInstance, Boolean]): TestInstance = js.native
  def findByType(`type`: ReactComponentClass[_]): TestInstance      = js.native
  def findByProps(props: js.Object): TestInstance                   = js.native

  def findAll(test: js.Function1[TestInstance, Boolean]): js.Array[TestInstance] = js.native
  def findAllByType(`type`: ReactComponentClass[_]): js.Array[TestInstance]      = js.native
  def findAllByProps(props: js.Object): js.Array[TestInstance]                   = js.native

  val instance: js.Object               = js.native
  @JSName("type") val `type`: js.Object = js.native
  val props: js.Object                  = js.native
  val parent: TestInstance              = js.native
  val children: js.Array[TestInstance]  = js.native
}
