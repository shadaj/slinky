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

    // we add a * tag which is supported by all attributes
    val extractedWithoutStar = decode[TagsModel](Source.fromFile(providerName).getLines().mkString("\n")).right.get
    val extracted = extractedWithoutStar.copy(
      tags = extractedWithoutStar.tags :+ Tag("*", Seq.empty),
      attributes = extractedWithoutStar.attributes.map(a =>
        a.copy(compatibleTags = a.compatibleTags.map(_ :+ "*")))
    )

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
      val symbolWithoutEscapeFixed = if (symbolWithoutEscape == "*") "star" else symbolWithoutEscape

      val tagsGen = tags.map { t =>
        s"""type tagType = tag.type
           |/**
           | * ${t.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")}
           | */
           |@inline def apply(mod: AttrPair[tag.type], remainingMods: AttrPair[tag.type]*) = new WithAttrs("${t.tagName}", js.Dictionary((mod +: remainingMods).map(m => m.name -> m.value): _*))
           |/**
           | * ${t.docLines.map(_.replace("*", "&#47;")).mkString("\n * ")}
           | */
           |@inline def apply(elems: ReactElement*) = React.createElement("${t.tagName}", js.Dictionary.empty[js.Any], elems: _*)"""
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
          val fixedT = if (t == "*") "star" else t
          s"""implicit def to${fixedT}Applied(pair: AttrPair[_${symbolWithoutEscape}_attr.type]) = pair.asInstanceOf[AttrPair[${Utils.identifierFor(t)}.tag.type]]"""
        }
      }

      val symbolExtendsList = (if (attrs.isDefined && attrs.get.attributeType == "Boolean") {
        Seq(s"""AttrPair[_${symbolWithoutEscape}_attr.type]("${attrs.get.attributeName}", true)""")
      } else Seq.empty) ++ (if (tags.nonEmpty) Seq("Tag") else Seq.empty)

      val symbolExtends = if (symbolExtendsList.isEmpty) "" else symbolExtendsList.mkString("extends ", " with ", "")

      val out = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbol + ".scala"))

      out.println(
        s"""package $pkg
           |
           |import me.shadaj.slinky.core.{AttrPair, TagElement, Tag, WithAttrs}
           |import me.shadaj.slinky.core.facade.{React, ReactElement}
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
           |object _${symbolWithoutEscapeFixed}_attr {
           |${attrToTagImplicits.mkString("\n")}
           |}""".stripMargin
      )

      out.close()
    }
  }
}
