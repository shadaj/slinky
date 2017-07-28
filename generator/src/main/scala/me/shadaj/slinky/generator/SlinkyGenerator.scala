package me.shadaj.slinky.generator

object SlinkyGenerator {
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

    tags.foreach { t =>
      println(t)

      val (summary, attributes) = MDN.htmlElement(t)

      val tagVariableName = if (keywords.contains(t)) {
        "`" + t + "`"
      } else t

      tagsScala = tagsScala :+
        s"""/**
           | * $summary
           | */
           |def $tagVariableName(mods: HtmlComponentMod[${t}Tag.type]*): HtmlComponent[${t}Tag.type] = new HtmlComponent[${t}Tag.type]("$t").apply(mods: _*)""".stripMargin

      tagsAppliedScala = tagsAppliedScala :+
        s"""object ${t}Tag {""".stripMargin

      var attributeConversions = Set.empty[String]

      attributes.foreach { case (a, d) =>
        val attributeName = if (keywords.contains(a.name)) {
          "`" + a.name + "`"
        } else a.name

        val doc = (t, d.replace("\n", " ").replace("*", "&#47;"))

        val attributeInstance =
          s"""object $attributeName {
             |def :=(v: ${a.valueType}): AttrPair[$attributeName.type] = new AttrPair[$attributeName.type]("${a.name}", v)
             |${if (attributeName == "data") "def -(sub: String) = new { def :=(v: String): AttrPair[data.type] = new AttrPair[data.type](\"data-\" + sub, v) }" else ""}
             |}"""

        attributeInstances = attributeInstances :+ (doc, attributeInstance)

        attributeConversions = attributeConversions + s"""implicit def ${a.name}PairTo${t}Applied(pair: AttrPair[$attributeName.type]): AttrPair[${t}Tag.type] = pair.asInstanceOf[AttrPair[${t}Tag.type]]"""
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
      s"""package me.shadaj.slinky.core.html.internal
         |import me.shadaj.slinky.core.html.AttrPair
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait tagsApplied extends attrs {
         |${tagsAppliedScala.mkString("\n")}
         |}""".stripMargin,
      s"""package me.shadaj.slinky.core.html.internal
         |import me.shadaj.slinky.core.html.{HtmlComponent, HtmlComponentMod}
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait tags extends tagsApplied {
         |${tagsScala.mkString("\n")}
         |}""".stripMargin,
      s"""package me.shadaj.slinky.core.html.internal
         |import me.shadaj.slinky.core.html.AttrPair
         |import scala.language.implicitConversions
         |import scala.scalajs.js
         |trait attrs {
         |${attributeInstancesLines.mkString("\n")}
         |}""".stripMargin
    )
  }
}
