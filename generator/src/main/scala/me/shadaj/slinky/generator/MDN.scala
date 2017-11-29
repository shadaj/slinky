package me.shadaj.slinky.generator

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

object MDN extends TagsProvider {
  val browser = JsoupBrowser()

  val extraAttributes = List(
    Attr("key") -> "",
    Attr("ref", "scala.Function1[org.scalajs.dom.Element, Unit]") -> "",
    Attr("dangerouslySetInnerHTML", "js.Object") -> "",
    Attr("suppressContentEditableWarning", "Boolean") -> "",
    Attr("defaultChecked", "Boolean") -> "",
    Attr("aria") -> "",
    Attr("onAbort", "EventHandler") -> "",
    Attr("onAutoComplete", "EventHandler") -> "",
    Attr("onAutoCompleteError", "EventHandler") -> "",
    Attr("onBlur", "EventHandler") -> "",
    Attr("onCancel", "EventHandler") -> "",
    Attr("onCanPlay", "EventHandler") -> "",
    Attr("onCanPlayThrough", "EventHandler") -> "",
    Attr("onChange", "EventHandler") -> "",
    Attr("onClick", "EventHandler") -> "",
    Attr("onClose", "EventHandler") -> "",
    Attr("onContextMenu", "EventHandler") -> "",
    Attr("onCueChange", "EventHandler") -> "",
    Attr("onDoubleClick", "EventHandler") -> "",
    Attr("onDrag", "EventHandler") -> "",
    Attr("onDragEnd", "EventHandler") -> "",
    Attr("onDragEnter", "EventHandler") -> "",
    Attr("onDragExit", "EventHandler") -> "",
    Attr("onDragLeave", "EventHandler") -> "",
    Attr("onDragOver", "EventHandler") -> "",
    Attr("onDragStart", "EventHandler") -> "",
    Attr("onDrop", "EventHandler") -> "",
    Attr("onDurationChange", "EventHandler") -> "",
    Attr("onEmptied", "EventHandler") -> "",
    Attr("onEnded", "EventHandler") -> "",
    Attr("onError", "EventHandler") -> "",
    Attr("onFocus", "EventHandler") -> "",
    Attr("onInput", "EventHandler") -> "",
    Attr("onInvalid", "EventHandler") -> "",
    Attr("onKeyDown", "EventHandler") -> "",
    Attr("onKeyPress", "EventHandler") -> "",
    Attr("onKeyUp", "EventHandler") -> "",
    Attr("onLoad", "EventHandler") -> "",
    Attr("onLoadedData", "EventHandler") -> "",
    Attr("onLoadedMetadata", "EventHandler") -> "",
    Attr("onLoadStart", "EventHandler") -> "",
    Attr("onMouseDown", "MouseEventHandler") -> "",
    Attr("onMouseEnter", "MouseEventHandler") -> "",
    Attr("onMouseLeave", "MouseEventHandler") -> "",
    Attr("onMouseMove", "MouseEventHandler") -> "",
    Attr("onMouseOut", "MouseEventHandler") -> "",
    Attr("onMouseOver", "MouseEventHandler") -> "",
    Attr("onMouseUp", "MouseEventHandler") -> "",
    Attr("onMouseWheel", "MouseEventHandler") -> "",
    Attr("onPause", "EventHandler") -> "",
    Attr("onPlay", "EventHandler") -> "",
    Attr("onPlaying", "EventHandler") -> "",
    Attr("onProgress", "EventHandler") -> "",
    Attr("onRateChange", "EventHandler") -> "",
    Attr("onReset", "EventHandler") -> "",
    Attr("onResize", "EventHandler") -> "",
    Attr("onScroll", "EventHandler") -> "",
    Attr("onSeeked", "EventHandler") -> "",
    Attr("onSeeking", "EventHandler") -> "",
    Attr("onSelect", "EventHandler") -> "",
    Attr("onShow", "EventHandler") -> "",
    Attr("onSort", "EventHandler") -> "",
    Attr("onStalled", "EventHandler") -> "",
    Attr("onSubmit", "EventHandler") -> "",
    Attr("onSuspend", "EventHandler") -> "",
    Attr("onTimeUpdate", "EventHandler") -> "",
    Attr("onToggle", "EventHandler") -> "",
    Attr("onVolumeChange", "EventHandler") -> "",
    Attr("onWaiting", "EventHandler") -> ""
  )

