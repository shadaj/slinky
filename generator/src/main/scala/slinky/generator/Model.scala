package slinky.generator

object Utils {
  val keywords = Set("var", "for", "object", "val", "type")

  def identifierFor(name: String): String = {
    if (Utils.keywords.contains(name)) {
      "`" + name + "`"
    } else name
  }
}

case class TagsModel(tags: Seq[Tag], attributes: Seq[Attribute])

case class Tag(tagName: String, scalaJSType: String, docLines: Seq[String])

case class Attribute(attributeName: String,
                     attributeType: String,
                     docLines: Seq[String],
                     compatibleTags: Option[Seq[String]],
                     withDash: Boolean) /* tag, identifier, doc */
