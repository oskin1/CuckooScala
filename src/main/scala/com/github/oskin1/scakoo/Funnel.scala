package com.github.oskin1.scakoo

import java.nio.charset.StandardCharsets

/** A type that provides a conversion from a value of type `A` to a [[Sink]] value.
  */
trait Funnel[A] { self =>

  /** Convert a value to [[Sink]].
    */
  def apply(value: A): Sink

}

object Funnel {

  implicit val byteArrayFunnel: Funnel[Array[Byte]] = { a: Array[Byte] =>
    Sink.fromByteArray(a)
  }

  implicit val stringFunnel: Funnel[String] = { s: String =>
    Sink.fromString(s, StandardCharsets.UTF_8)
  }

  implicit val intFunnel: Funnel[Int] = { i: Int =>
    Sink.fromInt(i)
  }

  implicit val longFunnel: Funnel[Long] = { i: Long =>
    Sink.fromLong(i)
  }

}
