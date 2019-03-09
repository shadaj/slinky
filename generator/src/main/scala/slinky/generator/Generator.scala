package slinky.generator

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
      tags = extractedWithoutStar.tags :+ Tag("*", "Any", Seq.empty),
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
           |
           |@inline def apply(mods: TagMod[tag.type]*) = {
           |  new WithAttrs("${t.tagName}", js.Dictionary.empty, js.Array()).apply(mods: _*)
           |}"""
      }

      val attrsGen = attrs.toList.flatMap { a =>
        val compatibles = a.compatibleTags.map(ts => ts.map(n => extracted.tags.find(_.tagName == n).get)).getOrElse(extracted.tags)
        val base = (if (a.attributeType == "EventHandler") {
          val noEvent = s"""@inline def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)""".stripMargin
          compatibles.map { t =>
            s"""@inline def :=(v: slinky.core.SyntheticEvent[${t.scalaJSType}, org.scalajs.dom.Event] => Unit)(implicit _imp: ${Utils.identifierFor(t.tagName)}.tag.type) =
               |  new AttrPair[${Utils.identifierFor(t.tagName)}.tag.type]("${a.attributeName}", v)""".stripMargin
          }.mkString("", "\n", "\n") + noEvent
        } else if (a.attributeType == "MouseEventHandler") {
          s"""@inline def :=(v: org.scalajs.dom.MouseEvent => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |@inline def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else if (a.attributeType == "TouchEventHandler") {
          s"""@inline def :=(v: org.scalajs.dom.TouchEvent => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |@inline def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else if (a.attributeType == "RefType") {
          s"""@inline def :=(v: org.scalajs.dom.Element => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |@inline def :=(v: slinky.core.facade.ReactRef[org.scalajs.dom.Element]) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
           """.stripMargin
        } else {
          s"""@inline def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)"""
        }) + s"\ntype attrType = _${symbolWithoutEscape}_attr.type"

        if (a.withDash) {
          Seq(
            base,
            s"""final class WithDash(val sub: String) extends AnyVal { @inline def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}-" + sub, v) }
               |@inline def -(sub: String) = new WithDash(sub)""".stripMargin
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
      } else Seq.empty) ++ (if (tags.nonEmpty) Seq("Tag") else Seq.empty) ++ (if (attrs.isDefined) Seq("Attr") else Seq.empty)

      val symbolExtends = if (symbolExtendsList.isEmpty) "" else symbolExtendsList.mkString("extends ", " with ", "")

      val out = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbol + ".scala"))

      out.println(
        s"""package $pkg
           |
           |import slinky.core.{AttrPair, TagElement, Tag, Attr, WithAttrs, TagMod}
           |import slinky.core.facade.{React, ReactElement}
           |import scala.scalajs.js
           |import scala.language.implicitConversions
           |
           |/**
           | * ${(tags.map(_.docLines) ++ attrs.map(_.docLines)).map(_.map(_.replace("*", "&#47;")).mkString("\n * "))}
           | */
           |object $symbol $symbolExtends {
           |implicit object tag extends TagElement
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
