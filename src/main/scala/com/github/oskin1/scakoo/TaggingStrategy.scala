package com.github.oskin1.scakoo

trait TaggingStrategy[A] { self =>

  def apply(value: A, size: Long): CuckooTag

}
