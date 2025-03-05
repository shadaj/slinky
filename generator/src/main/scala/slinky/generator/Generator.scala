package slinky.generator

import java.io.{File, PrintWriter}

import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source

object Generator {

  def main(args: Array[String]): Unit = {
    val providerName :: out :: pkg :: Nil = args.toList

    val eventToSynthetic = Map[String, String => String](
      "EventHandler"            -> (t => s"slinky.core.SyntheticEvent[$t, org.scalajs.dom.Event]"),
      "InputEventHandler"       -> (t => s"slinky.web.SyntheticInputEvent[$t]"),
      "ClipboardEventHandler"   -> (t => s"slinky.web.SyntheticClipboardEvent[$t]"),
      "CompositionEventHandler" -> (t => s"slinky.web.SyntheticCompositionEvent[$t]"),
      "KeyboardEventHandler"    -> (t => s"slinky.web.SyntheticKeyboardEvent[$t]"),
      "FocusEventHandler"       -> (t => s"slinky.web.SyntheticFocusEvent[$t]"),
      "MouseEventHandler"       -> (t => s"slinky.web.SyntheticMouseEvent[$t]"),
      "PointerEventHandler"     -> (t => s"slinky.web.SyntheticPointerEvent[$t]"),
      "TouchEventHandler"       -> (t => s"slinky.web.SyntheticTouchEvent[$t]"),
      "UIEventHandler"          -> (t => s"slinky.web.SyntheticUIEvent[$t]"),
      "WheelEventHandler"       -> (t => s"slinky.web.SyntheticWheelEvent[$t]"),
      "AnimationEventHandler"   -> (t => s"slinky.web.SyntheticAnimationEvent[$t]"),
      "TransitionEventHandler"  -> (t => s"slinky.web.SyntheticTransitionEvent[$t]")
    )

    val outFolder = new File(out)
    if (!outFolder.exists()) {
      outFolder.mkdirs()

      // we add a * tag which is supported by all attributes
      val extractedWithoutStar = decode[TagsModel](
        Source
          .fromFile(providerName)
          .getLines()
          .filterNot(l => l.trim.isEmpty || l.trim.startsWith("//"))
          .mkString("\n")
      ).fold(throw _, identity)
      val extracted = extractedWithoutStar.copy(
        tags = extractedWithoutStar.tags :+ Tag("*", "Any", Seq.empty),
        attributes = extractedWithoutStar.attributes.map(a => a.copy(compatibleTags = a.compatibleTags.map(_ :+ "*")))
      )

      val allSymbols = extracted.attributes.foldLeft(
        extracted.tags
          .map(t => Utils.identifierFor(t.tagName) -> ((Some(t): Option[Tag], None: Option[Attribute])))
          .toSet
      ) { case (symbols, attr) =>
        symbols.find(_._1 == Utils.identifierFor(attr.attributeName)) match {
          case Some(o @ (_, (tags, None))) =>
            symbols - o + ((Utils.identifierFor(attr.attributeName), (tags, Some(attr))))
          case _ =>
            symbols + ((Utils.identifierFor(attr.attributeName), (None, Some(attr))))
        }
      }

      allSymbols.foreach { case (symbol, (tags, attrs)) =>
        val symbolWithoutEscape      = if (symbol.startsWith("`")) symbol.tail.init else symbol
        val symbolWithoutEscapeFixed = if (symbolWithoutEscape == "*") "star" else symbolWithoutEscape

        val tagsGen = tags.map { t =>
          s"""type tagType = tag.type
             |
             |@inline def apply(mods: slinky.core.TagMod[tag.type]*): slinky.core.WithAttrs[tagType] = {
             |  slinky.core.WithAttrs("${t.tagName}", mods)
             |}"""
        }

        def attrsGen(nameSuffix: String = "") = attrs.toList.flatMap { a =>
          val base = (if (eventToSynthetic.contains(a.attributeType)) {
                        val eventTypeForTagType = eventToSynthetic(a.attributeType)
                        s"""import slinky.core.OptionalAttrPair.optionToJsOption
                           |
                           |@inline def :=[T <: slinky.core.TagElement](v: ${eventTypeForTagType("T#RefType")} => Unit) =
                           |  new slinky.core.AttrPair[T]("${a.attributeName + nameSuffix}", v)
                           |@inline def :=(v: () => Unit) = new slinky.core.AttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}", v)
                           |@inline def :=(v: Option[() => Unit]) = new slinky.core.OptionalAttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}", optionToJsOption(v))""".stripMargin
                      } else if (a.attributeType == "RefType") {
                        s"""@inline def :=[T <: slinky.core.TagElement](v: T#RefType => Unit) =
                           |  new slinky.core.AttrPair[T]("${a.attributeName + nameSuffix}", v)
                           |@inline def :=[T <: slinky.core.TagElement, E <: T#RefType](v: slinky.core.RefAttr[E]) =
                           |  new slinky.core.AttrPair[T]("${a.attributeName + nameSuffix}", v)""".stripMargin
                      } else {
                        s"""import slinky.core.OptionalAttrPair.optionToJsOption
                           |
                           |@inline def :=(v: ${a.attributeType}) = new slinky.core.AttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}", v)
                           |@inline def :=(v: Option[${a.attributeType}]) = new slinky.core.OptionalAttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}", optionToJsOption(v))""".stripMargin
                      }) + s"\ntype attrType = _${symbolWithoutEscape + nameSuffix}_attr.type"

          if (a.withDash) {
            Seq(
              base,
              s"""import slinky.core.OptionalAttrPair.optionToJsOption
                 |
                 |final class WithDash(@inline private val sub: String) extends AnyVal {
                 |@inline def :=(v: ${a.attributeType}) = new slinky.core.AttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}-" + sub, v)
                 |@inline def :=(v: Option[${a.attributeType}]) = new slinky.core.OptionalAttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]("${a.attributeName + nameSuffix}-" + sub, optionToJsOption(v)) }
                 |@inline def -(sub: String) = new WithDash(sub)""".stripMargin
            )
          } else Seq(base)

        }

        def attrToTagImplicits(nameSuffix: String = "") = attrs.toList.flatMap { a =>
          a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).flatMap { t =>
            val fixedT = if (t == "*") "star" else t
            Seq(
              s"""@inline implicit def to${fixedT}Applied(pair: slinky.core.AttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]): slinky.core.AttrPair[${Utils
                .identifierFor(t)}.tag.type] = pair.asInstanceOf[slinky.core.AttrPair[${Utils.identifierFor(
                t
              )}.tag.type]]
                 |@inline implicit def to${fixedT}OptionalApplied(pair: slinky.core.OptionalAttrPair[_${symbolWithoutEscape + nameSuffix}_attr.type]): slinky.core.OptionalAttrPair[${Utils
                .identifierFor(t)}.tag.type] = pair.asInstanceOf[slinky.core.OptionalAttrPair[${Utils
                .identifierFor(t)}.tag.type]]
               """.stripMargin
            )
          }
        }

        def booleanImplicits(nameSuffix: String = "") = attrs.toList.flatMap { a =>
          a.compatibleTags.getOrElse(extracted.tags.map(_.tagName)).flatMap { t =>
            val fixedT = if (t == "*") "star" else t
            if (attrs.isDefined && attrs.get.attributeType == "Boolean") {
              Seq(s"""@inline implicit def boolToPair${fixedT}Applied(@scala.annotation.nowarn attrObj: this.type): slinky.core.AttrPair[${Utils
                .identifierFor(t)}.tag.type] = new slinky.core.AttrPair[${Utils
                .identifierFor(t)}.tag.type]("${attrs.get.attributeName + nameSuffix}", true)""")
            } else Seq.empty
          }
        }

        val symbolExtendsList =
          (if (tags.nonEmpty) Seq("slinky.core.Tag") else Seq.empty) ++
            (if (attrs.isDefined) Seq("slinky.core.Attr") else Seq.empty)

        val symbolExtends = if (symbolExtendsList.isEmpty) "" else symbolExtendsList.mkString("extends ", " with ", "")

        def fileGen(symbol: String, nameSuffix: String = "") = {
          // Character "*" is not allowed in file names in Windows filesystem
          val symbolFixed = if (symbol == "*") "star" else symbol
          val out         = new PrintWriter(new File(outFolder.getAbsolutePath + "/" + symbolFixed + nameSuffix + ".scala"))

          out.println(
            s"""package $pkg
               |
               |/**
               | * ${(tags.map(_.docLines) ++ attrs.map(_.docLines)).flatten
              .map(_.replace("*", "&#47;"))
              .mkString("\n * ")}
               | */
               |object ${symbol + nameSuffix} $symbolExtends {
               |implicit object tag extends slinky.core.TagElement {
               |  type RefType = ${tags.headOption.map(_.scalaJSType).getOrElse("Nothing")}
               |}
               |${tagsGen.mkString("\n")}
               |${attrsGen(nameSuffix).mkString("\n")}
               |${booleanImplicits(nameSuffix).mkString("\n")}
               |}
               |
               |object _${symbolWithoutEscapeFixed + nameSuffix}_attr {
               |${attrToTagImplicits(nameSuffix).mkString("\n")}
               |}""".stripMargin
          )

          out.close()
        }

        attrs match {
          case Some(attr) if attr.hasCaptureVariant =>
            fileGen(symbol)
            fileGen(symbol, "Capture") // generate version with 'Capture' suffix
          case _ => fileGen(symbol)
        }

      }
    }
  }
}
