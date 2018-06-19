import java.io.PrintWriter

enablePlugins(ScalaJSPlugin)

name := "slinky-readwrite"

// TODO: use published version after there is a release with support for fallback derivations
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.6.0"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

sourceGenerators in Compile += Def.task {
  val genFile = (sourceManaged in Compile).value / "GenWriters.scala"
  (sourceManaged in Compile).value.mkdirs()
  genFile.createNewFile()
  val out = new PrintWriter(genFile)
  val gens = (0 to 22).map { n =>
    s"""implicit def function$n[${(0 until n).map(i => s"I$i, ").mkString} O]
       |              (implicit ${(0 until n).map(i => s"i${i}Reader: Reader[I$i], ").mkString} oWriter: Writer[O]): Writer[(${(0 until n).map(i => s"I$i").mkString(", ")}) => O] = s => {
       |  val fn: js.Function$n[${(0 until n).map(_ => "js.Object, ").mkString} js.Object] = (${(0 until n).map(i => s"i$i: js.Object").mkString(", ")}) => {
       |    oWriter.write(s(${(0 until n).map(i => s"i${i}Reader.read(i$i)").mkString(", ")}))
       |  }
       |
       |  fn.asInstanceOf[js.Object]
       |}""".stripMargin
  }

  out.println(
    s"""package slinky.readwrite
       |import scala.collection.generic.CanBuildFrom
       |import scala.concurrent.Future
       |import scala.language.experimental.macros
       |import scala.language.{higherKinds, implicitConversions}
       |import scala.reflect.ClassTag
       |import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
       |import scala.scalajs.js
       |import scala.scalajs.js.|
       |
       |import magnolia._
       |
       |trait Writer[P] {
       |  def write(p: P): js.Object
       |}
       |
       |object Writer {
       |  implicit def jsAnyWriter[T <: js.Any]: Writer[T] = _.asInstanceOf[js.Object]
       |
       |  implicit val unitWriter: Writer[Unit] = _ => js.Dynamic.literal()
       |
       |  implicit val stringWriter: Writer[String] = _.asInstanceOf[js.Object]
       |
       |  implicit val charWriter: Writer[Char] = _.toString.asInstanceOf[js.Object]
       |
       |  implicit val byteWriter: Writer[Byte] = _.asInstanceOf[js.Object]
       |
       |  implicit val shortWriter: Writer[Short] = _.asInstanceOf[js.Object]
       |
       |  implicit val intWriter: Writer[Int] = _.asInstanceOf[js.Object]
       |
       |  implicit val longWriter: Writer[Long] = _.toString.asInstanceOf[js.Object]
       |
       |  implicit val booleanWriter: Writer[Boolean] = _.asInstanceOf[js.Object]
       |
       |  implicit val doubleWriter: Writer[Double] = _.asInstanceOf[js.Object]
       |
       |  implicit val floatWriter: Writer[Float] = _.asInstanceOf[js.Object]
       |
       |  implicit def undefOrWriter[T](implicit writer: Writer[T]): Writer[js.UndefOr[T]] =
       |    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])
       |
       |  implicit def unionWriter[A: ClassTag, B: ClassTag](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[A | B] = { v =>
       |    try {
       |      aWriter.write(v.asInstanceOf[A])
       |    } catch {
       |      case e: Throwable =>
       |        try {
       |          bWriter.write(v.asInstanceOf[B])
       |        } catch {
       |          case e2: Throwable =>
       |            println("Neither writer for the union worked.")
       |            e.printStackTrace()
       |            throw e2
       |        }
       |    }
       |  }
       |
       |  implicit def optionWriter[T](implicit writer: Writer[T]): Writer[Option[T]] =
       |    _.map(v => writer.write(v)).getOrElse(js.undefined.asInstanceOf[js.Object])
       |
       |  implicit def collectionWriter[T, C[_]](implicit writer: Writer[T],
       |                                         cbf: CanBuildFrom[Nothing, T, Seq[T]],
       |                                         ev: C[T] <:< Iterable[T]): Writer[C[T]] = s => {
       |    js.Array(s.to[Seq](cbf).map(v => writer.write(v)): _*).asInstanceOf[js.Object]
       |  }
       |
       |  implicit def futureWriter[O](implicit oWriter: Writer[O]): Writer[Future[O]] = s => {
       |    import scala.scalajs.js.JSConverters._
       |    s.map(v => oWriter.write(v)).toJSPromise.asInstanceOf[js.Object]
       |  }
       |
       |  ${gens.mkString("\n")}
       |
       |  type Typeclass[T] = Writer[T]
       |
       |  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = value => {
       |    if (ctx.isValueClass) {
       |      val param = ctx.parameters.head
       |      param.typeclass.write(param.dereference(value))
       |    } else if (ctx.isObject) {
       |      js.Dynamic.literal("_type" -> ctx.typeName.full)
       |    } else {
       |      val ret = js.Dynamic.literal()
       |      ctx.parameters.foreach { param =>
       |        val dereferenced = param.dereference(value)
       |        // If any value is js.undefined, don't add it as a property to the written object.
       |        // This way, JS libraries that rely on checking if a property does not exists (where or not set to undefined)
       |        // will work correctly
       |        if (!js.isUndefined(dereferenced)) {
       |          ret.updateDynamic(param.label)(param.typeclass.write(dereferenced))
       |        }
       |      }
       |
       |      ret
       |    }
       |  }
       |
       |  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = value => {
       |    ctx.dispatch(value) { sub =>
       |      val ret = sub.typeclass.write(sub.cast(value))
       |
       |      ret.asInstanceOf[js.Dynamic].updateDynamic("_type")(sub.typeName.full)
       |
       |      ret
       |    }
       |  }
       |
       |  def fallback[T]: Writer[T] = s => js.Dynamic.literal(__ = s.asInstanceOf[js.Any])
       |
       |  implicit def generic[T]: Typeclass[T] = macro Magnolia.gen[T]
       |}""".stripMargin)

  out.close()
  Seq(genFile)
}

