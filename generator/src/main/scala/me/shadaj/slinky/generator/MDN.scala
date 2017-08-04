package me.shadaj.slinky.generator

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

object MDN {
  val browser = JsoupBrowser()

  val extraAttributes = List(
    HTMLToJSMapping.Attr("key") -> "",
    HTMLToJSMapping.Attr("ref", "scala.Function1[org.scalajs.dom.Element, Unit]") -> "",
    HTMLToJSMapping.Attr("dangerouslySetInnerHTML", "js.Object") -> "",
    HTMLToJSMapping.Attr("suppressContentEditableWarning", "Boolean") -> "",
    HTMLToJSMapping.Attr("defaultChecked", "Boolean") -> "",
    HTMLToJSMapping.Attr("aria") -> "",
    HTMLToJSMapping.Attr("onAbort", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onAutoComplete", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onAutoCompleteError", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onBlur", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onCancel", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onCanPlay", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onCanPlayThrough", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onChange", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onClick", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onClose", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onContextMenu", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onCueChange", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDoubleClick", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDrag", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragEnd", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragEnter", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragExit", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragLeave", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragOver", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDragStart", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDrop", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onDurationChange", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onEmptied", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onEnded", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onError", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onFocus", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onInput", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onInvalid", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onKeyDown", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onKeyPress", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onKeyUp", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onLoad", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onLoadedData", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onLoadedMetadata", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onLoadStart", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseDown", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseEnter", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseLeave", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseMove", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseOut", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseOver", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseUp", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onMouseWheel", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onPause", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onPlay", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onPlaying", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onProgress", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onRateChange", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onReset", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onResize", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onScroll", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSeeked", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSeeking", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSelect", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onShow", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSort", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onStalled", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSubmit", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onSuspend", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onTimeUpdate", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onToggle", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onVolumeChange", "EventHandler") -> "",
    HTMLToJSMapping.Attr("onWaiting", "EventHandler") -> ""
  )

  val supportedAttributes: Set[String] =
    """accept acceptCharset accessKey action allowFullScreen allowTransparency alt
      |async autoComplete autoFocus autoPlay capture cellPadding cellSpacing challenge
      |charSet checked cite classID class colSpan cols content contentEditable
      |contextMenu controls coords crossOrigin data data-* dateTime default defer dir
      |disabled download draggable encType form formAction formEncType formMethod
      |formNoValidate formTarget frameBorder headers height hidden high href hrefLang
      |html httpEquiv icon id inputMode integrity is keyParams keyType kind label
      |lang list loop low manifest marginHeight marginWidth max maxLength media
      |mediaGroup method min minLength multiple muted name noValidate nonce open
      |optimum pattern placeholder poster preload profile radioGroup readOnly rel
      |required reversed role rowSpan rows sandbox scope scoped scrolling seamless
      |selected shape size sizes span spellCheck src srcDoc srcLang srcSet start step
      |style summary tabIndex target title type useMap value width wmode wrap"""
      .stripMargin.split('\n').flatMap(_.split(' ')).toSet

  lazy val globalAttributes: List[(HTMLToJSMapping.Attr, String)] = {
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

  def htmlElement(name: String): (String, List[(HTMLToJSMapping.Attr, String)]) = {
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
        if (extraAttributes.exists(_._1.name.toLowerCase == attr) || !supportedAttributes.contains(attr)) {
          None
        } else {
          Some(HTMLToJSMapping.convert(attr) -> doc)
        }
      }.toList
    } ++ globalAttributes

    (summary, attributes)
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
      (Tag(n, extracted._1.split('\n')), extracted._2)
    }

    val attrs = tagsWithAttributes.flatMap(v => v._2.map(t => (v._1, t._1, t._2)))
      .groupBy(_._2).map { case (attr, instances) =>
      Attribute(
        attr.name,
        attr.valueType,
        instances.map { case (tag, _, doc) =>
          tag -> doc
        }.groupBy(_._1).toSeq.map(_._2.head),
        attr.name == "data" || attr.name == "aria"
      )
    }.toSeq

    (tagsWithAttributes.map(_._1), attrs)
  }
}
