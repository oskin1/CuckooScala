package com.github.oskin1.scakoo

import org.scalatest.{Matchers, PropSpec}

class HashTableSpec extends PropSpec with Matchers {

  val tableSize = 10

  property("find empty entry on (all entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.emptyEntry(i) shouldBe 0
    }
  }

  property("find empty entry on (some entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.insert(i, 1).emptyEntry(i) shouldBe 1
    }
  }

  property("find empty entry on (none entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.insert(i, 87).insert(i, 87).emptyEntry(i) shouldBe -1
    }
  }

  property("check for empty entry (all entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.isVacantBucket(i) shouldBe true
    }
  }

  property("check for empty entry (some entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.insert(i, 87).isVacantBucket(i) shouldBe true
    }
  }

  property("check for empty entry (none entries are empty in the bucket)") {
    val table = HashTable(2, tableSize)
    (0 until tableSize).foreach { i =>
      table.insert(i, 87).insert(i, 87).isVacantBucket(i) shouldBe false
    }
  }

}
