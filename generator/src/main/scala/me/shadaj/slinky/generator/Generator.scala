package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source

object Generator extends App {
  val providerName :: out :: pkg :: Nil = args.toList

  val outFolder = new File(out)
  outFolder.mkdirs()

  val extracted = decode[TagsModel](Source.fromFile(providerName).getLines().mkString("\n")).right.get

  val allSymbols = extracted.attributes.foldLeft(extracted.tags.map(t => Utils.identifierFor(t.tagName) -> (Some(t): Option[Tag], None: Option[Attribute])).toSet) { case (symbols, attr) =>
    symbols.find(_._1 == Utils.identifierFor(attr.attributeName)) match {
      case Some(o@(_, (tags, None))) =>
        symbols - o + ((Utils.identifierFor(attr.attributeName), (tags, Some(attr))))
      case None =>
        symbols + ((Utils.identifierFor(attr.attributeName), (None, Some(attr))))
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
      val base = if (a.event.isDefined) {
        val targetTags = a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).map { t =>
          extracted.tags.find(_.tagName == t).get
        }

        val tagSpecific = targetTags.map { t =>
          s"""def :=(v: ${a.event.get}[${t.scalajsDomType}] => Unit)(implicit _imp: ${Utils.identifierFor(t.tagName)}.tagType.type): TagMod[${Utils.identifierFor(t.tagName)}.tag.type] = new AttrPair[${Utils.identifierFor(t.tagName)}.tag.type]("${a.attributeName}", v)"""
        }.mkString("\n")

        s"""$tagSpecific
           |def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)""".stripMargin
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
      a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).map { t =>
        s"""implicit def to${t}Applied(pair: AttrPair[_${symbolWithoutEscape}_attr.type]) = pair.asInstanceOf[AttrPair[${Utils.identifierFor(t)}.tag.type]]"""
      }
    }

    val symbolExtends = if (attrs.isDefined && attrs.get.attributeType == "Boolean") {
      s"""extends AttrPair[_${symbolWithoutEscape}_attr.type]("${attrs.get.attributeName}", true)"""
    } else ""

    val out = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbol + ".scala"))

    out.println(
      s"""package ${pkg}
         |
         |import me.shadaj.slinky.core.{AttrPair, TagComponent, TagMod, TagElement}
         |import me.shadaj.slinky.core.facade.SyntheticEvent
         |import scala.scalajs.js
         |import scala.language.implicitConversions
         |
         |/**
         | * ${attrs.map(_.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")).getOrElse("")}
         | */
         |object $symbol $symbolExtends {
         |object tag extends TagElement
         |implicit object tagType
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
