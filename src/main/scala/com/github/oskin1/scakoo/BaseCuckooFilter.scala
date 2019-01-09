package com.github.oskin1.scakoo

trait BaseCuckooFilter[T] extends Serializable {

  private[scakoo] val table: BaseMemTable

  protected val funnel: Funnel[T]

  protected val strategy: TaggingStrategy

  /** Current load factor of the filter. Reasonably sized filters could be
    * expected to reach 95% (0.95) load factor before insertion failure.
    */
  def loadFactor: Double

  def isEmpty: Boolean

  def entriesCount: Int

  def entriesPerBucket: Int = table.entriesPerBucket

  def memTable: Array[Byte]

  /** Check if some `value` contained in the filter with some false positive probability.
    */
  def lookup(value: T): Boolean = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    table.containsEntry(idx, fp) || table.containsEntry(strategy.altIndex(idx, fp, size), fp)
  }

  /** Absolute maximum number of entries the filter can theoretically contain.
    */
  def capacity: Long = table.capacity

  def size: Int = table.numBuckets

  override def toString: String = if (isEmpty) "CuckooFilter(empty)" else s"CuckooFilter(${loadFactor * 100}%)"

}
