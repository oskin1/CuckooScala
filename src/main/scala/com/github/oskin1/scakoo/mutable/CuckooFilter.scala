package com.github.oskin1.scakoo.mutable

import com.github.oskin1.scakoo.{Constants, Funnel, MemTable, TaggingStrategy}
import scodec.bits.ByteVector

import scala.collection.mutable
import scala.util.{Failure, Random, Success, Try}

/** Mutable Cuckoo Filter implementation. The Cuckoo Filter is a probabilistic data structure
  * that supports fast set membership testing. It is very similar to a bloom filter in that they
  * both are very fast and space efficient. Both the bloom filter and cuckoo filter also report
  * false positives on set membership. Cuckoo Filter supports items deletion.
  */
final class CuckooFilter[T] private(private var _table: MemTable, private var _entriesCount: Long = 0)
                                   (funnel: Funnel[T], strategy: TaggingStrategy)
  extends Serializable {

  /** Insert the `value` fingerprint to the table unless the table is full.
    */
  def insert(value: T): Try[Unit] = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    val emptyEntryIdx = _table.emptyEntry(idx)
    if (emptyEntryIdx != -1) {
      _table = _table.updated(idx, emptyEntryIdx, fp)
      _entriesCount += 1
      Success(())
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEmptyEntryIdx = _table.emptyEntry(altIdx)
      if (altEmptyEntryIdx != -1) {
        _table = _table.updated(altIdx, altEmptyEntryIdx, fp)
        _entriesCount += 1
        Success(())
      } else {
        mutable.HashMap
        val idxToSwap = if (Random.nextBoolean()) idx else altIdx
        swap(idxToSwap, fp)
      }
    }
  }

  /** Perform an insert if fingerprint of `value` isn't already contained in the table.
    */
  def insertUnique(value: T): Try[Unit] = if (!lookup(value)) insert(value) else Success(())

  def delete(value: T): Unit = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    val entryIdx = _table.entryIndex(idx, fp)
    if (entryIdx != -1) {
      _table = _table.updated(idx, entryIdx, Constants.NullFp)
      _entriesCount -= 1
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEntryIdx = _table.entryIndex(idx, fp)
      if (altEntryIdx != -1) {
        _table = _table.updated(altIdx, altEntryIdx, Constants.NullFp)
        _entriesCount -= 1
      }
    }
  }

  /** Check if some `value` contained in the filter with some false positive probability.
    */
  def lookup(value: T): Boolean = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    _table.containsEntry(idx, fp) || _table.containsEntry(strategy.altIndex(idx, fp, size), fp)
  }

  def entriesCount: Long = _entriesCount

  /** Current load factor of the filter. Reasonably sized filters could be
    * expected to reach 95% (0.95) load factor before insertion failure.
    */
  def loadFactor: Double = _entriesCount / (size * entriesPerBucket).toDouble

  def isEmpty: Boolean = _entriesCount == 0

  /** Absolute maximum number of entries the filter can theoretically contain.
    */
  def capacity: Long = _table.capacity

  def size: Long = _table.numBuckets

  def entriesPerBucket: Int = _table.entriesPerBucket

  def memTable: ByteVector = _table.memBlock

  override def toString: String = if (isEmpty) "CuckooFilter(empty)" else s"CuckooFilter(${loadFactor * 100}%, ${_table})"

  /** Swap fingerprint with some other one from random entry of `idx`th bucket.
    */
  private def swap(idx: Long, fp: Byte): Try[Unit] = {
    def loop(idx0: Long, fp0: Byte, acc: MemTable = _table, counter: Int = 0): Try[MemTable] = {
      val entryIdx = Random.nextInt(_table.entriesPerBucket)
      val swappedFp = _table.readEntry(idx0, entryIdx)
      val altIdx = strategy.altIndex(idx, swappedFp, _table.numBuckets)
      _table.emptyEntry(altIdx) match {
        case emptyEntryIdx if emptyEntryIdx != -1 =>
          Success(acc.updated(idx0, entryIdx, fp0).updated(altIdx, emptyEntryIdx, swappedFp))
        case _ if counter < Constants.MaxSwapsQty =>
          loop(altIdx, swappedFp, acc.updated(idx0, entryIdx, fp0), counter + 1)
        case _ =>
          Failure(new Exception("Filter is full"))
      }
    }
    loop(idx, fp).map { mt =>
      _table = mt
      _entriesCount += 1
    }
  }

}

object CuckooFilter {

  def apply[T](entriesPerBucket: Int, bucketsQty: Long)
              (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    new CuckooFilter[T](MemTable(entriesPerBucket, bucketsQty))(funnel, strategy)
  }

  def recover[T](memBlock: ByteVector, entriesCount: Long, entriesPerBucket: Int)
                (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    val table = new MemTable(memBlock, entriesPerBucket)
    new CuckooFilter[T](table, entriesCount)(funnel, strategy)
  }

}
