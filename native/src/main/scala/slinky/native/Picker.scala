package slinky.native

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@react object Picker extends ExternalComponent {
  case class Props(onValueChange: js.UndefOr[(String | Int, Int) => Unit] = js.undefined,
                   selectedValue: js.UndefOr[String | Int] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   testID: js.UndefOr[String] = js.undefined,
                   enabled: js.UndefOr[Boolean] = js.undefined,
                   mode: js.UndefOr[String] = js.undefined,
                   prompt: js.UndefOr[String] = js.undefined,
                   itemStyle: js.UndefOr[js.Object] = js.undefined)

  @js.native
  @JSImport("react-native", "Picker")
  object Component extends js.Object {
    val Item: js.Object = js.native
  }

  override val component = Component

  @react object Item extends ExternalComponent {
    case class Props(label: String, value: String | Int)

    override val component = Component.Item
  }
}
