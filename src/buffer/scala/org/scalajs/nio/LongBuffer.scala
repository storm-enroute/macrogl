package org.scalajs.nio

import scala.scalajs.js

abstract class LongBuffer extends Buffer with TypedBuffer[Long, LongBuffer] with Comparable[LongBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Long]): LongBuffer = this.put(src, 0, src.length)

  override def toString = "LongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
  
  def array(): Array[Long]
  def jsArray(): js.Object
}

object LongBuffer {
  def allocate(capacity: Int): LongBuffer = NativeLongBuffer.allocate(capacity)

  def wrap(array: Array[Long]): LongBuffer = NativeLongBuffer.wrap(array)
  def wrap(array: Array[Long], offset: Int, length: Int): LongBuffer = NativeLongBuffer.wrap(array, offset, length)
}