  val supportedAttributes: Set[String] =
    """accept acceptCharset accessKey action allowFullScreen allowTransparency alt
      |async autoComplete autoFocus autoPlay capture cellPadding cellSpacing challenge
      |charSet checked cite classID class colSpan cols content contentEditable
      |contextMenu controls coords crossOrigin data data-* dateTime default defer dir
      |disabled download draggable encType for form formAction formEncType formMethod
      |formNoValidate formTarget frameBorder headers height hidden high href hrefLang
      |html httpEquiv icon id inputMode integrity is keyParams keyType kind label
      |lang list loop low manifest marginHeight marginWidth max maxLength media
      |mediaGroup method min minLength multiple muted name noValidate nonce open
      |optimum pattern placeholder poster preload profile radioGroup readOnly rel
      |required reversed role rowSpan rows sandbox scope scoped scrolling seamless
      |selected shape size sizes span spellCheck src srcDoc srcLang srcSet start step
      |style summary tabIndex target title type useMap value width wmode wrap"""
      .stripMargin.split('\n').flatMap(_.split(' ')).toSet

  lazy val globalAttributes: List[(Attr, String)] = {
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
      if (extraAttributes.exists(_._1.name.toLowerCase == attrString) || !supportedAttributes.contains(attrString)) {
        None
      } else {
        Some(HTMLToJSMapping.convert(attrString) -> docs.innerHtml)
      }
    }.toList
  }

  def htmlElement(name: String): (String, List[(Attr, String)]) = {
    if (Set("h1", "h2", "h3", "h4", "h5", "h6").contains(name)) {
      ("A header element", globalAttributes)
    } else {
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
            acc.init :+ acc.last.copy(_2 = if (acc.last._2.isEmpty) cur.innerHtml else acc.last._2 + " " + cur.innerHtml)
          }
        }

        attrsAndDocs.flatMap { case (attr, doc) =>
          if (extraAttributes.exists(_._1.name.toLowerCase == attr) || !supportedAttributes.contains(attr)) {
            None
          } else {
            Some(HTMLToJSMapping.convert(attr) -> doc.filterNot(_ == '\n'))
          }
        }.toList
      } ++ globalAttributes

      (summary, attributes)
    }
  }

  val tags: Seq[String] =
    """a abbr address area article aside audio b base bdi bdo big blockquote body br
      |button canvas caption cite code col colgroup data datalist dd del details dfn
      |dialog div dl dt em embed fieldset figcaption figure footer form h1 h2 h3 h4 h5
      |h6 head header hr html i iframe img input ins kbd keygen label legend li link
      |main map mark menu menuitem meta meter nav noscript object ol optgroup option
      |output p param picture pre progress q rp rt ruby s samp script section select
      |small source span strong style sub summary sup table tbody td textarea tfoot th
      |thead time title tr track u ul var video wbr"""
    .stripMargin.split('\n').flatMap(_.split(' ')).toSeq

  def extract: (Seq[Tag], Seq[Attribute]) = {
    val tagsWithAttributes = tags.map { n =>
      val extracted = htmlElement(n)
      (Tag(n, "", extracted._1.split('\n')), extracted._2)
    }

    val attrs = tagsWithAttributes.flatMap(v => v._2.map(t => (v._1, t._1, t._2)))
      .groupBy(_._2).map { case (attr, instances) =>
      val groupedDocs = instances.groupBy(_._3)

      Attribute(
        attr.name,
        attr.valueType,
        if (groupedDocs.size == 1) {
          List(groupedDocs.head._1)
        } else {
          groupedDocs.toList.map { case (doc, tagsForDoc) =>
            s"${tagsForDoc.map(_._1.tagName).mkString(", ")} - $doc"
          }
        },
        if (instances.map(_._1.tagName).distinct.size == tags.size) None else Some(instances.map(_._1.tagName)),
        attr.name == "data" || attr.name == "aria"
      )
    }.toSeq

    (tagsWithAttributes.map(_._1), attrs)
  }
}
