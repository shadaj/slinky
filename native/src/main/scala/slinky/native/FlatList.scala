package slinky.native

import slinky.core._
import slinky.core.facade.ReactElement
import slinky.readwrite.{ObjectOrWritten, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class Separators(highlight: () => Unit, unhighlight: () => Unit, updateProps: (String, js.Object) => ReactElement)
case class ItemLayout(length: Int, offset: Int, index: Int)

case class OnEndReachedEvent(distanceFromEnd: Double)

case class ViewToken[T](
  item: T,
  key: String,
  index: js.UndefOr[Int],
  isViewable: Boolean,
  section: js.UndefOr[Section[T]]
)
case class ViewableItemsChangedInfo[T](viewableItems: Seq[js.Object], changed: Seq[js.Object])
case class ViewableItemsChangedEvent[T](info: ViewableItemsChangedInfo[T])

case class ScrollToEndParams(animated: js.UndefOr[Boolean] = js.undefined)
case class ScrollToIndexParams(
  index: Int,
  viewOffset: Int,
  animated: js.UndefOr[Boolean] = js.undefined,
  viewPosition: js.UndefOr[Double] = js.undefined
)

case class ScrollToItemParams[T](
  item: T,
  animated: js.UndefOr[Boolean] = js.undefined,
  viewPosition: js.UndefOr[Double] = js.undefined
)

case class ScrollToOffsetParams(offset: Int, animated: js.UndefOr[Boolean] = js.undefined)

case class RenderItemInfo[T](item: T, index: Int, separators: Separators)

@js.native
trait FlatListInstance[T] extends js.Object {
  def scrollToEnd(): Unit                                           = js.native
  def scrollToEnd(params: ObjectOrWritten[ScrollToEndParams]): Unit = js.native

  def scrollToIndex(params: ObjectOrWritten[ScrollToIndexParams]): Unit = js.native

  def scrollToItem(params: ObjectOrWritten[ScrollToItemParams[T]]): Unit = js.native

  def scrollToOffset(params: ObjectOrWritten[ScrollToOffsetParams]): Unit = js.native

  def recordInteraction(): Unit = js.native

  def flashScrollIndicators(): Unit = js.native
}

object FlatList extends ExternalComponentWithRefType[FlatListInstance[Any]] {
  case class Props(
    data: Seq[Object],
    renderItem: RenderItemInfo[Object] => ReactElement,
    ItemSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
    ListEmptyComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    ListFooterComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    ListHeaderComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    columnWrapperStyle: js.UndefOr[js.Object] = js.undefined,
    extraData: js.UndefOr[Object] = js.undefined,
    getItemLayout: js.UndefOr[(js.Object, Int) => ItemLayout] = js.undefined,
    horizontal: js.UndefOr[Boolean] = js.undefined,
    initialNumToRender: js.UndefOr[Int] = js.undefined,
    initialScrollIndex: js.UndefOr[Int] = js.undefined,
    inverted: js.UndefOr[Boolean] = js.undefined,
    keyExtractor: js.UndefOr[(Object, Int) => String] = js.undefined,
    numColumns: js.UndefOr[Int] = js.undefined,
    onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
    onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
    onRefresh: js.UndefOr[() => Unit] = js.undefined,
    onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[Object] => Unit] = js.undefined,
    progressViewOffset: js.UndefOr[Double] = js.undefined,
    legacyImplementation: js.UndefOr[Boolean] = js.undefined,
    refreshing: js.UndefOr[Boolean] = js.undefined,
    removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
    // start of props inherited from ScrollView
    pagingEnabled: js.UndefOr[Boolean] = js.undefined,
    scrollEnabled: js.UndefOr[Boolean] = js.undefined,
    showsHorizontalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    showsVerticalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    snapToAlignment: js.UndefOr[String] = js.undefined,
    snapToInterval: js.UndefOr[Double] = js.undefined
  )

  @js.native
  @JSImport("react-native", "FlatList")
  object Component extends js.Object

  override val component = Component

  private val writer = implicitly[Writer[Props]]

  def apply[T](
    data: Seq[T],
    renderItem: RenderItemInfo[T] => ReactElement,
    ItemSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
    ListEmptyComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    ListFooterComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    ListHeaderComponent: js.UndefOr[ReactComponentClass[_] |(() => ReactElement) | ReactElement] = js.undefined,
    columnWrapperStyle: js.UndefOr[js.Object] = js.undefined,
    extraData: js.UndefOr[Any] = js.undefined,
    getItemLayout: js.UndefOr[(T, Int) => ItemLayout] = js.undefined,
    horizontal: js.UndefOr[Boolean] = js.undefined,
    initialNumToRender: js.UndefOr[Int] = js.undefined,
    initialScrollIndex: js.UndefOr[Int] = js.undefined,
    inverted: js.UndefOr[Boolean] = js.undefined,
    keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined,
    numColumns: js.UndefOr[Int] = js.undefined,
    onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
    onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
    onRefresh: js.UndefOr[() => Unit] = js.undefined,
    onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[T] => Unit] = js.undefined,
    progressViewOffset: js.UndefOr[Double] = js.undefined,
    legacyImplementation: js.UndefOr[Boolean] = js.undefined,
    refreshing: js.UndefOr[Boolean] = js.undefined,
    removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
    // start of props inherited from ScrollView
    pagingEnabled: js.UndefOr[Boolean] = js.undefined,
    scrollEnabled: js.UndefOr[Boolean] = js.undefined,
    showsHorizontalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    showsVerticalScrollIndicator: js.UndefOr[Boolean] = js.undefined,
    snapToAlignment: js.UndefOr[String] = js.undefined,
    snapToInterval: js.UndefOr[Double] = js.undefined
  ): BuildingComponent[FlatListInstance[T], js.Object] =
    new BuildingComponent(
      js.Array(
        component.asInstanceOf[js.Any],
        writer.write(
          Props(
            data = data.asInstanceOf[Seq[Object]],
            renderItem = o => renderItem(o.asInstanceOf[RenderItemInfo[T]]),
            ItemSeparatorComponent = ItemSeparatorComponent,
            ListEmptyComponent = ListEmptyComponent,
            ListFooterComponent = ListFooterComponent,
            ListHeaderComponent = ListHeaderComponent,
            columnWrapperStyle = columnWrapperStyle,
            extraData = extraData,
            getItemLayout = getItemLayout.map(f => (o, i) => f(o.asInstanceOf[T], i)),
            horizontal = horizontal,
            initialNumToRender = initialNumToRender,
            initialScrollIndex = initialScrollIndex,
            inverted = inverted,
            keyExtractor = keyExtractor.map(f => (o, i) => f(o.asInstanceOf[T], i)),
            numColumns = numColumns,
            onEndReached = onEndReached,
            onEndReachedThreshold = onEndReachedThreshold,
            onRefresh = onRefresh,
            onViewableItemsChanged =
              onViewableItemsChanged.map(f => (e) => f(e.asInstanceOf[ViewableItemsChangedEvent[T]])),
            progressViewOffset = progressViewOffset,
            legacyImplementation = legacyImplementation,
            refreshing = refreshing,
            // start of props inherited from ScrollView
            pagingEnabled = pagingEnabled,
            scrollEnabled = scrollEnabled,
            showsHorizontalScrollIndicator = showsHorizontalScrollIndicator,
            showsVerticalScrollIndicator = showsVerticalScrollIndicator,
            snapToAlignment = snapToAlignment,
            snapToInterval = snapToInterval
          )
        )
      )
    )
}
