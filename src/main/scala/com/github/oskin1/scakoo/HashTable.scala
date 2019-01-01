package com.github.oskin1.scakoo

import com.google.common.math.LongMath
import scodec.bits.ByteVector

final class HashTable private (memBlock: ByteVector, entriesPerBucket: Int) {

  /** Find vacant entry in the bucket.
    * @return - vacant entry in the `idx`th bucket
    */
  def emptyEntry(idx: Long): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock.get(idx * entriesPerBucket + entriesChecked) == 0) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  def isVacantBucket(idx: Long): Boolean = emptyEntry(idx) != -1

  /** Insert value to the `idx`th bucket.
    */
  def insert(idx: Long, value: Byte): HashTable = {
    val emptyEntryIdx = emptyEntry(idx)
    if (emptyEntryIdx != -1) {
      new HashTable(memBlock.insert(idx * entriesPerBucket + emptyEntryIdx, value), entriesPerBucket)
    } else {
      this
    }
  }

  /** Remove value from the `idx`th bucket in case it is in the bucket.
    */
  def remove(idx: Long, value: Byte): HashTable = {
    val entryIdx = entryIndex(idx, value)
    if (entryIdx != -1) {
      new HashTable(memBlock.insert(idx * entriesPerBucket + entryIdx, value), entriesPerBucket)
    } else {
      this
    }
  }

  override def toString: String = memBlock.toString()

  /** Find index of the entry in the `bucketIdx`th bucket were specific `value` is located.
    */
  private def entryIndex(bucketIdx: Long, value: Byte): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock.get(bucketIdx * entriesPerBucket + entriesChecked) == value) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

}

object HashTable {

  def apply(entriesPerBucket: Int, bucketsQty: Int): HashTable = {
    val blockSize = LongMath.checkedMultiply(bucketsQty, entriesPerBucket)
    new HashTable(ByteVector.low(blockSize), entriesPerBucket)
  }
  
}
