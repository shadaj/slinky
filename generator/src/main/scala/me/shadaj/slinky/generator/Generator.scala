package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

object Generator extends App {
  new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html/internal").mkdirs()
  val outFile = new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html/internal/gen.scala")
  if (!outFile.exists()) {
    val extracted = MDN.extract

    val allSymbols = extracted._2.foldLeft(extracted._1.map(t => t.identifier -> (Some(t): Option[Tag], None: Option[Attribute])).toSet) { case (symbols, attr) =>
      symbols.find(_._1 == attr.identifier) match {
        case Some(o@(_, (tags, None))) =>
          symbols - o + ((attr.identifier, (tags, Some(attr))))
        case None =>
          symbols + ((attr.identifier, (None, Some(attr))))
      }
    }

    val gen = allSymbols.map { case (symbol, (tags, attrs)) =>
      val tagsGen = tags.map { t =>
        s"""/**
           | * ${t.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")}
           | */
           |def apply(mods: HtmlComponentMod[$symbol.tag.type]*): HtmlComponent[$symbol.tag.type] = new HtmlComponent[$symbol.tag.type]("${t.tagName}").apply(mods: _*)""".stripMargin
      }

      val attrsGen = attrs.toList.flatMap { a =>
        val base = s"""def :=(v: ${a.attributeType}): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}", v)"""

        if (a.withDash) {
          Seq(
            base,
            s"""def -(sub: String) = new { def :=(v: ${a.attributeType}): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}-" + sub, v) }"""
          )
        } else Seq(base)
      }

      val attrToTagImplicits = attrs.toList.flatMap { a =>
        a.compatibleTags.map { t =>
          s"""implicit def ${a.attributeName}PairTo${t._1.tagName}Applied(pair: AttrPair[$symbol.attr.type]): AttrPair[${t._1.identifier}.tag.type] = pair.asInstanceOf[AttrPair[${t._1.identifier}.tag.type]]"""
        }
      }

      val attrDocs = attrs.map { a =>
        val grouped = a.compatibleTags.groupBy(_._2).toList

        if (grouped.size == 1) {
          grouped.head._1
        } else {
          grouped.map { case (doc, tags) =>
            s"""${tags.map(_._1.tagName).mkString(", ")} - ${doc.replace("*", "&#47;")}"""
          }.mkString("\n * <h2></h2>\n * ")
        }
      }.getOrElse("")

      s"""/**
         | * $attrDocs
         | */
         |object $symbol {
         |object tag
         |object attr {
         |${attrToTagImplicits.mkString("\n")}
         |}
         |${tagsGen.mkString("\n")}
         |${attrsGen.mkString("\n")}
         |}""".stripMargin
    }

    val out = new PrintWriter(outFile, "UTF-8")
    out.println(
      s"""package me.shadaj.slinky.core.html.internal
         |import me.shadaj.slinky.core.html.{AttrPair, HtmlComponent, HtmlComponentMod}
         |import scala.scalajs.js
         |trait gen {
         |${gen.mkString("\n")}
         |}""".stripMargin)
    out.close()
  }
}
