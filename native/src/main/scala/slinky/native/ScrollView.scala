package slinky.native

import slinky.core.ExternalComponentWithRefType
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.readwrite.ObjectOrWritten

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class ScrollTarget(x: Int, y: Int, animated: Boolean)
case class ScrollOptions(animated: Boolean)

@js.native
trait ScrollViewInstance extends js.Object {
  def scrollTo(target: ObjectOrWritten[ScrollTarget]): Unit      = js.native
  def scrollToEnd(): Unit                                        = js.native
  def scrollToEnd(options: ObjectOrWritten[ScrollOptions]): Unit = js.native
  def flashScrollIndicators(): Unit                              = js.native
}

@react object ScrollView extends ExternalComponentWithRefType[ScrollViewInstance] {
  case class Props(
    alwaysBounceVertical: js.UndefOr[Boolean] = js.undefined,
    contentContainerStyle: js.UndefOr[js.Object] = js.undefined,
    keyboardDismissMode: js.UndefOr[String] = js.undefined,
    keyboardShouldPersistTaps: js.UndefOr[String] = js.undefined,
    onContentSizeChange: js.UndefOr[(Int, Int) => Unit] = js.undefined,
    onMomentumScrollBegin: js.UndefOr[() => Unit] = js.undefined,
    onMomentumScrollEnd: js.UndefOr[() => Unit] = js.undefined,
    onScroll: js.UndefOr[() => Unit] = js.undefined,
    onScrollBeginDrag: js.UndefOr[() => Unit] = js.undefined,
    onScrollEndDrag: js.UndefOr[() => Unit] = js.undefined,
    pagingEnabled: js.UndefOr[Boolean] = js.undefined,
    refreshControl: js.UndefOr[ReactElement] = js.undefined,
    removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
    scrollEnabled: js.UndefOr[Boolean] = js.undefined,
    showsHorizontalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    showsVerticalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    stickyHeaderIndices: js.UndefOr[Seq[Int]] = js.undefined,
    endFillColor: js.UndefOr[String] = js.undefined,
    overScrollMode: js.UndefOr[String] = js.undefined,
    scrollPerfTag: js.UndefOr[String] = js.undefined,
    alwaysBounceHorizontal: js.UndefOr[Boolean] = js.undefined,
    horizontal: js.UndefOr[Boolean] = js.undefined,
    automaticallyAdjustContentInsets: js.UndefOr[Boolean] = js.undefined,
    bounces: js.UndefOr[Boolean] = js.undefined,
    bouncesZoom: js.UndefOr[Boolean] = js.undefined,
    canCancelContentTouches: js.UndefOr[Boolean] = js.undefined,
    centerContent: js.UndefOr[Boolean] = js.undefined,
    contentInset: js.UndefOr[BoundingBox] = js.undefined,
    contentInsetAdjustmentBehavior: js.UndefOr[String] = js.undefined,
    contentOffset: js.UndefOr[ContentOffset] = js.undefined,
    decelerationRate: js.UndefOr[String | Double] = js.undefined,
    directionalLockEnabled: js.UndefOr[Boolean] = js.undefined,
    indicatorStyle: js.UndefOr[String] = js.undefined,
    maximumZoomScale: js.UndefOr[Double] = js.undefined,
    minimumZoomScale: js.UndefOr[Double] = js.undefined,
    pinchGestureEnabled: js.UndefOr[Boolean] = js.undefined,
    scrollEventThrottle: js.UndefOr[Int] = js.undefined,
    scrollIndicatorInsets: js.UndefOr[BoundingBox] = js.undefined,
    scrollsToTop: js.UndefOr[Boolean] = js.undefined,
    snapToAlignment: js.UndefOr[String] = js.undefined,
    snapToInterval: js.UndefOr[Double] = js.undefined,
    zoomScale: js.UndefOr[Double] = js.undefined
  )

  @js.native
  @JSImport("react-native", "ScrollView")
  object Component extends js.Object

  override val component = Component
}
