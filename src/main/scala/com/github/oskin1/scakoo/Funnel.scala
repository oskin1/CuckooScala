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

  implicit val byteFunnel: Funnel[Byte] = { v: Byte =>
    Sink.fromByte(v)
  }

  implicit val shortFunnel: Funnel[Short] = { v: Short =>
    Sink.fromShort(v)
  }

  implicit val intFunnel: Funnel[Int] = { v: Int =>
    Sink.fromInt(v)
  }

  implicit val longFunnel: Funnel[Long] = { v: Long =>
    Sink.fromLong(v)
  }

  implicit val byteArrayFunnel: Funnel[Array[Byte]] = { v: Array[Byte] =>
    Sink.fromByteArray(v)
  }

  implicit val stringFunnel: Funnel[String] = { v: String =>
    Sink.fromString(v, StandardCharsets.UTF_8)
  }

}