sourceGenerators in Compile += Def.task {
  val genFile = (sourceManaged in Compile).value / "GenReaders.scala"
  (sourceManaged in Compile).value.mkdirs()
  genFile.createNewFile()
  val out = new PrintWriter(genFile)
  val gens = (0 to 22).map { n =>
    s"""implicit def function$n[${(0 until n).map(i => s"I$i, ").mkString} O]
       |              (implicit ${(0 until n).map(i => s"i${i}Writer: Writer[I$i], ").mkString} oReader: Reader[O]): Reader[(${(0 until n).map(i => s"I$i").mkString(", ")}) => O] = s => {
       |  val fn = s.asInstanceOf[js.Function$n[${(0 until n).map(_ => "js.Object, ").mkString} js.Object]]
       |  (${(0 until n).map(i => s"i$i: I$i").mkString(", ")}) => {
       |    oReader.read(fn(${(0 until n).map(i => s"i${i}Writer.write(i$i)").mkString(", ")}))
       |  }
       |}""".stripMargin
  }

  out.println(
    s"""package slinky.readwrite
       |
       |import scala.collection.generic.CanBuildFrom
       |import scala.concurrent.Future
       |import scala.language.experimental.macros
       |import scala.language.{higherKinds, implicitConversions}
       |import scala.reflect.ClassTag
       |import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
       |import scala.scalajs.js
       |import scala.scalajs.js.|
       |
       |import magnolia._
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
       |object Reader {
       |  implicit def jsAnyReader[T <: js.Any]: Reader[T] = _.asInstanceOf[T]
       |
       |  implicit val unitReader: Reader[Unit] = _ => ()
       |
       |  implicit val stringReader: Reader[String] = _.asInstanceOf[String]
       |
       |  implicit val charReader: Reader[Char] = _.asInstanceOf[String].head
       |
       |  implicit val byteReader: Reader[Byte] = _.asInstanceOf[Byte]
       |
       |  implicit val shortReader: Reader[Short] = _.asInstanceOf[Short]
       |
       |  implicit val intReader: Reader[Int] = _.asInstanceOf[Int]
       |
       |  implicit val longReader: Reader[Long] = _.asInstanceOf[String].toLong
       |
       |  implicit val booleanReader: Reader[Boolean] = _.asInstanceOf[Boolean]
       |
       |  implicit val doubleReader: Reader[Double] = _.asInstanceOf[Double]
       |
       |  implicit val floatReader: Reader[Float] = _.asInstanceOf[Float]
       |
       |  implicit def undefOrReader[T](implicit reader: Reader[T]): Reader[js.UndefOr[T]] = s => {
       |    if (js.isUndefined(s)) {
       |      js.undefined
       |    } else {
       |      reader.read(s)
       |    }
       |  }
       |
       |  implicit def unionReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[A | B] = s => {
       |    try {
       |      aReader.read(s)
       |    } catch {
       |      case _: Throwable => bReader.read(s)
       |    }
       |  }
       |
       |  implicit def optionReader[T](implicit reader: Reader[T]): Reader[Option[T]] = s => {
       |    if (js.isUndefined(s) || s == null) {
       |      None
       |    } else {
       |      Some(reader.read(s))
       |    }
       |  }
       |
       |  implicit def collectionReader[T, C[_]](implicit reader: Reader[T],
       |                                         cbf: CanBuildFrom[Nothing, T, C[T]],
       |                                         ev: C[T] <:< Iterable[T]): Reader[C[T]] =
       |    _.asInstanceOf[js.Array[js.Object]].map(o => reader.read(o)).to[C]
       |
       |  implicit def futureReader[O](implicit oReader: Reader[O]): Reader[Future[O]] =
       |    _.asInstanceOf[js.Promise[js.Object]].toFuture.map { v =>
       |      oReader.read(v)
       |    }
       |
       |  ${gens.mkString("\n")}
       |
       |  type Typeclass[T] = Reader[T]
       |
       |  def combine[T](ctx: CaseClass[Typeclass, T]): Typeclass[T] = o => {
       |    if (ctx.isValueClass) {
       |      ctx.construct { param =>
       |        param.typeclass.read(o)
       |      }
       |    } else {
       |      ctx.construct { param =>
       |        param.typeclass.read(o.asInstanceOf[js.Dynamic].selectDynamic(param.label).asInstanceOf[js.Object])
       |      }
       |    }
       |  }
       |
       |  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Typeclass[T] = o => {
       |    val typeString = o.asInstanceOf[js.Dynamic]._type.asInstanceOf[String]
       |    ctx.subtypes.find(_.typeName.full == typeString).get.typeclass.read(o)
       |  }
       |
       |  def fallback[T]: Reader[T] = v => {
       |    if (js.isUndefined(v.asInstanceOf[js.Dynamic].__)) {
       |      throw new IllegalArgumentException("Tried to read opaque Scala.js type that was not written by opaque writer")
       |    } else {
       |      v.asInstanceOf[js.Dynamic].__.asInstanceOf[T]
       |    }
       |  }
       |
       |  implicit def generic[T]: Typeclass[T] = macro Magnolia.gen[T]
       |}""".stripMargin)

  out.close()
  Seq(genFile)
}
