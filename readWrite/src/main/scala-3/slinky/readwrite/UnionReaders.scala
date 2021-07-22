package slinky.readwrite

trait UnionReaders {

  // implicit def unionReader[A, B](implicit aReader: Reader[A], bReader: Reader[B]): Reader[A | B] = s => {
  //   try {
  //     aReader.read(s)
  //   } catch {
  //     case _: Throwable => bReader.read(s)
  //   }
  // }

}
