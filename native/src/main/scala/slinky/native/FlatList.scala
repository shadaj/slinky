package slinky.native

import slinky.core._
import slinky.core.facade.ReactElement
import slinky.readwrite.{ObjectOrWritten, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class Separators(highlight: () => Unit, unhighlight: () => Unit, updateProps: (String, js.Object) => ReactElement)
case class ItemLayout(length: Int, offset: Int, index: Int)

case class OnEndReachedInfo(distanceFromEnd: Int)
case class OnEndReachedEvent(info: OnEndReachedInfo)

case class ViewableItemsChangedInfo(viewableItems: Seq[js.Object], changed: Seq[js.Object])
case class ViewableItemsChangedEvent(info: ViewableItemsChangedInfo)

case class ScrollToEndParams(animated: js.UndefOr[Boolean] = js.undefined)
case class ScrollToIndexParams(index: Int,
                               viewOffset: Int,
                               animated: js.UndefOr[Boolean] = js.undefined,
                               viewPosition: js.UndefOr[Double] = js.undefined)

case class ScrollToItemParams[T](item: T,
                                 animated: js.UndefOr[Boolean] = js.undefined,
                                 viewPosition: js.UndefOr[Double] = js.undefined)

case class ScrollToOffsetParams(offset: Int,
                                animated: js.UndefOr[Boolean] = js.undefined)

@js.native
trait FlatListInstance[T] extends js.Object {
  def scrollToEnd(): Unit = js.native
  def scrollToEnd(params: ObjectOrWritten[ScrollToEndParams]): Unit = js.native

  def scrollToIndex(params: ObjectOrWritten[ScrollToIndexParams]): Unit = js.native

  def scrollToItem(params: ObjectOrWritten[ScrollToItemParams[T]]): Unit = js.native

  def scrollToOffset(params: ObjectOrWritten[ScrollToOffsetParams]): Unit = js.native

  def recordInteraction(): Unit = js.native

  def flashScrollIndicators(): Unit = js.native
}

case class FlatListProps[T](renderItem: (T, Int, Separators) => Unit,
                            data: Seq[T],
                            ItemSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
                            ListEmptyComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                            ListFooterComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                            ListHeaderComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                            columnWrapperStyle: js.UndefOr[js.Object] = js.undefined,
                            extraData: js.UndefOr[Any] = js.undefined,
                            getItemLayout: js.UndefOr[(js.Object, Int) => ItemLayout] = js.undefined,
                            horizontal: js.UndefOr[Boolean] = js.undefined,
                            initialNumToRender: js.UndefOr[Int] = js.undefined,
                            initialScrollIndex: js.UndefOr[Int] = js.undefined,
                            inverted: js.UndefOr[Boolean] = js.undefined,
                            keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined,
                            numColumns: js.UndefOr[Int] = js.undefined,
                            onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
                            onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
                            onRefresh: js.UndefOr[() => Unit] = js.undefined,
                            onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent => Unit] = js.undefined,
                            progressViewOffset: js.UndefOr[Double] = js.undefined,
                            legacyImplementation: js.UndefOr[Boolean] = js.undefined,
                            refreshing: js.UndefOr[Boolean] = js.undefined,
                            removeClippedSubviews: js.UndefOr[Boolean] = js.undefined)

object FlatListProps {
  val anyWriter = implicitly[Writer[FlatListProps[Any]]].asInstanceOf[ExternalPropsWriterProvider]
}

object FlatList extends ExternalComponentWithRefType[FlatListInstance[js.Object]]()(FlatListProps.anyWriter) {
  type Props = FlatListProps[Any]

  @js.native
  @JSImport("react-native", "FlatList")
  object Component extends js.Object

  override val component = Component

  def apply[T](data: Seq[T],
               renderItem: (T, Int, Separators) => Unit,
               ItemSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
               ListEmptyComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
               ListFooterComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
               ListHeaderComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
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
               onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent => Unit] = js.undefined,
               progressViewOffset: js.UndefOr[Double] = js.undefined,
               legacyImplementation: js.UndefOr[Boolean] = js.undefined,
               refreshing: js.UndefOr[Boolean] = js.undefined,
               removeClippedSubviews: js.UndefOr[Boolean] = js.undefined): BuildingComponent[FlatListInstance[T], js.Object] = {
    new BuildingComponent(
      component,
      FlatListProps.anyWriter.asInstanceOf[Writer[Props]].write(FlatListProps(
        renderItem = (o, i, s) => renderItem(o.asInstanceOf[T], i, s),
        data = data,
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
        onViewableItemsChanged = onViewableItemsChanged,
        progressViewOffset = progressViewOffset,
        legacyImplementation = legacyImplementation,
        refreshing = refreshing,
        removeClippedSubviews = removeClippedSubviews
      )),
      null, null, Seq.empty
    )
  }
}
