package com.github.oskin1.scakoo

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class CuckooFilterSpec extends PropSpec
  with Matchers
  with GeneratorDrivenPropertyChecks
  with CuckooFilterTestHelper {

  property("lookup") {
    forAll(filterGen) { f =>
      var filter = f
      forAll(valueGen) { v =>
        filter = filter.insert(v).get
        filter.lookup(v) shouldBe true
      }
    }
  }

  property("delete") {
    forAll(filterGen) { f =>
      var filter = f
      forAll(valueGen) { v =>
        filter = filter.insert(v).get.delete(v)
        filter.lookup(v) shouldBe false
      }
    }
  }

  property("should reach load factor >= 95%") {
    var filter = newFilter(4, 4096)
    def loop(): Unit = {
      val r = filter.insertUnique(valueGen.sample.get)
      if (r.isSuccess) {
        filter = r.get
        loop()
      }
    }
    loop()
    filter.loadFactor >= 0.95 shouldBe true
  }

}
