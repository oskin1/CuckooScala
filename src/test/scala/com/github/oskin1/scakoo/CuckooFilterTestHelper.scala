package com.github.oskin1.scakoo

import org.scalacheck.Gen

trait CuckooFilterTestHelper {

  val valueGen: Gen[Array[Byte]] = Gen.nonEmptyListOf(Gen.negNum[Byte]).map(_.toArray)

  val immutableFilterGen: Gen[immutable.CuckooFilter[Array[Byte]]] = for {
    entries <- Gen.chooseNum(1, 16)
    powerOfTwo <- Gen.chooseNum(8, 16)
  } yield newImmutableFilter(entries, math.pow(2, powerOfTwo).toInt)

  val mutableFilterGen: Gen[mutable.CuckooFilter[Array[Byte]]] = for {
    entries <- Gen.chooseNum(1, 16)
    powerOfTwo <- Gen.chooseNum(8, 16)
  } yield newMutableFilter(entries, math.pow(2, powerOfTwo).toInt)

  def newImmutableFilter(entriesPerBucket: Int, bucketsQty: Int): immutable.CuckooFilter[Array[Byte]] = {
    import Funnel.byteArrayFunnel
    import TaggingStrategy.MurmurHash3Strategy
    immutable.CuckooFilter(entriesPerBucket, bucketsQty)
  }

  def newMutableFilter(entriesPerBucket: Int, bucketsQty: Int): mutable.CuckooFilter[Array[Byte]] = {
    import Funnel.byteArrayFunnel
    import TaggingStrategy.MurmurHash3Strategy
    mutable.CuckooFilter(entriesPerBucket, bucketsQty)
  }

}
