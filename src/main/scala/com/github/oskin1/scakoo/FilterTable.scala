package com.github.oskin1.scakoo

import com.google.common.math.LongMath
import scodec.bits.BitVector

class FilterTable[T] private (memBlock: BitVector)(strategy: TaggingStrategy[T]) {}

object FilterTable {

  def apply[T](bucketCapacity: Int,
               bucketsQty: Int,
               fingerprintSizeBits: Int)(implicit strategy: TaggingStrategy[T]): FilterTable[T] = {
    val blockSize = LongMath.checkedMultiply(bucketsQty, bucketCapacity * fingerprintSizeBits)
    new FilterTable[T](BitVector.low(blockSize))(strategy)
  }
  
}
