package com.github.oskin1.scakoo

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class TaggingStrategySpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks {

  private val strategy = TaggingStrategy.MurmurHash3Strategy

  val sinkGen: Gen[Sink] = Gen.nonEmptyListOf(Gen.negNum[Byte]).map(x => Sink(x.toArray))

  property("index could be calculated using alternative index and fingerprint") {
    val indexRange = 1024
    forAll(sinkGen) { sink =>
      val (idx, fp) = strategy.tag(sink, indexRange)
      val altIdx = strategy.altIndex(idx, fp, indexRange)
      strategy.altIndex(altIdx, fp, indexRange) shouldEqual idx
    }
  }

}
