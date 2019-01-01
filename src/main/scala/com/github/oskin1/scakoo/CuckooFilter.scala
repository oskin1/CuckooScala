package com.github.oskin1.scakoo

/** Immutable Cuckoo Filter implementation. The Cuckoo Filter is a probabilistic data structure
  * that supports fast set membership testing. It is very similar to a bloom filter in that they
  * both are very fast and space efficient. Both the bloom filter and cuckoo filter also report
  * false positives on set membership.
  */
final class CuckooFilter[T] private(table: MemTable)(funnel: Funnel[T], strategy: TaggingStrategy) {}

object CuckooFilter {

  val MaxSwapsQty: Int = 500

}
