package me.shadaj.slinky.generator

import io.circe.generic.auto._, io.circe.syntax._

trait TagsProvider {
  def extract: (Seq[Tag], Seq[Attribute])

  def main(args: Array[String]): Unit = {
    val extracted = extract
    println(TagsModel(extracted._1, extracted._2).asJson.toString())
  }
}
