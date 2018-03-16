package slinky.native

import slinky.core.ExternalComponentWithRefType
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

case class ContentSize(width: Int, height: Int)
case class ContentSizeEvent(contentSize: ContentSize)
case class ContentOffset(x: Int, y: Int)
case class ContentOffsetEvent(contentOffset: ContentOffset)
case class Selection(start: Int, end: Int)
case class SelectionEvent(selection: Selection)
case class KeyPressEvent(key: String)

@js.native
trait TextInputInstance extends js.Object {
  def isFocused(): Boolean = js.native
  def clear(): Unit = js.native
}

@react object TextInput extends ExternalComponentWithRefType[TextInputInstance] {
  case class Props(placeholderTextColor: js.UndefOr[String] = js.undefined,
                   allowFontScaling: js.UndefOr[Boolean] = js.undefined,
                   autoCorrect: js.UndefOr[Boolean] = js.undefined,
                   autoFocus: js.UndefOr[Boolean] = js.undefined,
                   blurOnSubmit: js.UndefOr[Boolean] = js.undefined,
                   caretHidden: js.UndefOr[Boolean] = js.undefined,
                   contextMenuHidden: js.UndefOr[Boolean] = js.undefined,
                   defaultValue: js.UndefOr[String] = js.undefined,
                   editable: js.UndefOr[Boolean] = js.undefined,
                   keyboardType: js.UndefOr[String] = js.undefined,
                   maxHeight: js.UndefOr[Int] = js.undefined,
                   maxLength: js.UndefOr[Int] = js.undefined,
                   multiline: js.UndefOr[Boolean] = js.undefined,
                   onBlur: js.UndefOr[() => Unit] = js.undefined,
                   onChange: js.UndefOr[() => Unit] = js.undefined,
                   onChangeText: js.UndefOr[String => Unit] = js.undefined,
                   onContextSizeChange: js.UndefOr[NativeSyntheticEvent[ContentSizeEvent] => Unit] = js.undefined,
                   onEndEditing: js.UndefOr[() => Unit] = js.undefined,
                   onFocus: js.UndefOr[() => Unit] = js.undefined,
                   onLayout: js.UndefOr[NativeSyntheticEvent[LayoutChangeEvent] => Unit] = js.undefined,
                   onScroll: js.UndefOr[NativeSyntheticEvent[ContentOffsetEvent] => Unit] = js.undefined,
                   onSelectionChange: js.UndefOr[NativeSyntheticEvent[SelectionEvent] => Unit] = js.undefined,
                   onSubmitEditing: js.UndefOr[() => Unit] = js.undefined,
                   placeholder: js.UndefOr[String] = js.undefined,
                   autoCapitalize: js.UndefOr[String] = js.undefined,
                   returnKeyType: js.UndefOr[String] = js.undefined,
                   secureTextEntry: js.UndefOr[Boolean] = js.undefined,
                   selectTextOnFocus: js.UndefOr[Boolean] = js.undefined,
                   selection: js.UndefOr[Selection] = js.undefined,
                   selectionColor: js.UndefOr[String] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined,
                   value: js.UndefOr[String] = js.undefined,
                   disableFullscreenUI: js.UndefOr[Boolean] = js.undefined,
                   inlineImageLeft: js.UndefOr[String] = js.undefined,
                   inlineImagePadding: js.UndefOr[Int] = js.undefined,
                   numberOfLines: js.UndefOr[Int] = js.undefined,
                   returnKeyLabel: js.UndefOr[String] = js.undefined,
                   textBreakStrategy: js.UndefOr[String] = js.undefined,
                   underlineColorAndroid: js.UndefOr[String] = js.undefined,
                   clearButtonMode: js.UndefOr[String] = js.undefined,
                   clearTextOnFocus: js.UndefOr[Boolean] = js.undefined,
                   dataDetectorTypes: js.UndefOr[String] = js.undefined,
                   enablesReturnKeyAutomatically: js.UndefOr[Boolean] = js.undefined,
                   keyboardAppearance: js.UndefOr[String] = js.undefined,
                   onKeyPress: js.UndefOr[NativeSyntheticEvent[KeyPressEvent] => Unit] = js.undefined,
                   selectionState: js.UndefOr[js.Object] = js.undefined,
                   spellCheck: js.UndefOr[Boolean] = js.undefined)

  @js.native
  @JSImport("react-native", "TextInput")
  object Component extends js.Object

  override val component = Component
}
