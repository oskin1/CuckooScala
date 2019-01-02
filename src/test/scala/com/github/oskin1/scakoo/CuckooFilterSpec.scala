package com.github.oskin1.scakoo

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class CuckooFilterSpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks {

  val valueGen: Gen[Array[Byte]] = Gen.nonEmptyListOf(Gen.negNum[Byte]).map(_.toArray)

  val filterGen: Gen[CuckooFilter[Array[Byte]]] = for {
    entries <- Gen.chooseNum(1, 16)
    powerOfTwo <- Gen.chooseNum(8, 16)
  } yield newFilter(entries, math.pow(2, powerOfTwo).toLong)

  def newFilter(entriesPerBucket: Int, bucketsQty: Long): CuckooFilter[Array[Byte]] = {
    import Funnel.byteArrayFunnel
    implicit val strategy: TaggingStrategy = MurmurHash3Strategy
    CuckooFilter(entriesPerBucket, bucketsQty)
  }

  property("insert and lookup") {
    forAll(filterGen) { f =>
      var filter = f
      forAll(valueGen) { v =>
        filter = filter.insert(v).get
        filter.lookup(v) shouldBe true
      }
    }
  }

}