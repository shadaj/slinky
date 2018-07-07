package slinky.generator

import io.circe.generic.auto._, io.circe.syntax._

trait TagsProvider {
  def extract: (Seq[Tag], Seq[Attribute])

  def main(args: Array[String]): Unit = {
    val extracted = extract
    println(TagsModel(extracted._1, extracted._2 ++ Seq(
      Attribute(
        "key",
        "String",
        Seq.empty,
        compatibleTags = None,
        false
      ),
      Attribute(
        "ref",
        "RefType",
        Seq.empty,
        compatibleTags = None,
        false
      ),
      Attribute(
        "dangerouslySetInnerHTML",
        "js.Object",
        Seq.empty,
        compatibleTags = None,
        false
      )
    )).asJson.toString())
  }
}
