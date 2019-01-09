package com.github.oskin1.scakoo.immutable

import com.github.oskin1.scakoo.{BaseMemTable, Utils}
import com.google.common.math.IntMath
import scodec.bits.ByteVector

/** Immutable memory table implementation backed by [[ByteVector]].
  */
private[scakoo] class MemTable(val memBlock: ByteVector, val entriesPerBucket: Int)
  extends BaseMemTable {

  def emptyEntry(idx: Int): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock.get(idx * entriesPerBucket + entriesChecked) == 0) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  /** Insert value to the `entryIdx`th entry in `bucketIdx`th bucket.
    */
  def updated(bucketIdx: Int, entryIdx: Int, value: Byte): MemTable = {
    new MemTable(memBlock.update(bucketIdx * entriesPerBucket + entryIdx, value), entriesPerBucket)
  }

  def entryIndex(bucketIdx: Int, value: Byte): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock.get(bucketIdx * entriesPerBucket + entriesChecked) == value) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  def readEntry(bucketIdx: Int, entryIdx: Int): Byte = memBlock.get(bucketIdx * entriesPerBucket + entryIdx)

  def capacity: Int = memBlock.size.toInt

}

private object MemTable {

  def apply(entriesPerBucket: Int, desiredBucketsQty: Int): MemTable = {
    // force number of buckets to be a power of 2 due to "modulo bias".
    val bucketsQty = Utils.nextPositivePowerOfTwo(desiredBucketsQty)
    val blockSize = IntMath.checkedMultiply(bucketsQty, entriesPerBucket)
    new MemTable(ByteVector.low(blockSize), entriesPerBucket)
  }

}
