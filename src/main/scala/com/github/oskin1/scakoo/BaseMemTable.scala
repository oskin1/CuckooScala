package com.github.oskin1.scakoo

trait BaseMemTable extends Serializable {

  val entriesPerBucket: Int

  /** Find vacant entry in the bucket.
    * @return - vacant entry index in the `idx`th bucket
    */
  def emptyEntry(idx: Int): Int

  /** Find index of the entry in the `bucketIdx`th bucket were specific `value` is located.
    */
  def entryIndex(bucketIdx: Int, value: Byte): Int

  /** Read entry from the `entryIdx`th entry in `bucketIdx`th bucket.
    */
  def readEntry(bucketIdx: Int, entryIdx: Int): Byte

  def capacity: Int

  def isVacantBucket(idx: Int): Boolean = emptyEntry(idx) != -1

  def containsEntry(bucketIdx: Int, value: Byte): Boolean = entryIndex(bucketIdx, value) != -1

  def numBuckets: Int = capacity / entriesPerBucket

}
