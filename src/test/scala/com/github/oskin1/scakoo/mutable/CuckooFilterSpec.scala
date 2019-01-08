package com.github.oskin1.scakoo.mutable

import com.github.oskin1.scakoo.CuckooFilterTestHelper
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class CuckooFilterSpec extends PropSpec
  with Matchers
  with GeneratorDrivenPropertyChecks
  with CuckooFilterTestHelper {

  property("lookup") {
    forAll(mutableFilterGen) { f =>
      forAll(valueGen) { v =>
        f.insert(v).get
        f.lookup(v) shouldBe true
      }
    }
  }

  property("delete") {
    forAll(mutableFilterGen) { f =>
      forAll(valueGen) { v =>
        f.insert(v).get
        f.delete(v)
        f.lookup(v) shouldBe false
      }
    }
  }

  property("should reach load factor >= 95%") {
    val filter = newMutableFilter(4, 4096)
    def loop(): Unit = {
      if (filter.insertUnique(valueGen.sample.get).isSuccess) {
        loop()
      }
    }
    loop()
    filter.loadFactor >= 0.95 shouldBe true
  }

}
