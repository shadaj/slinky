package slinky.core

import slinky.core.facade.ReactElement

import scala.collection.immutable.{Iterable, Queue}
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Try

trait ReactElementContainer[F[_]] extends Any { self =>
  def map[A](fa: F[A])(f: A => ReactElement): F[ReactElement]
}

object ReactElementContainer {
  def apply[F[_] : ReactElementContainer]: ReactElementContainer[F] = implicitly[ReactElementContainer[F]]

  @inline implicit def function0Container: ReactElementContainer[Function0] = new ReactElementContainer[Function0] {
    override def map[A](fa: () => A)(f: A => ReactElement): () => ReactElement = () => f(fa())
  }

  @inline implicit def futureContainer: ReactElementContainer[Future] = new ReactElementContainer[Future] {
    import scala.concurrent.ExecutionContext.Implicits.global
    override def map[A](fa: Future[A])(f: A => ReactElement): Future[ReactElement] = fa.map(f)
  }

  @inline implicit def iterableContainer: ReactElementContainer[Iterable] = new ReactElementContainer[Iterable] {
    override def map[A](fa: Iterable[A])(f: A => ReactElement): Iterable[ReactElement] = fa.map(f)
  }

  @inline implicit def jsUndefOrContainer: ReactElementContainer[js.UndefOr] = new ReactElementContainer[js.UndefOr] {
    override def map[A](fa: js.UndefOr[A])(f: A => ReactElement): js.UndefOr[ReactElement] = fa.map(f)
  }

  @inline implicit def listContainer: ReactElementContainer[List] = new ReactElementContainer[List] {
    override def map[A](fa: List[A])(f: A => ReactElement): List[ReactElement] = fa.map(f)
  }

  @inline implicit def optionContainer: ReactElementContainer[Option] = new ReactElementContainer[Option] {
    override def map[A](fa: Option[A])(f: A => ReactElement): Option[ReactElement] = fa.map(f)
  }

  @inline implicit def queueContainer: ReactElementContainer[Queue] = new ReactElementContainer[Queue] {
    override def map[A](fa: Queue[A])(f: A => ReactElement): Queue[ReactElement] = fa.map(f)
  }

  @inline implicit def seqContainer: ReactElementContainer[Seq] = new ReactElementContainer[Seq] {
    override def map[A](fa: Seq[A])(f: A => ReactElement): Seq[ReactElement] = fa.map(f)
  }

  @inline implicit def setContainer: ReactElementContainer[Set] = new ReactElementContainer[Set] {
    override def map[A](fa: Set[A])(f: A => ReactElement): Set[ReactElement] = fa.map(f)
  }

  @inline implicit def someContainer: ReactElementContainer[Some] = new ReactElementContainer[Some] {
    override def map[A](fa: Some[A])(f: A => ReactElement): Some[ReactElement] = Some(fa.map(f).get)
  }

  @inline implicit def tryContainer: ReactElementContainer[Try] = new ReactElementContainer[Try] {
    override def map[A](fa: Try[A])(f: A => ReactElement): Try[ReactElement] = fa.map(f)
  }

  @inline implicit def vectorContainer: ReactElementContainer[Vector] = new ReactElementContainer[Vector] {
    override def map[A](fa: Vector[A])(f: A => ReactElement): Vector[ReactElement] = fa.map(f)
  }
}