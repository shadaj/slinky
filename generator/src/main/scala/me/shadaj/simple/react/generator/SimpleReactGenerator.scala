package me.shadaj.simple.react.generator

object SimpleReactGenerator {
  val tags = """a
               |abbr
               |address
               |area
               |article
               |aside
               |audio
               |b
               |base
               |bdi
               |bdo
               |blockquote
               |body
               |br
               |button
               |canvas
               |caption
               |cite
               |code
               |col
               |colgroup
               |command
               |data
               |datalist
               |dd
               |del
               |details
               |dfn
               |dialog
               |div
               |dl
               |dt
               |em
               |embed
               |fieldset
               |figcaption
               |figure
               |footer
               |form
               |h1
               |h2
               |h3
               |h4
               |h5
               |h6
               |head
               |header
               |hgroup
               |hr
               |html
               |i
               |iframe
               |img
               |input
               |ins
               |kbd
               |keygen
               |label
               |legend
               |li
               |link
               |map
               |mark
               |menu
               |meta
               |meter
               |nav
               |noscript
               |object
               |ol
               |optgroup
               |option
               |output
               |p
               |param
               |pre
               |progress
               |q
               |rp
               |rt
               |ruby
               |s
               |samp
               |script
               |section
               |select
               |small
               |source
               |span
               |strong
               |style
               |sub
               |summary
               |sup
               |table
               |tbody
               |td
               |textarea
               |tfoot
               |th
               |thead
               |time
               |title
               |tr
               |track
               |u
               |ul
               |var
               |video
               |wbr""".stripMargin.split('\n')

  def generateGen = {
    var tagsScala = List.empty[String]
    var tagsAppliedScala = List.empty[String]

    var attributeInstances = List.empty[((String, String), String)]

//    var attributeAppliedConversions = Set.empty[String]

    val keywords = Set("var", "for", "object", "val", "type")

    // conflict with tags, outputed with _tag suffix
    val hiddenTags = Set("data", "style", "title", "cite", "link", "form", "span", "label", "summary", "abbr")

    tags.foreach { t =>
      println(t)

      val (summary, attributes) = MDN.htmlElement(t)

      val tagVariableName = if (hiddenTags.contains(t)) t + "_tag" else if (keywords.contains(t)) {
        "`" + t + "`"
      } else t

      tagsScala = tagsScala :+
        s"""/**
           | * $summary
           | */
           |val $tagVariableName = new HtmlComponent[${t}AttributeApplied]("$t")""".stripMargin

      tagsAppliedScala = tagsAppliedScala :+
        s"""case class ${t}AttributeApplied(name: String, value: js.Any) extends AppliedAttribute
           |object ${t}AttributeApplied {""".stripMargin

      var attributeConversions = Set.empty[String]

      attributes.foreach { case (a, d) =>
        val attributeName = if (keywords.contains(a.name)) {
          "`" + a.name + "`"
        } else a.name

        val doc = (t, d.replace("\n", " ").replace("*", "&#47;"))

        val attributeInstance =
          s"""object $attributeName extends Attr[${a.valueType}, ${a.name}Pair]("${a.name}") {
             |def :=(v: ${a.valueType}): ${a.name}Pair = new ${a.name}Pair(name, v)
             |}"""
        val attributePairInstance = s"""class ${a.name}Pair(attr: String, value: ${a.valueType}) extends AttrPair[${a.valueType}](attr, value)"""

        attributeInstances = attributeInstances :+ (doc, attributeInstance + "\n" + attributePairInstance)

        if (attributeName == "data") {
          val dataSpecial =
            s"""def data(sub: String) = new Attr[${a.valueType}, ${a.name}Pair]("data-" + sub) {
               |def :=(v: String): ${a.name}Pair = new ${a.name}Pair(name, v)
               |}"""
          attributeInstances = attributeInstances :+ (doc, dataSpecial)
        }

        attributeConversions = attributeConversions + s"""implicit def ${a.name}PairTo${t}Applied(pair: ${a.name}Pair): ${t}AttributeApplied = ${t}AttributeApplied(pair.name, pair.value)"""
      }

      tagsAppliedScala = tagsAppliedScala :+ attributeConversions.mkString("\n")

      tagsAppliedScala = tagsAppliedScala :+ "}"
    }

    val attributeInstancesLines = attributeInstances.groupBy(_._2).map { case (instance, types) =>
      val docs = types.map(_._1).groupBy(_._2)
      val docsText = if (docs.size == 1) {
        docs.map(t => "* " + t._1).mkString("\n* <h2></h2>\n")
      } else {
        docs.map(t => "* " + t._2.map(_._1).mkString(", ") + " - " + t._1).mkString("\n* <h2></h2>\n")
      }

      s"""/**
         | $docsText
         | */
         |$instance"""
    }

    (
      s"""package me.shadaj.simple.react.core.html
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait tagsApplied {
         |${tagsAppliedScala.mkString("\n")}
         |}""".stripMargin,
      s"""package me.shadaj.simple.react.core.html
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait tags {
         |${tagsScala.mkString("\n")}
         |}""".stripMargin,
      s"""package me.shadaj.simple.react.core.html
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait attrs {
         |${attributeInstancesLines.mkString("\n")}
         |}""".stripMargin
    )
  }
}
