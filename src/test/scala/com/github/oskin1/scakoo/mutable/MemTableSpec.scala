package com.github.oskin1.scakoo.mutable

import org.scalatest.{Matchers, PropSpec}

class MemTableSpec extends PropSpec with Matchers {

  val tableSize = 100

  property("find empty entry on (all entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.emptyEntry(i) shouldBe 0
    }
  }

  property("find empty entry on (some entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.update(i, 0, 1)
      table.emptyEntry(i) shouldBe 1
    }
  }

  property("find empty entry on (none entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.update(i, 0, 87)
      table.update(i, 1, 97)
      table.emptyEntry(i) shouldBe -1
    }
  }

  property("check for empty entry (all entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.isVacantBucket(i) shouldBe true
    }
  }

  property("check for empty entry (some entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.update(i, 0, 87)
      table.isVacantBucket(i) shouldBe true
    }
  }

  property("check for empty entry (none entries are empty in the bucket)") {
    val table = MemTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.update(i, 0, 87)
      table.update(i, 1, 97)
      table.isVacantBucket(i) shouldBe false
    }
  }

}
