package com.github.oskin1.scakoo

import com.google.common.math.LongMath
import scodec.bits.BitVector

class HashTable private (memBlock: BitVector)(strategy: TaggingStrategy) {}

object HashTable {

  def apply(bucketCapacity: Int, bucketsQty: Int)(implicit strategy: TaggingStrategy): HashTable = {
    val blockSize = LongMath.checkedMultiply(bucketsQty, bucketCapacity * CuckooFilter.FingerprintSize)
    new HashTable(BitVector.low(blockSize))(strategy)
  }
  
}
