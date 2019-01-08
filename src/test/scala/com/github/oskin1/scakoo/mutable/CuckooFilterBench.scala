package com.github.oskin1.scakoo.mutable

import com.github.oskin1.scakoo.BaseFilterBench
import org.scalameter.picklers.Implicits._
import org.scalameter.{Bench, Gen}

object CuckooFilterBench extends Bench.ForkedTime with BaseFilterBench {

  val filters: Gen[CuckooFilter[Array[Byte]]] = for {
    buckets <- Gen.enumeration("bucketsQty")(32000, 64000, 128000)
    entries <- Gen.enumeration("entriesQty")(4, 8, 16)
  } yield newMutableFilter(entries, buckets)

  val testCaseGen: Gen[(CuckooFilter[Array[Byte]], IndexedSeq[Array[Byte]])] = for {
    items <- values
    fls <- filters
  } yield (fls, items)

  def benchInsert(filter: CuckooFilter[Array[Byte]], items: Seq[Array[Byte]]): Unit = {
    items.foreach(filter.insert(_).get)
  }

  def benchLookup(filter: CuckooFilter[Array[Byte]], items: Seq[Array[Byte]]): Unit = {
    items.foreach(filter.lookup)
  }

  performance of "mutable.CuckooFilter" in {
    performance of "insert" in {
      using(testCaseGen) config(config: _*) in {
        case (filter, items) => benchInsert(filter, items)
      }
    }
    performance of "lookup" in {
      using(testCaseGen) config(config: _*) in {
        case (filter, items) => benchLookup(filter, items)
      }
    }
  }

}
