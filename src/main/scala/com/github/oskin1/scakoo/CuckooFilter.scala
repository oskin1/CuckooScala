package com.github.oskin1.scakoo

import scodec.bits.ByteVector

import scala.util.{Failure, Random, Success, Try}

/** Immutable Cuckoo Filter implementation. The Cuckoo Filter is a probabilistic data structure
  * that supports fast set membership testing. It is very similar to a bloom filter in that they
  * both are very fast and space efficient. Both the bloom filter and cuckoo filter also report
  * false positives on set membership. Cuckoo Filter supports items deletion.
  */
final class CuckooFilter[T] private(table: MemTable)(funnel: Funnel[T], strategy: TaggingStrategy)
  extends Serializable {

  private val nullFp: Byte = 0

  /** Insert the `value` fingerprint to the table unless the table is full.
    */
  def insert(value: T): Try[CuckooFilter[T]] = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    val emptyEntryIdx = table.emptyEntry(idx)
    if (emptyEntryIdx != -1) {
      Success(updated(table.updated(idx, emptyEntryIdx, fp)))
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEmptyEntryIdx = table.emptyEntry(altIdx)
      if (altEmptyEntryIdx != -1) {
        Success(updated(table.updated(altIdx, altEmptyEntryIdx, fp)))
      } else {
        val idxToSwap = if (Random.nextBoolean()) idx else altIdx
        swap(idxToSwap, fp)
      }
    }
  }

  /** Perform an insert if fingerprint of `value` isn't already contained in the table.
    */
  def insertUnique(value: T): Try[CuckooFilter[T]] = if (!lookup(value)) insert(value) else Success(this)

  def delete(value: T): CuckooFilter[T] = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    val entryIdx = table.entryIndex(idx, fp)
    if (entryIdx != -1) {
      updated(table.updated(idx, entryIdx, nullFp))
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEntryIdx = table.entryIndex(idx, fp)
      if (altEntryIdx != -1) updated(table.updated(altIdx, altEntryIdx, nullFp))
      else this
    }
  }

  def lookup(value: T): Boolean = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    table.containsEntry(idx, fp) || table.containsEntry(strategy.altIndex(idx, fp, size), fp)
  }

  /** Absolute maximum number of entries the filter can theoretically contain.
    */
  def capacity: Long = table.capacity

  def size: Long = table.numBuckets

  def entriesPerBucket: Int = table.entriesPerBucket

  def memTable: ByteVector = table.memBlock

  override def toString: String = table.toString

  /** Swap fingerprint with some other one from random entry of `idx`th bucket.
    */
  private def swap(idx: Long, fp: Byte): Try[CuckooFilter[T]] = {
    def loop(idx0: Long, fp0: Byte, acc: MemTable = table, counter: Int = 0): Try[MemTable] = {
      val entryIdx = Random.nextInt(table.entriesPerBucket)
      val swappedFp = table.readEntry(idx0, entryIdx)
      val altIdx = strategy.altIndex(idx, swappedFp, table.numBuckets)
      table.emptyEntry(altIdx) match {
        case x if x != -1 =>
          Success(acc.updated(idx0, entryIdx, fp0).updated(altIdx, entryIdx, swappedFp))
        case _ if counter < CuckooFilter.MaxSwapsQty =>
          loop(altIdx, swappedFp, acc.updated(idx0, entryIdx, fp0), counter + 1)
        case _ =>
          Failure(new Exception("Filter is full"))
      }
    }
    loop(idx, fp).map(updated)
  }

  private def updated(mt: MemTable): CuckooFilter[T] = new CuckooFilter[T](mt)(funnel, strategy)

}

object CuckooFilter {

  val MaxSwapsQty: Int = 500

  def apply[T](entriesPerBucket: Int, bucketsQty: Long)
              (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    new CuckooFilter[T](MemTable(entriesPerBucket, bucketsQty))(funnel, strategy)
  }

  def recover[T](memBlock: ByteVector, entriesPerBucket: Int)
                (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    val table = new MemTable(memBlock, entriesPerBucket)
    new CuckooFilter[T](table)(funnel, strategy)
  }

}
