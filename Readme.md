# Cuckoo Filter

This repository contains high performance Cuckoo filter implementation for Scala.

The Cuckoo Filter is a probabilistic data structure that supports fast set membership testing. It is very similar to a Bloom filter in that they both are very fast and space efficient. Both the Bloom filter and Cuckoo filter are constructed with some false positive probability.

Cuckoo filters allow to add and remove items dynamically. A Cuckoo filter is based on cuckoo partial hashing. It is essentially a cuckoo hash table storing each items's fingerprint. Cuckoo hash tables can be highly compact, thus a cuckoo filter could use less space than conventional Bloom filters.

Details about the algorithm could be found in original article

["Cuckoo Filter: Better Than Bloom" by Bin Fan, Dave Andersen and Michael Kaminsky](https://www.cs.cmu.edu/~dga/papers/cuckoo-conext2014.pdf)


# Example usage

```scala
import Funnel.intFunnel

object ExampleUsage {

  def main(args: Array[String]): Unit = {
  
    implicit val strategy: TaggingStrategy = MurmurHash3Strategy

    var filter = CuckooFilter[Int](4, 1024)
    val item = 87

    filter.insert(item) match {
      case scala.util.Success(updated) =>
        println(s"$item inserted successfully")
        filter = updated
      case scala.util.Failure(exception) =>
        println(exception.getCause)
    }

    println(s"filter contains $item = ${filter.lookup(item)}")

    filter = filter.delete(item)

    println(s"$item removed from filter")

    println(s"filter contains $item = ${filter.lookup(item)}")
  }

}
```
