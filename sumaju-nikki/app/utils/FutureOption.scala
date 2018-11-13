package utils

import scala.concurrent.{ExecutionContext, Future}

object FutureOption {
  implicit def liftToFutureOption[T](value: Future[Option[T]]): FutureOption[T] = FutureOption[T](value)
  implicit def unliftFutureOption[T](lifted: FutureOption[T]): Future[Option[T]] = lifted.value
}

case class FutureOption[T](value: Future[Option[T]]) {
  def map[U](f: T => U)(implicit ec: ExecutionContext): FutureOption[U] =
    FutureOption[U](value.map(_.map(f)))

  def flatMap[U](f: T => FutureOption[U])(implicit ec: ExecutionContext): FutureOption[U] =
    FutureOption[U](value.flatMap(_.map(inner => f(inner).value).getOrElse(Future(None))))
}
