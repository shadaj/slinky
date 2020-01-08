package slinky.generator

import java.io.{File, PrintWriter}

import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source

object Generator extends App {
  val providerName :: out :: pkg :: Nil = args.toList

  val eventToSynthetic = Map[String, String => String](
    "EventHandler" -> (t => s"slinky.core.SyntheticEvent[$t, org.scalajs.dom.Event]"),
    "ClipboardEventHandler" -> (t => s"slinky.web.SyntheticClipboardEvent[$t]"),
    "CompositionEventHandler" -> (t => s"slinky.web.SyntheticCompositionEvent[$t]"),
    "KeyboardEventHandler" -> (t => s"slinky.web.SyntheticKeyboardEvent[$t]"),
    "FocusEventHandler" -> (t => s"slinky.web.SyntheticFocusEvent[$t]"),
    "MouseEventHandler" -> (t => s"slinky.web.SyntheticMouseEvent[$t]"),
    "PointerEventHandler" -> (t => s"slinky.web.SyntheticPointerEvent[$t]"),
    "TouchEventHandler" -> (t => s"slinky.web.SyntheticTouchEvent[$t]"),
    "UIEventHandler" -> (t => s"slinky.web.SyntheticUIEvent[$t]"),
    "WheelEventHandler" -> (t => s"slinky.web.SyntheticWheelEvent[$t]"),
    "AnimationEventHandler" -> (t => s"slinky.web.SyntheticAnimationEvent[$t]"),
    "TransitionEventHandler" -> (t => s"slinky.web.SyntheticTransitionEvent[$t]")
  )

  val outFolder = new File(out)
  if (!outFolder.exists()) {
    outFolder.mkdirs()

    // we add a * tag which is supported by all attributes
    val extractedWithoutStar = decode[TagsModel](
      Source.fromFile(providerName).getLines()
        .filterNot(l => l.trim.isEmpty || l.trim.startsWith("//")).mkString("\n")
      ).right.get
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
           |@inline def apply(mods: TagMod[tag.type]*): WithAttrs[tagType] = {
           |  WithAttrs("${t.tagName}", mods)
           |}"""
      }

      val attrsGen = attrs.toList.flatMap { a =>
        val compatibles = a.compatibleTags.map(ts => ts.map(n => extracted.tags.find(_.tagName == n).get)).getOrElse(extracted.tags)
        val noEvent = s"""@inline def :=(v: () => Unit) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
                         |@inline def :=(v: Option[() => Unit]) = new OptionalAttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)""".stripMargin
        val base = (if (eventToSynthetic.contains(a.attributeType)) {
          val eventTypeForTagType = eventToSynthetic(a.attributeType)
          s"""@inline def :=[T <: TagElement](v: ${eventTypeForTagType("T#RefType")} => Unit)(implicit supported: AttrPair[attrType] => AttrPair[T]) =
              |  new AttrPair[T]("${a.attributeName}", v)
              |$noEvent""".stripMargin
        } else if (a.attributeType == "RefType") {
          s"""@inline def :=[T <: TagElement](v: T#RefType => Unit)(implicit supported: AttrPair[attrType] => AttrPair[T]) =
             |  new AttrPair[T]("${a.attributeName}", v)
             |@inline def :=[T <: TagElement, E <: T#RefType](v: slinky.core.RefAttr[E])(implicit supported: AttrPair[attrType] => AttrPair[T]) =
             |  new AttrPair[T]("${a.attributeName}", v)""".stripMargin
        } else {
          s"""@inline def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)
             |@inline def :=(v: Option[${a.attributeType}]) = new OptionalAttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}", v)""".stripMargin
        }) + s"\ntype attrType = _${symbolWithoutEscape}_attr.type"

        if (a.withDash) {
          Seq(
            base,
            s"""final class WithDash(@inline private val sub: String) extends AnyVal {
               |@inline def :=(v: ${a.attributeType}) = new AttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}-" + sub, v)
               |@inline def :=(v: Option[${a.attributeType}]) = new OptionalAttrPair[_${symbolWithoutEscape}_attr.type]("${a.attributeName}-" + sub, v) }
               |@inline def -(sub: String) = new WithDash(sub)""".stripMargin
          )
        } else Seq(base)
      }

      val attrToTagImplicits = attrs.toList.flatMap { a =>
        a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).flatMap { t =>
          val fixedT = if (t == "*") "star" else t
          Seq(
            s"""@inline implicit def to${fixedT}Applied(pair: AttrPair[_${symbolWithoutEscape}_attr.type]) = pair.asInstanceOf[AttrPair[${Utils.identifierFor(t)}.tag.type]]
               |@inline implicit def to${fixedT}OptionalApplied(pair: OptionalAttrPair[_${symbolWithoutEscape}_attr.type]) = pair.asInstanceOf[OptionalAttrPair[${Utils.identifierFor(t)}.tag.type]]
             """.stripMargin
          )
        }
      }

      val booleanImplicits = attrs.toList.flatMap { a =>
        a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).flatMap { t =>
          val fixedT = if (t == "*") "star" else t
          if (attrs.isDefined && attrs.get.attributeType == "Boolean") {
            Seq(s"""@inline implicit def boolToPair${fixedT}Applied(attrObj: this.type) = new AttrPair[${Utils.identifierFor(t)}.tag.type]("${attrs.get.attributeName}", true)""")
          } else Seq.empty
        }
      }

      val symbolExtendsList = (if (tags.nonEmpty) Seq("Tag") else Seq.empty) ++ (if (attrs.isDefined) Seq("Attr") else Seq.empty)

      val symbolExtends = if (symbolExtendsList.isEmpty) "" else symbolExtendsList.mkString("extends ", " with ", "")

      // Character "*" is not allowed in file names in Windows filesystem
      val symbolFixed = if (symbol == "*") "star" else symbol
      val out = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbolFixed + ".scala"))

      out.println(
        s"""package $pkg
           |
           |import slinky.core.{AttrPair, OptionalAttrPair, TagElement, Tag, Attr, WithAttrs, TagMod}
           |import slinky.core.OptionalAttrPair._
           |import slinky.core.facade.{React, ReactElement}
           |import scala.scalajs.js
           |import scala.language.implicitConversions
           |
           |/**
           | * ${(tags.map(_.docLines) ++ attrs.map(_.docLines)).flatten.map(_.replace("*", "&#47;")).mkString("\n * ")}
           | */
           |object $symbol $symbolExtends {
           |implicit object tag extends TagElement {
           |  type RefType = ${tags.headOption.map(_.scalaJSType).getOrElse("Nothing")}
           |}
           |${tagsGen.mkString("\n")}
           |${attrsGen.mkString("\n")}
           |${booleanImplicits.mkString("\n")}
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
