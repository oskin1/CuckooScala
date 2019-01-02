package com.github.oskin1.scakoo

import scala.util.hashing.MurmurHash3

/** A type that provides methods for tag calculation of some [[Sink]].
  */
trait TaggingStrategy {

  def hash(value: Sink): Int

  def fingerprintOf(value: Sink): Byte = (hash(value) % 255).toByte

  def tag(value: Sink, indexRange: Long): (Long, Byte) = {
    val idx = math.abs(hash(value) % indexRange)
    val fp = fingerprintOf(value)
    (idx, fp)
  }

  def altIndex(idx: Long, fp: Byte, indexRange: Long): Long = {
    val fpHash = hash(Sink.fromByte(fp))
    math.abs((idx ^ fpHash) % indexRange)
  }

}

object MurmurHash3Strategy extends TaggingStrategy {

  override def hash(value: Sink): Int = MurmurHash3.arrayHash(value.data)

}
