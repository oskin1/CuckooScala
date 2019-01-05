package com.github.oskin1.scakoo

import scala.util.hashing.{ByteswapHashing, MurmurHash3}

/** A type that provides methods for tag calculation of some [[Sink]].
  */
trait TaggingStrategy {

  def hash(value: Sink): Int

  def fingerprintOf(value: Sink): Byte = (hash(value) % 255).toByte

  def tag(value: Sink, indexRange: Long): (Long, Byte) = {
    val hashVal = hash(value)
    val idx = (if (hashVal < 0) ~hashVal else hashVal) % indexRange
    def notEmptyFp(curTag: Byte, salt: Int = 0): Byte = {
      if (curTag == 0) notEmptyFp(fingerprintOf(value.putInt(salt)), salt + 1)
      else curTag
    }
    val fp = notEmptyFp(fingerprintOf(value))
    (idx, fp)
  }

  def altIndex(idx: Long, fp: Byte, indexRange: Long): Long = {
    val fpHash = hash(Sink.fromByte(fp))
    val hashVal = idx ^ fpHash
    (if (hashVal < 0) ~hashVal else hashVal) % indexRange
  }

}

object MurmurHash3Strategy extends TaggingStrategy {

  override def hash(value: Sink): Int = MurmurHash3.arrayHash(value.data)

}

object ByteswapHashStrategy extends TaggingStrategy {

  private val hashing = new ByteswapHashing[Array[Byte]]

  override def hash(value: Sink): Int = hashing.hash(value.data)

}
