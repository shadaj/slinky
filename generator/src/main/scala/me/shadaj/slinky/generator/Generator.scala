package me.shadaj.slinky.generator

import java.io.{File, PrintWriter}

object Generator extends App {
  new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html").mkdirs()

  val tagsApplied = new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html/tagsApplied.scala")
  val tags = new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html/tags.scala")
  val attrs = new File("core/target/scala-2.12/src_managed/main/me/shadaj/slinky/core/html/attrs.scala")
  if (!tagsApplied.exists() || !tags.exists() || !attrs.exists()) {
    val gen = SlinkyGenerator.generateGen

    val tagsAppliedOut = new PrintWriter(tagsApplied, "UTF-8")
    val tagsOut = new PrintWriter(tags, "UTF-8")
    val attrsOut = new PrintWriter(attrs, "UTF-8")

    tagsAppliedOut.println(gen._1)
    tagsOut.println(gen._2)
    attrsOut.println(gen._3)

    tagsAppliedOut.close()
    tagsOut.close()
    attrsOut.close()
  }
}
