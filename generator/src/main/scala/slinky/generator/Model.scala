package slinky.generator

object Utils {
  val keywords = Set("var", "for", "object", "val", "type")

  def identifierFor(name: String): String = {
    val camelCased = HTMLToJSMapping.dashToCamelCase(name)
    if (Utils.keywords.contains(camelCased)) {
      "`" + camelCased + "`"
    } else camelCased
  }
}

case class TagsModel(tags: Seq[Tag], attributes: Seq[Attribute])

case class Tag(tagName: String, docLines: Seq[String])

case class Attribute(attributeName: String,
                     attributeType: String,
                     docLines: Seq[String],
                     compatibleTags: Option[Seq[String]],
                     withDash: Boolean) /* tag, identifier, doc */
