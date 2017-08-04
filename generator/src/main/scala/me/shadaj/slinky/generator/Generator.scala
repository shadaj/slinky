package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

object Generator extends App {
  val outFile = new File(args.head)
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
           |def apply(mods: TagMod[$symbol.tag.type]*): TagComponent[$symbol.tag.type] = new TagComponent[$symbol.tag.type]("${t.tagName}").apply(mods: _*)""".stripMargin
      }

      val attrsGen = attrs.toList.flatMap { a =>
        val base = if (a.attributeType == "EventHandler") {
          s"""def :=(v: org.scalajs.dom.Event => Unit): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}", v)
             |def :=(v: () => Unit): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else {
          s"""def :=(v: ${a.attributeType}): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}", v)"""
        }

        if (a.withDash) {
          Seq(
            base,
            s"""class WithDash(val sub: String) { def :=(v: ${a.attributeType}): AttrPair[$symbol.attr.type] = new AttrPair[$symbol.attr.type]("${a.attributeName}-" + sub, v) }
               |def -(sub: String) = new WithDash(sub)""".stripMargin
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
      s"""package ${args(1)}
         |import me.shadaj.slinky.core.{AttrPair, TagComponent, TagMod}
         |import scala.scalajs.js
         |import scala.language.implicitConversions
         |private[${args(1).split('.').last}] trait gen {
         |${gen.mkString("\n")}
         |}""".stripMargin)
    out.close()
  }
}
