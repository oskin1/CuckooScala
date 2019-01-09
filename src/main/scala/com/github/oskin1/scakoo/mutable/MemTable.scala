package com.github.oskin1.scakoo.mutable

import com.github.oskin1.scakoo.{BaseMemTable, Utils}
import com.google.common.math.IntMath

/** Mutable memory table implementation backed by byte array.
  */
private[scakoo] class MemTable(val memBlock: Array[Byte], val entriesPerBucket: Int)
  extends BaseMemTable {

  def emptyEntry(idx: Int): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock(idx * entriesPerBucket + entriesChecked) == 0) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  /** Insert value to the `entryIdx`th entry in `bucketIdx`th bucket.
    */
  def update(bucketIdx: Int, entryIdx: Int, value: Byte): Unit = {
    memBlock.update(bucketIdx * entriesPerBucket + entryIdx, value)
  }

  def entryIndex(bucketIdx: Int, value: Byte): Int = {
    def checkBucket(entriesChecked: Int = 0): Int = {
      if (memBlock(bucketIdx * entriesPerBucket + entriesChecked) == value) entriesChecked
      else if (entriesChecked + 1 < entriesPerBucket) checkBucket(entriesChecked + 1)
      else -1
    }
    checkBucket()
  }

  def readEntry(bucketIdx: Int, entryIdx: Int): Byte = memBlock(bucketIdx * entriesPerBucket + entryIdx)

  def capacity: Int = memBlock.length

}

private object MemTable {

  def apply(entriesPerBucket: Int, desiredBucketsQty: Int): MemTable = {
    // force number of buckets to be a power of 2 due to "modulo bias".
    val bucketsQty = Utils.nextPositivePowerOfTwo(desiredBucketsQty)
    val blockSize = IntMath.checkedMultiply(bucketsQty, entriesPerBucket)
    new MemTable(Array.fill(blockSize)(0: Byte), entriesPerBucket)
  }
  
}
