package com.github.oskin1.scakoo

import com.google.common.math.LongMath
import scodec.bits.ByteVector

private final class MemTable(memBlock: ByteVector, val entriesPerBucket: Int) {

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

  /** Insert value to the `entryIdx`th entry in `bucketIdx`th bucket.
    */
  def updated(bucketIdx: Long, entryIdx: Int, value: Byte): MemTable = {
    updated0(memBlock.insert(bucketIdx * entriesPerBucket + entryIdx, value))
  }

  /** Find index of the entry in the `bucketIdx`th bucket were specific `value` is located.
    */
  def entryIndex(bucketIdx: Long, value: Byte): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock.get(bucketIdx * entriesPerBucket + entriesChecked) == value) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  def numBuckets: Long = memBlock.size / entriesPerBucket

  override def toString: String = memBlock.toString()

  private def updated0(mb: ByteVector): MemTable = new MemTable(mb, entriesPerBucket)

}

private object MemTable {

  val initialSize: Long = 16L

  def apply(entriesPerBucket: Int, bucketsQty: Long = initialSize): MemTable = {
    val blockSize = LongMath.checkedMultiply(bucketsQty, entriesPerBucket)
    new MemTable(ByteVector.low(blockSize), entriesPerBucket)
  }
  
}
