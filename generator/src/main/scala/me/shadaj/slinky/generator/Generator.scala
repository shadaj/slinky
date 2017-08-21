package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

object Generator extends App {
  val providerName :: out :: pkg :: Nil = args.toList
  val provider = Class.forName(providerName).newInstance().asInstanceOf[TagsProvider]

  val outFolder = new File(out)
  if (!outFolder.exists()) {
    outFolder.mkdirs()

    val extracted = provider.extract

    val allSymbols = extracted._2.foldLeft(extracted._1.map(t => t.identifier -> (Some(t): Option[Tag], None: Option[Attribute])).toSet) { case (symbols, attr) =>
      symbols.find(_._1 == attr.identifier) match {
        case Some(o@(_, (tags, None))) =>
          symbols - o + ((attr.identifier, (tags, Some(attr))))
        case None =>
          symbols + ((attr.identifier, (None, Some(attr))))
      }
    }

    allSymbols.foreach { case (symbol, (tags, attrs)) =>
      val symbolWithoutEscape = if (symbol.startsWith("`")) symbol.tail.init else symbol

      val tagsGen = tags.map { t =>
        s"""/**
           | * ${t.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")}
           | */
           |def apply(mods: TagMod[tag.type]*) = new TagComponent[tag.type]("${t.tagName}").apply(mods: _*)""".stripMargin
      }

      val attrsGen = attrs.toList.flatMap { a =>
        val base = if (a.attributeType == "EventHandler") {
          s"""def :=(v: org.scalajs.dom.Event => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else if (a.attributeType == "MouseEventHandler") {
          s"""def :=(v: org.scalajs.dom.MouseEvent => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else {
          s"""def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)"""
        }

        if (a.withDash) {
          Seq(
            base,
            s"""class WithDash(val sub: String) { def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}-" + sub, v) }
               |def -(sub: String) = new WithDash(sub)""".stripMargin
          )
        } else Seq(base)
      }

      val attrToTagImplicits = attrs.toList.flatMap { a =>
        a.compatibleTags.map { t =>
          s"""implicit def to${t._1.tagName}Applied(pair: AttrPair[_${symbolWithoutEscape}_attr.type]) = pair.asInstanceOf[AttrPair[${t._1.identifier}.tag.type]]"""
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

      val symbolExtends = if (attrs.isDefined && attrs.get.attributeType == "Boolean") {
        s"""extends AttrPair[_${symbolWithoutEscape}_attr.type]("${attrs.get.attributeName}", true)"""
      } else ""

      val out = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbol + ".scala"))

      out.println(
        s"""package ${pkg}
           |
           |import me.shadaj.slinky.core.{AttrPair, TagComponent, TagMod}
           |import scala.scalajs.js
           |import scala.language.implicitConversions
           |
           |/**
           | * $attrDocs
           | */
           |object $symbol $symbolExtends {
           |object tag
           |${tagsGen.mkString("\n")}
           |${attrsGen.mkString("\n")}
           |}
           |
           |object _${symbolWithoutEscape}_attr {
           |${attrToTagImplicits.mkString("\n")}
           |}""".stripMargin
      )

      out.close()
    }
  }
}
