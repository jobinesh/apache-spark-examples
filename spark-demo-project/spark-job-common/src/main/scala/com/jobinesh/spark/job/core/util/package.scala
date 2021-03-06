package com.jobinesh.spark.job.core.util

import scala.util.{ Failure, Success, Try }

/**
 *  A common utility
 */
package object utils {


  /**
   * Simple decorator for the scala Try, which adds the following simple functions:
   * - composable error logging
   * - composable success logging
   * @param attempt
   * @tparam T
   */
  implicit class TryOps[T](val attempt: Try[T]) extends AnyVal {

    /**
     * Log the error using the logging function and return the failure
     * @param logging
     * @return
     */
    def logFailure(logging: (Throwable) => Unit) =
      attempt.recoverWith {
        case t: Throwable =>
          logging(t)
          Failure(t)
      }

    /**
     * Log the success using the logging function and return the attempt
     * @param logging
     * @return
     */
    def logSuccess(logging: (T) => Unit): Try[T] =
      attempt.map { t =>
        logging(t)
        t
      }

    /**
     * Log the progress and return back the try.
     * @param successLogging
     * @param failureLogging
     * @return
     */
    def log(successLogging: (T) => Unit, failureLogging: (Throwable) => Unit): Try[T] = {
      attempt match {
        case Success(s) => successLogging(s)
        case Failure(t) => failureLogging(t)
      }
      attempt
    }

    /**
     * Wraps a throwable into another throwable. Useful to wrap or change Exceptions.
     * @param map
     * @return
     */
    def mapFailure(map: Throwable => Throwable): Try[T] = attempt match {
      case Success(s) => attempt
      case Failure(t) => Failure(map(t))
    }
  }

  /**
   * Flatten a sequence of Trys to a try of sequence, which is a failure if any if the Trys is a failure.
   * @param attempts
   * @tparam T
   * @return
   */
  def allOkOrFail[T](attempts: Traversable[Try[T]]): Try[Traversable[T]] =
    attempts.foldLeft(Try(Seq[T]())) { (acc, tryField) => tryField.flatMap(tf => acc.map(tf +: _)) }

  /**
   * Separate the failures from the successes from a sequnce of `Try`s
   * @param attempts
   * @tparam T
   * @return
   */
  def separate[T](attempts: Traversable[Try[T]]): (Traversable[Throwable], Traversable[T]) = {
    def loop(attempts: Traversable[Try[T]], tx: Seq[Throwable], sx: Seq[T]): (Seq[Throwable], Seq[T]) =
      attempts match {
        case Nil => (tx, sx)
        case Success(s) :: rest => loop(rest, tx, s +: sx)
        case Failure(t) :: rest => loop(rest, t +: tx, sx)
      }
    val (tx, sx) = loop(attempts, Nil, Nil)
    (tx.reverse, sx.reverse)
  }

  /**
   * Simple decorator for the scala Try, which adds the following simple functions:
   * @param attempts
   * @tparam T
   */
  implicit class TraversableTryOps[T](val attempts: Traversable[Try[T]]) extends AnyVal {

    /**
     * Flatten a sequence of Trys to a try of sequence, which is a failure if any of the Trys is a failure.
     * @return
     */
    def allOkOrFail: Try[Traversable[T]] = utils.allOkOrFail(attempts)

    /**
     * Separate the failures from the successes from a sequnce of `Try`s
     * @return
     */
    def separate: (Traversable[Throwable], Traversable[T]) =
      utils.separate(attempts)
  }

  /**
   * Try running a code block that uses a resource which is silently closed on return.
   * @param resource resource creation block
   * @param code code that uses the resource
   * @tparam R resource type which must be [[AutoCloseable]]
   * @tparam T the code block return type
   * @return Success if the resource was successfully initialised and the code was successfully ran,
   *         even if the resource was not successfully closed.
   */
  def tryWithCloseable[R <: AutoCloseable, T](resource: => R)(code: R => T): Try[T] = {
    val res = Try(resource)
    val result = res.map(code)
    res.map(r => Try(r.close()))
    result
  }

  /** Convert a Scala Map into a Java Properties instance */
  def map2Properties[K, V](map: Map[K, V]): java.util.Properties = {
    val properties = new java.util.Properties()
    map.foreach { case (k, v) => properties.setProperty(k.toString, v.toString) }
    properties
  }

  /** Convert a Scala Map into a Java HashMap instance */
  def map2HashMap[K, V](map: Map[K, V]): java.util.HashMap[K, V] = {
    val hashmap = new java.util.HashMap[K, V]()
    map.foreach { case (k, v) => hashmap.put(k, v) }
    hashmap
  }

  /** Decorate Scala Maps with various utility functions */
  implicit class MapOps[K, V](val map: Map[K, V]) {
    def asProperties: java.util.Properties = map2Properties(map)
    def asHashMap: java.util.HashMap[K, V] = map2HashMap(map)
  }

}