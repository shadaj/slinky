package slinky.native

import slinky.core._
import slinky.core.facade.ReactElement
import slinky.readwrite.{ObjectOrWritten, Writer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

case class Section[T](data: Seq[T],
                      key: js.UndefOr[String] = js.undefined,
                      renderItem: js.UndefOr[(T, Int, Separators) => ReactElement] = js.undefined,
                      ItemSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
                      keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined)

case class RenderSectionInfo[T](section: Section[T])
case class RenderSectionEvent[T](info: RenderSectionInfo[T])

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
                   renderItem: js.UndefOr[(Any, Int, Section[Any], Separators) => ReactElement] = js.undefined,
                   onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
                   extraData: js.UndefOr[Any] = js.undefined,
                   ItemSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
                   inverted: js.UndefOr[Boolean] = js.undefined,
                   ListFooterComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                   legacyImplementation: js.UndefOr[Boolean] = js.undefined,
                   ListEmptyComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                   onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
                   onRefresh: js.UndefOr[() => Unit] = js.undefined,
                   onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[Any] => Unit] = js.undefined,
                   refreshing: js.UndefOr[Boolean] = js.undefined,
                   removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
                   ListHeaderComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
                   renderSectionFooter: js.UndefOr[RenderSectionEvent[Any] => ReactElement] = js.undefined,
                   renderSectionHeader: js.UndefOr[RenderSectionEvent[Any] => ReactElement] = js.undefined,
                   SectionSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
                   stickySectionHeadersEnabled: js.UndefOr[Boolean] = js.undefined,
                   getItemLayout: js.UndefOr[(Any, Int) => ItemLayout] = js.undefined)

  @js.native
  @JSImport("react-native", "SectionList")
  object Component extends js.Object

  override val component = Component

  private val writer = implicitly[Writer[Props]]

  def apply[T](sections: Seq[Section[T]],
               initialNumToRender: js.UndefOr[Int] = js.undefined,
               keyExtractor: js.UndefOr[(T, Int) => String] = js.undefined,
               renderItem: js.UndefOr[(T, Int, Section[T], Separators) => ReactElement] = js.undefined,
               onEndReached: js.UndefOr[OnEndReachedEvent => Unit] = js.undefined,
               extraData: js.UndefOr[Any] = js.undefined,
               ItemSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
               inverted: js.UndefOr[Boolean] = js.undefined,
               ListFooterComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
               legacyImplementation: js.UndefOr[Boolean] = js.undefined,
               ListEmptyComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
               onEndReachedThreshold: js.UndefOr[Double] = js.undefined,
               onRefresh: js.UndefOr[() => Unit] = js.undefined,
               onViewableItemsChanged: js.UndefOr[ViewableItemsChangedEvent[T] => Unit] = js.undefined,
               refreshing: js.UndefOr[Boolean] = js.undefined,
               removeClippedSubviews: js.UndefOr[Boolean] = js.undefined,
               ListHeaderComponent: js.UndefOr[ReactComponentClass | (() => ReactElement) | ReactElement] = js.undefined,
               renderSectionFooter: js.UndefOr[RenderSectionEvent[T] => ReactElement] = js.undefined,
               renderSectionHeader: js.UndefOr[RenderSectionEvent[T] => ReactElement] = js.undefined,
               SectionSeparatorComponent: js.UndefOr[ReactComponentClass] = js.undefined,
               stickySectionHeadersEnabled: js.UndefOr[Boolean] = js.undefined,
               getItemLayout: js.UndefOr[(T, Int) => ItemLayout] = js.undefined): BuildingComponent[SectionListInstance[T], js.Object] = {
    new BuildingComponent(
      component,
      writer.write(Props(
        sections = sections.asInstanceOf[Seq[Section[Any]]],
        initialNumToRender = initialNumToRender,
        keyExtractor = keyExtractor.map(f => (a, i) => f(a.asInstanceOf[T], i)),
        renderItem = renderItem.map(f => (a, i, s, sp) => f(a.asInstanceOf[T], i, s.asInstanceOf[Section[T]], sp)),
        onEndReached = onEndReached,
        extraData = extraData,
        ItemSeparatorComponent = ItemSeparatorComponent,
        inverted = inverted,
        ListFooterComponent = ListFooterComponent,
        legacyImplementation = legacyImplementation,
        ListEmptyComponent = ListEmptyComponent,
        onEndReachedThreshold = onEndReachedThreshold,
        onRefresh = onRefresh,
        onViewableItemsChanged = onViewableItemsChanged.map(f => (e) => f(e.asInstanceOf[ViewableItemsChangedEvent[T]])),
        refreshing = refreshing,
        removeClippedSubviews = removeClippedSubviews,
        ListHeaderComponent = ListHeaderComponent,
        renderSectionFooter = renderSectionFooter.map(f => (e) => f(e.asInstanceOf[RenderSectionEvent[T]])),
        renderSectionHeader = renderSectionHeader.map(f => (e) => f(e.asInstanceOf[RenderSectionEvent[T]])),
        SectionSeparatorComponent = SectionSeparatorComponent,
        stickySectionHeadersEnabled = stickySectionHeadersEnabled,
        getItemLayout = getItemLayout.map(f => (v, i) => f(v.asInstanceOf[T], i))
      )),
      null, null, Seq.empty
    )
  }
}
