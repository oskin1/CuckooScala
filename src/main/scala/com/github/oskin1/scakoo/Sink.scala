package com.github.oskin1.scakoo

import java.nio.charset.Charset

import com.google.common.primitives.{Bytes, Ints, Longs, Shorts}

case class Sink(value: Array[Byte]) extends AnyVal {

  def putByte(v: Byte): Sink = {
    Sink(Bytes.concat(value, Array(v)))
  }

  def putShort(v: Short): Sink = {
    Sink(Bytes.concat(value, Shorts.toByteArray(v)))
  }

  def putInt(v: Int): Sink = {
    Sink(Bytes.concat(value, Ints.toByteArray(v)))
  }

  def putLong(v: Long): Sink = {
    Sink(Bytes.concat(value, Longs.toByteArray(v)))
  }

  def putByteArray(v: Array[Byte]): Sink = {
    Sink(Bytes.concat(value, v))
  }

  def putString(v: String, charset: Charset): Sink = {
    Sink(Bytes.concat(value, v.getBytes(charset)))
  }

}

object Sink {

  def fromByte(v: Byte): Sink = {
    Sink(Array(v))
  }

  def fromShort(v: Short): Sink = {
    Sink(Shorts.toByteArray(v))
  }

  def fromInt(v: Int): Sink = {
    Sink(Ints.toByteArray(v))
  }

  def fromLong(v: Long): Sink = {
    Sink(Longs.toByteArray(v))
  }

  def fromByteArray(v: Array[Byte]): Sink = {
    Sink(v)
  }

  def fromString(v: String, charset: Charset): Sink = {
    Sink(v.getBytes(charset))
  }

}
