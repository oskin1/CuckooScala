package com.github.oskin1.scakoo

/** A type that provides methods for hash and fingerprint calculation of some [[Sink]].
  */
trait TaggingStrategy {

  def hash(value: Sink): Long

  def fingerprintOf(value: Sink): Byte

  def tag(value: Sink, indexRange: Long): CuckooTag = {
    val idx = hash(value) % indexRange
    val fp = fingerprintOf(value)
    CuckooTag(idx, fp)
  }

  def altIndex(tag: CuckooTag, indexRange: Long): Long = {
    val fpHash = hash(Sink.fromByte(tag.fingerprint))
    (tag.idx ^ fpHash) % indexRange
  }

}
