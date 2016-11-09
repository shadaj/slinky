package me.shadaj.simple.react.generator

import java.io.{File, PrintWriter}

object Generator extends App {
  new File("core/src/gen/scala/me/shadaj/simple/react/core/html").mkdirs()
  val target = new File("core/src/gen/scala/me/shadaj/simple/react/core/html/gen.scala")

  if (!target.exists()) {
    val output = new PrintWriter(target)
    output.println(SimpleReactGenerator.generateGen)
    output.close()
  }
}
