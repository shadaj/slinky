package slinky.generator

case class Attr(name: String, valueType: String = "String")

object HTMLToJSMapping {
  val convert = Map(
    "accept" -> Attr("accept"),
    "acceptcharset" -> Attr("acceptCharset"),
    "accesskey" -> Attr("accessKey"),
    "action" -> Attr("action"),
    "allowfullscreen" -> Attr("allowFullScreen", "Boolean"),
    "allowtransparency" -> Attr("allowTransparency"),
    "alt" -> Attr("alt"),
    "async" -> Attr("async", "Boolean"),
    "autocomplete" -> Attr("autoComplete"),
    "autoplay" -> Attr("autoPlay", "Boolean"),
    "capture" -> Attr("capture", "Boolean"),
    "cellpadding" -> Attr("cellPadding"),
    "cellspacing" -> Attr("cellSpacing"),
    "charset" -> Attr("charSet"),
    "challenge" -> Attr("challenge"),
    "checked" -> Attr("checked", "Boolean"),
    "cite" -> Attr("cite"),
    "classid" -> Attr("classID"),
    "class" -> Attr("className"),
    "cols" -> Attr("cols"),
    "colspan" -> Attr("colSpan"),
    "content" -> Attr("content"),
    "contenteditable" -> Attr("contentEditable"),
    "contextmenu" -> Attr("contextMenu"),
    "controls" -> Attr("controls", "Boolean"),
    "coords" -> Attr("coords"),
    "crossorigin" -> Attr("crossOrigin"),
    "data" -> Attr("data"),
    "data-*" -> Attr("data"),
    "datetime" -> Attr("dateTime"),
    "default" -> Attr("default", "Boolean"),
    "defer" -> Attr("defer", "Boolean"),
    "dir" -> Attr("dir"),
    "disabled" -> Attr("disabled", "Boolean"),
    "download" -> Attr("download", "Boolean"),
    "draggable" -> Attr("draggable"),
    "enctype" -> Attr("encType"),
    "for" -> Attr("htmlFor"),
    "form" -> Attr("form"),
    "formaction" -> Attr("formAction"),
    "formenctype" -> Attr("formEncType"),
    "formmethod" -> Attr("formMethod"),
    "formnovalidate" -> Attr("formNoValidate", "Boolean"),
    "formtarget" -> Attr("formTarget"),
    "frameborder" -> Attr("frameBorder"),
    "headers" -> Attr("headers"),
    "height" -> Attr("height"),
    "hidden" -> Attr("hidden", "Boolean"),
    "high" -> Attr("high"),
    "href" -> Attr("href"),
    "hreflang" -> Attr("hrefLang"),
    "httpequiv" -> Attr("httpEquiv"),
    "icon" -> Attr("icon"),
    "id" -> Attr("id"),
    "inputmode" -> Attr("inputMode"),
    "integrity" -> Attr("integrity"),
    "is" -> Attr("is"),
    "keyparams" -> Attr("keyParams"),
    "keytype" -> Attr("keyType"),
    "kind" -> Attr("kind"),
    "label" -> Attr("label"),
    "lang" -> Attr("lang"),
    "list" -> Attr("list"),
    "loop" -> Attr("loop", "Boolean"),
    "low" -> Attr("low"),
    "manifest" -> Attr("manifest"),
    "marginheight" -> Attr("marginHeight"),
    "marginwidth" -> Attr("marginWidth"),
    "max" -> Attr("max"),
    "maxlength" -> Attr("maxLength"),
    "media" -> Attr("media"),
    "mediagroup" -> Attr("mediaGroup"),
    "method" -> Attr("method"),
    "min" -> Attr("min"),
    "minlength" -> Attr("minLength"),
    "multiple" -> Attr("multiple", "Boolean"),
    "muted" -> Attr("muted", "Boolean"),
    "name" -> Attr("name"),
    "nonce" -> Attr("nonce"),
    "novalidate" -> Attr("noValidate", "Boolean"),
    "open" -> Attr("open", "Boolean"),
    "optimum" -> Attr("optimum"),
    "pattern" -> Attr("pattern"),
    "placeholder" -> Attr("placeholder"),
    "poster" -> Attr("poster"),
    "preload" -> Attr("preload"),
    "profile" -> Attr("profile"),
    "radiogroup" -> Attr("radioGroup"),
    "readonly" -> Attr("readOnly", "Boolean"),
    "referrerpolicy" -> Attr("referrerPolicy"),
    "rel" -> Attr("rel"),
    "required" -> Attr("required", "Boolean"),
    "reversed" -> Attr("reversed", "Boolean"),
    "role" -> Attr("role"),
    "rows" -> Attr("rows"),
    "rowspan" -> Attr("rowSpan"),
    "sandbox" -> Attr("sandbox"),
    "scope" -> Attr("scope"),
    "scoped" -> Attr("scoped", "Boolean"),
    "scrolling" -> Attr("scrolling"),
    "seamless" -> Attr("seamless", "Boolean"),
    "selected" -> Attr("selected", "Boolean"),
    "shape" -> Attr("shape"),
    "size" -> Attr("size"),
    "sizes" -> Attr("sizes"),
    "span" -> Attr("span"),
    "spellcheck" -> Attr("spellCheck"),
    "src" -> Attr("src"),
    "srcdoc" -> Attr("srcDoc"),
    "srclang" -> Attr("srcLang"),
    "srcset" -> Attr("srcSet"),
    "start" -> Attr("start"),
    "step" -> Attr("step"),
    "style" -> Attr("style", "js.Dynamic"),
    "summary" -> Attr("summary"),
    "tabindex" -> Attr("tabIndex"),
    "target" -> Attr("target"),
    "title" -> Attr("title"),
    "type" -> Attr("type"),
    "usemap" -> Attr("useMap"),
    "value" -> Attr("value"),
    "width" -> Attr("width"),
    "wmode" -> Attr("wmode"),
    "wrap" -> Attr("wrap"),
    "about" -> Attr("about"),
    "datatype" -> Attr("datatype"),
    "inlist" -> Attr("inlist"),
    "prefix" -> Attr("prefix"),
    "property" -> Attr("property"),
    "resource" -> Attr("resource"),
    "typeof" -> Attr("typeof"),
    "vocab" -> Attr("vocab"),
    "autocapitalize" -> Attr("autoCapitalize"),
    "autocorrect" -> Attr("autoCorrect"),
    "autosave" -> Attr("autoSave"),
    "color" -> Attr("color"),
    "itemprop" -> Attr("itemProp"),
    "itemscope" -> Attr("itemScope", "Boolean"),
    "itemtype" -> Attr("itemType"),
    "itemid" -> Attr("itemID"),
    "itemref" -> Attr("itemRef"),
    "results" -> Attr("results"),
    "security" -> Attr("security"),
    "unselectable" -> Attr("unselectable"),
    "dropzone" -> Attr("dropzone"),
    "translate" -> Attr("translate"),
    "ping" -> Attr("ping")
  ).withDefault { k =>
    println(s""""$k" -> Attr("${dashToCamelCase(k)}", "js.Any"),""")
    Attr(dashToCamelCase(k), "js.Any")
  }

  def dashToCamelCase(dashed: String) = {
    val splitName = dashed.split('-')
    (splitName.head +: splitName.tail.map(s => s.head.toUpper + s.tail)).mkString
  }
}
