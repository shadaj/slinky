package slinky.readwrite

import scala.reflect.ClassTag

trait UnionWriters {
  // implicit def unionWriter[A: ClassTag, B: ClassTag](implicit aWriter: Writer[A], bWriter: Writer[B]): Writer[A | B] = {
  //   v =>
  //     if (implicitly[ClassTag[A]].runtimeClass == v.getClass) {
  //       aWriter.write(v.asInstanceOf[A])
  //     } else if (implicitly[ClassTag[B]].runtimeClass == v.getClass) {
  //       bWriter.write(v.asInstanceOf[B])
  //     } else {
  //       try {
  //         aWriter.write(v.asInstanceOf[A])
  //       } catch {
  //         case _: Throwable =>
  //           bWriter.write(v.asInstanceOf[B])
  //       }
  //     }
  // }
}