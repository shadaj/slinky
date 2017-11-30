package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source

object Generator extends App {
  val providerName :: out :: pkg :: Nil = args.toList

  val outFolder = new File(out)
  if (!outFolder.exists()) {
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
           |import scala.scalajs.js
           |import scala.language.implicitConversions
           |
           |/**
           | * ${attrs.map(_.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")).getOrElse("")}
           | */
           |object $symbol $symbolExtends {
           |object tag extends TagElement
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
