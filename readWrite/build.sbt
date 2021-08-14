import java.io.PrintWriter

enablePlugins(ScalaJSPlugin)

name := "slinky-readwrite"

scalacOptions ~= {
  _.filterNot(_ == "-source:3.0-migration") // Having this option breaks nested quotes/splices entirely
}

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value
    )
    case _ =>
      Seq(
      "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
    )
  }
}

Compile / sourceGenerators += Def.task {
  val genFile = (Compile / sourceManaged).value / "GenWriters.scala"
  (Compile / sourceManaged).value.mkdirs()
  genFile.createNewFile()
  val out = new PrintWriter(genFile)
  val gens = (0 to 22).map { n =>
    s"""implicit def function$n[${(0 until n).map(i => s"I$i, ").mkString} O]
       |              (implicit ${(0 until n)
         .map(i => s"i${i}Reader: Reader[I$i], ")
         .mkString} oWriter: Writer[O]): Writer[(${(0 until n).map(i => s"I$i").mkString(", ")}) => O] = s => {
       |  val fn: js.Function$n[${(0 until n)
         .map(_ => "js.Object, ")
         .mkString} js.Object] = (${(0 until n).map(i => s"i$i: js.Object").mkString(", ")}) => {
       |    oWriter.write(s(${(0 until n).map(i => s"i${i}Reader.read(i$i)").mkString(", ")}))
       |  }
       |
       |  fn.asInstanceOf[js.Object]
       |}""".stripMargin
  }

  out.println(s"""package slinky.readwrite
                 |
                 |import scala.scalajs.js
                 |
                 |trait Writer[P] {
                 |  def write(p: P): js.Object
                 |}
                 |
                 |trait FunctionWriters {
                 |  ${gens.mkString("\n")}
                 |}
                 |
                 |object Writer extends CoreWriters""".stripMargin)

  out.close()
  Seq(genFile)
}

Compile / sourceGenerators += Def.task {
  val genFile = (Compile / sourceManaged).value / "GenReaders.scala"
  (Compile / sourceManaged).value.mkdirs()
  genFile.createNewFile()
  val out = new PrintWriter(genFile)
  val gens = (0 to 22).map { n =>
    s"""implicit def function$n[${(0 until n).map(i => s"I$i, ").mkString} O]
       |              (implicit ${(0 until n)
         .map(i => s"i${i}Writer: Writer[I$i], ")
         .mkString} oReader: Reader[O]): Reader[(${(0 until n).map(i => s"I$i").mkString(", ")}) => O] = s => {
       |  val fn = s.asInstanceOf[js.Function$n[${(0 until n).map(_ => "js.Object, ").mkString} js.Object]]
       |  (${(0 until n).map(i => s"i$i: I$i").mkString(", ")}) => {
       |    oReader.read(fn(${(0 until n).map(i => s"i${i}Writer.write(i$i)").mkString(", ")}))
       |  }
       |}""".stripMargin
  }

  out.println(s"""package slinky.readwrite
                 |
                 |import scala.scalajs.js
                 |
                 |trait Reader[P] {
                 |  def read(o: js.Object): P = {
                 |    val ret = if (js.typeOf(o) == "object" && o != null && !js.isUndefined(o.asInstanceOf[js.Dynamic].__)) {
                 |      o.asInstanceOf[js.Dynamic].__.asInstanceOf[P]
                 |    } else {
                 |      forceRead(o)
                 |    }
                 |
                 |    if (ret.isInstanceOf[WithRaw]) {
                 |      ret.asInstanceOf[js.Dynamic].__slinky_raw = o
                 |    }
                 |
                 |    ret
                 |  }
                 |
                 |  protected def forceRead(o: js.Object): P
                 |}
                 |
                 |trait AlwaysReadReader[P] extends Reader[P] {
                 |  override def read(o: js.Object): P = forceRead(o)
                 |  protected def forceRead(o: js.Object): P
                 |}
                 |
                 |trait FunctionReaders {
                 |  ${gens.mkString("\n")}
                 }
                 |
                 |object Reader extends CoreReaders""".stripMargin)

  out.close()
  Seq(genFile)
}
