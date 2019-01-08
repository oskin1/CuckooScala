# Cuckoo Filter

This repository contains high performance Cuckoo filter implementation for Scala.

The Cuckoo Filter is a probabilistic data structure that supports fast set membership testing. It is very similar to a Bloom filter in that they both are very fast and space efficient. Both the Bloom filter and Cuckoo filter are constructed with some false positive probability.

Cuckoo filters allow to add and remove items dynamically. A Cuckoo filter is based on cuckoo partial hashing. It is essentially a cuckoo hash table storing each items's fingerprint. Cuckoo hash tables can be highly compact, thus a cuckoo filter could use less space than conventional Bloom filters.

Details about the algorithm could be found in original article

["Cuckoo Filter: Better Than Bloom" by Bin Fan, Dave Andersen and Michael Kaminsky](https://www.cs.cmu.edu/~dga/papers/cuckoo-conext2014.pdf)


# Example usage

```scala
import com.github.oskin1.scakoo.mutable
import com.github.oskin1.scakoo.Funnel.intFunnel
import com.github.oskin1.scakoo.TaggingStrategy.MurmurHash3Strategy

object ExampleApp {

  def main(args: Array[String]): Unit = {

    val filter = mutable.CuckooFilter[Int](4, 1024)
    val item = 87

    if (filter.insert(item).isSuccess) println(s"$item inserted successfully")

    println(s"filter contains $item = ${filter.lookup(item)}")

    filter.delete(item)

    println(s"$item removed from filter")

    println(s"filter contains $item = ${filter.lookup(item)}")
  }

}
```
