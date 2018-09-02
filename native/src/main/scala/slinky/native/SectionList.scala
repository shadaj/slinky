package slinky.native

import slinky.core._
import slinky.core.facade.ReactElement
import slinky.readwrite.ObjectOrWritten

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class SectionRenderItemInfo[T](item: T, index: Int, section: Section[Any], separators: Separators)

case class Section[T](data: Seq[T],
                      key: js.UndefOr[String] = js.undefined,
                      renderItem: js.UndefOr[SectionRenderItemInfo[T] => ReactElement] = js.undefined,
                      ItemSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
                      keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined,
                      extraData: js.UndefOr[Any] = js.undefined)

case class RenderSectionInfo[T](section: Section[T])

@js.native
trait SectionListInstance[T] extends js.Object {
  def scrollToEnd(): Unit = js.native
  def scrollToEnd(params: ObjectOrWritten[ScrollToEndParams]): Unit = js.native

  def scrollToIndex(params: ObjectOrWritten[ScrollToIndexParams]): Unit = js.native

  def scrollToItem(params: ObjectOrWritten[ScrollToItemParams[T]]): Unit = js.native

  def scrollToOffset(params: ObjectOrWritten[ScrollToOffsetParams]): Unit = js.native

  def recordInteraction(): Unit = js.native

  def flashScrollIndicators(): Unit = js.native
}

object SectionList extends ExternalComponentWithRefType[SectionListInstance[Any]] {
  case class Props(sections: Seq[Section[Any]],
                   initialNumToRender: js.UndefOr[Int] = js.undefined,
                   keyExtractor: js.UndefOr[(Any, Int) => String] = js.undefined,
                   renderItem: js.UndefOr[SectionRenderItemInfo[Any] => ReactElement] = js.undefined,
                   onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
                   extraData: js.UndefOr[Any] = js.undefined,
                   ItemSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
                   inverted: js.UndefOr[Boolean] = js.undefined,
                   ListFooterComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
                   legacyImplementation: js.UndefOr[Boolean] = js.undefined,
                   ListEmptyComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
                   onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
                   onRefresh: js.UndefOr[() => Unit] = js.undefined,
                   onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[Any] => Unit] = js.undefined,
                   refreshing: js.UndefOr[Boolean] = js.undefined,
                   removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
                   ListHeaderComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
                   renderSectionFooter: js.UndefOr[RenderSectionInfo[Any] => ReactElement] = js.undefined,
                   renderSectionHeader: js.UndefOr[RenderSectionInfo[Any] => ReactElement] = js.undefined,
                   SectionSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
                   stickySectionHeadersEnabled: js.UndefOr[Boolean] = js.undefined,
                   getItemLayout: js.UndefOr[(Any, Int) => ItemLayout] = js.undefined)

  @js.native
  @JSImport("react-native", "SectionList")
  object Component extends js.Object

  override val component = Component

  def apply[T](sections: Seq[Section[T]],
               initialNumToRender: js.UndefOr[Int] = js.undefined,
               keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined,
               renderItem: js.UndefOr[SectionRenderItemInfo[T] => ReactElement] = js.undefined,
               onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
               extraData: js.UndefOr[Any] = js.undefined,
               ItemSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
               inverted: js.UndefOr[Boolean] = js.undefined,
               ListFooterComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
               legacyImplementation: js.UndefOr[Boolean] = js.undefined,
               ListEmptyComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
               onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
               onRefresh: js.UndefOr[() => Unit] = js.undefined,
               onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[T] => Unit] = js.undefined,
               refreshing: js.UndefOr[Boolean] = js.undefined,
               removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
               ListHeaderComponent: js.UndefOr[ReactComponentClass[_] | (() => ReactElement) | ReactElement] = js.undefined,
               renderSectionFooter: js.UndefOr[RenderSectionInfo[T] => ReactElement] = js.undefined,
               renderSectionHeader: js.UndefOr[RenderSectionInfo[T] => ReactElement] = js.undefined,
               SectionSeparatorComponent: js.UndefOr[ReactComponentClass[_]] = js.undefined,
               stickySectionHeadersEnabled: js.UndefOr[Boolean] = js.undefined,
               getItemLayout: js.UndefOr[(T, Int) => ItemLayout] = js.undefined): BuildingComponent[Nothing, SectionListInstance[T]] = {
    apply(Props(
      sections = sections.asInstanceOf[Seq[Section[Any]]],
      initialNumToRender = initialNumToRender,
      keyExtractor = keyExtractor.map(f => (a, i) => f(a.asInstanceOf[T], i)),
      renderItem = renderItem.map(f => o => f(o.asInstanceOf[SectionRenderItemInfo[T]])),
      onEndReached = onEndReached,
      extraData = extraData,
      ItemSeparatorComponent = ItemSeparatorComponent,
      inverted = inverted,
      ListFooterComponent = ListFooterComponent,
      legacyImplementation = legacyImplementation,
      ListEmptyComponent = ListEmptyComponent,
      onEndReachedThreshold = onEndReachedThreshold,
      onRefresh = onRefresh,
      onViewableItemsChanged = onViewableItemsChanged.asInstanceOf[js.UndefOr[ViewableItemsChangedEvent[Any] => Unit]],
      refreshing = refreshing,
      removeClippedSubviews = removeClippedSubviews,
      ListHeaderComponent = ListHeaderComponent,
      renderSectionFooter = renderSectionFooter.asInstanceOf[js.UndefOr[RenderSectionInfo[Any] => ReactElement]],
      renderSectionHeader = renderSectionHeader.asInstanceOf[js.UndefOr[RenderSectionInfo[Any] => ReactElement]],
      SectionSeparatorComponent = SectionSeparatorComponent,
      stickySectionHeadersEnabled = stickySectionHeadersEnabled,
      getItemLayout = getItemLayout.map(f => (v, i) => f(v.asInstanceOf[T], i))
    )).asInstanceOf[BuildingComponent[Nothing, SectionListInstance[T]]]
  }
}
