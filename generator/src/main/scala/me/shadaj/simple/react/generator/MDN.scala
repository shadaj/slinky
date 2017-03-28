package me.shadaj.simple.react.generator

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

object MDN {
  val browser = JsoupBrowser()

  val extraAttributes = List(
    HTMLToJSMapping.Attr("key", "String") -> "",
    HTMLToJSMapping.Attr("onAbort", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onAutoComplete", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onAutoCompleteError", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onBlur", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onCancel", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onCanPlay", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onCanPlayThrough", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onChange", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onClick", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onClose", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onContextMenu", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onCueChange", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDblClick", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDrag", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragEnd", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragEnter", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragExit", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragLeave", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragOver", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDragStart", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDrop", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onDurationChange", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onEmptied", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onEnded", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onError", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onFocus", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onInput", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onInvalid", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onKeyDown", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onKeyPress", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onKeyUp", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onLoad", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onLoadedData", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onLoadedMetadata", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onLoadStart", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseDown", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseEnter", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseLeave", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseMove", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseOut", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseOver", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseUp", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onMouseWheel", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onPause", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onPlay", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onPlaying", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onProgress", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onRateChange", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onReset", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onResize", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onScroll", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSeeked", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSeeking", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSelect", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onShow", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSort", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onStalled", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSubmit", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onSuspend", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onTimeUpdate", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onToggle", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onVolumeChange", "js.Function1[org.scalajs.dom.Event, Unit]") -> "",
    HTMLToJSMapping.Attr("onWaiting", "js.Function1[org.scalajs.dom.Event, Unit]") -> ""
  )

  lazy val globalAttributes = {
    val page = browser.get(s"https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes")
    val article = page >> element("#wikiArticle")
    val attributesSection = article.children.toList
      .dropWhile(!_.attrs.get("id").contains("Description"))
      .takeWhile(e => e.attrs.get("id").isEmpty || e.attr("id") == "Description")
      .filter(_.tagName == "dl")
      .flatMap(_.children)

    extraAttributes ::: attributesSection.grouped(2).flatMap { i =>
      val attr = i.head
      val docs = i.last

      val attrString = attr.head >> text("code")
      if (extraAttributes.exists(_._1.name.toLowerCase == attrString)) {
        None
      } else {
        Some(HTMLToJSMapping.convert(attrString) -> docs.innerHtml)
      }
    }.toList
  }

  def htmlElement(name: String) = {
    val page = browser.get(s"https://developer.mozilla.org/en-US/docs/Web/HTML/Element/$name")
    val article = page >> element("#wikiArticle")
    val summary = article.children.find(c => c.tagName == "p" && c.innerHtml.nonEmpty).get.innerHtml

    val attributesSection = article.children.toList
      .dropWhile(!_.attrs.get("id").contains("Attributes")).tail
      .takeWhile(e => e.tagName != "h2")
      .filter(_.tagName == "dl")

    val attributes = attributesSection.flatMap { dl =>
      val children = dl.children.toList
      val attrsAndDocs = children.foldLeft(Seq.empty[(String, String)]) { (acc, cur) =>
        if (cur.tagName == "dt") {
          acc :+ (cur >> text("code"), "")
        } else {
          acc.init :+ acc.last.copy(_2 = acc.last._2 + "\n" + cur.innerHtml)
        }
      }

      attrsAndDocs.flatMap { case (attr, doc) =>
        if (extraAttributes.exists(_._1.name.toLowerCase == attr)) {
          None
        } else {
          Some(HTMLToJSMapping.convert(attr) -> doc)
        }
      }.toList
    } ++ globalAttributes

    (summary, attributes)
  }
}
