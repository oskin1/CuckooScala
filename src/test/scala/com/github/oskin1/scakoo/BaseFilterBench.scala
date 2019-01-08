package com.github.oskin1.scakoo

import com.google.common.primitives.Ints
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.scalameter.{Gen, KeyValue}

trait BaseFilterBench extends CuckooFilterTestHelper {

  protected val values: Gen[IndexedSeq[Array[Byte]]] = for {
    size <- Gen.enumeration("itemsQty")(1000, 4000, 16000)
  } yield (0 until size).map(i => Ints.toByteArray(i * size))

  protected val mutableFilters: Gen[mutable.CuckooFilter[Array[Byte]]] = for {
    buckets <- Gen.enumeration("bucketsQty")(32000, 64000, 128000)
    entries <- Gen.enumeration("entriesQty")(4, 8, 16)
  } yield newMutableFilter(entries, buckets)

  protected val config: Seq[KeyValue] = Seq[KeyValue](
    exec.minWarmupRuns -> 10,
    exec.maxWarmupRuns -> 30,
    exec.benchRuns -> 20,
    exec.requireGC -> true
  )

}
