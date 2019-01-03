package com.github.oskin1.scakoo

import com.google.common.primitives.Ints
import org.scalameter.{Bench, Gen, KeyValue}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._

import scala.collection.immutable

object CuckooFilterBench extends Bench.ForkedTime with CuckooFilterTestHelper {

  private val values: Gen[immutable.IndexedSeq[Array[Byte]]] = for {
    size <- Gen.enumeration("itemsQty")(1000, 4000, 16000)
  } yield (0 until size).map(i => Ints.toByteArray(i * size))

  private val filters: Gen[CuckooFilter[Array[Byte]]] = for {
    buckets <- Gen.enumeration("bucketsQty")(32000, 64000, 128000)
    entries <- Gen.enumeration("entriesQty")(4, 8, 16)
  } yield newFilter(entries, buckets)

  private val testCaseGen = for {
    items <- values
    fls <- filters
  } yield (fls, items)

  private val config = Seq[KeyValue](
    exec.minWarmupRuns -> 10,
    exec.maxWarmupRuns -> 30,
    exec.benchRuns -> 20,
    exec.requireGC -> true
  )

  def benchInsert(filter: CuckooFilter[Array[Byte]], items: Seq[Array[Byte]]): Unit = {
    var fl = filter
    items.foreach(i => fl = fl.insert(i).get)
  }

  def benchLookup(filter: CuckooFilter[Array[Byte]], items: Seq[Array[Byte]]): Unit = {
    items.foreach(filter.lookup)
  }

  performance of "CuckooFilter" in {
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
