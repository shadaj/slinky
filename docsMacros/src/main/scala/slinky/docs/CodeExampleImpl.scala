package slinky.docs

import java.io.File

import slinky.core.facade.ReactElement

import scala.io.Source
import scala.reflect.macros.blackbox

object CodeExampleImpl {
  def text(c: blackbox.Context)(exampleLocation: c.Expr[String]): c.Expr[ReactElement] = {
    import c.universe._
    val Literal(Constant(loc: String)) = exampleLocation.tree
    val inputFile = new File(s"docs/src/main/scala/${loc.split('.').mkString("/")}.scala")
    val enclosingPackage = loc.split('.').init.mkString(".")

    val fileContent = Source.fromFile(inputFile).mkString

    val innerCode = fileContent.split('\n')

    val textToDisplay = innerCode
      .map(_.replaceAllLiterally("//display:", ""))
      .filterNot(_.endsWith("//nodisplay"))
      .dropWhile(_.trim.isEmpty)
      .reverse.dropWhile(_.trim.isEmpty).reverse
      .mkString("\n")

    val codeToRun = innerCode.filter(_.startsWith("//run:")).map(_.replaceAllLiterally("//run:", "")).mkString("\n")

    c.Expr[ReactElement](
      q"""{
         import ${c.parse(enclosingPackage)}._

         _root_.slinky.docs.CodeExampleInternal(codeText = ${Literal(Constant(textToDisplay))}, demoElement = {${c.parse(codeToRun)}})
       }""")
  }
}
