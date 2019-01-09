package com.github.oskin1.scakoo.mutable

import com.github.oskin1.scakoo.{BaseCuckooFilter, Constants, Funnel, TaggingStrategy}

import scala.util.{Failure, Random, Success, Try}

/** Mutable Cuckoo Filter implementation. The Cuckoo Filter is a probabilistic data structure
  * that supports fast set membership testing. It is very similar to a bloom filter in that they
  * both are very fast and space efficient. Both the bloom filter and cuckoo filter also report
  * false positives on set membership. Cuckoo Filter supports items deletion.
  */
final class CuckooFilter[T] private(private[scakoo] val table: MemTable, private var _entriesCount: Int = 0)
                                   (val funnel: Funnel[T], val strategy: TaggingStrategy)
  extends BaseCuckooFilter[T] {

  /** Insert `value` fingerprint to the table unless the table is full.
    */
  def insert(value: T): Try[Unit] = {
    val (idx, fp) = strategy.tag(funnel(value), size)
    val emptyEntryIdx = table.emptyEntry(idx)
    if (emptyEntryIdx != -1) {
      table.update(idx, emptyEntryIdx, fp)
      _entriesCount += 1
      Success(())
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEmptyEntryIdx = table.emptyEntry(altIdx)
      if (altEmptyEntryIdx != -1) {
        table.update(altIdx, altEmptyEntryIdx, fp)
        _entriesCount += 1
        Success(())
      } else {
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
    val entryIdx = table.entryIndex(idx, fp)
    if (entryIdx != -1) {
      table.update(idx, entryIdx, Constants.NullFp)
      _entriesCount -= 1
    } else {
      val altIdx = strategy.altIndex(idx, fp, size)
      val altEntryIdx = table.entryIndex(idx, fp)
      if (altEntryIdx != -1) {
        table.update(altIdx, altEntryIdx, Constants.NullFp)
        _entriesCount -= 1
      }
    }
  }

  def loadFactor: Double = _entriesCount / (size * entriesPerBucket).toDouble

  def isEmpty: Boolean = _entriesCount == 0

  def entriesCount: Int = _entriesCount

  /** A copy of byte array storing all fingerprints.
    */
  def memTable: Array[Byte] = table.memBlock.clone()

  /** Swap fingerprint with some other one from random entry of `idx`th bucket.
    */
  private def swap(idx: Int, fp: Byte): Try[Unit] = {
    def loop(idx0: Int, fp0: Byte, counter: Int = 0): Try[Unit] = {
      val entryIdx = Random.nextInt(table.entriesPerBucket)
      val swappedFp = table.readEntry(idx0, entryIdx)
      val altIdx = strategy.altIndex(idx0, swappedFp, table.numBuckets)
      table.emptyEntry(altIdx) match {
        case emptyEntryIdx if emptyEntryIdx != -1 =>
          table.update(idx0, entryIdx, fp0)
          table.update(altIdx, emptyEntryIdx, swappedFp)
          Success(())
        case _ if counter < Constants.MaxSwapsQty =>
          table.update(idx0, entryIdx, fp0)
          loop(altIdx, swappedFp, counter + 1)
        case _ =>
          Failure(new Exception("Filter is full"))
      }
    }
    loop(idx, fp).map(_ => _entriesCount += 1)
  }

}

object CuckooFilter {

  def apply[T](entriesPerBucket: Int, bucketsQty: Int)
              (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    new CuckooFilter[T](MemTable(entriesPerBucket, bucketsQty))(funnel, strategy)
  }

  def recover[T](memBlock: Array[Byte], entriesCount: Int, entriesPerBucket: Int)
                (implicit funnel: Funnel[T], strategy: TaggingStrategy): CuckooFilter[T] = {
    val table = new MemTable(memBlock, entriesPerBucket)
    new CuckooFilter[T](table, entriesCount)(funnel, strategy)
  }

}
