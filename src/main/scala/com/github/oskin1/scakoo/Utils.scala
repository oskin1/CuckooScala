package com.github.oskin1.scakoo

import java.lang.Integer.numberOfLeadingZeros

object Utils {

  /** Returns a power of two >= `target`.
    */
  def nextPositivePowerOfTwo(target: Int): Int = 1 << -numberOfLeadingZeros(target - 1)

}
