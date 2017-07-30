package me.shadaj.slinky.generator

object Utils {
  val keywords = Set("var", "for", "object", "val", "type")
}

case class Tag(tagName: String, docLines: Seq[String]) {
  lazy val identifier = if (Utils.keywords.contains(tagName)) {
    "`" + tagName + "`"
  } else tagName
}

case class Attribute(attributeName: String,
                     attributeType: String,
                     compatibleTags: Seq[(Tag, String)],
                     withDash: Boolean) /* tag, identifier, doc */ {
  lazy val identifier = if (Utils.keywords.contains(attributeName)) {
    "`" + attributeName + "`"
  } else attributeName
}
