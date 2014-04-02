package org.scalajs.nio

abstract class ShortBuffer extends Buffer with TypedBuffer[Short, ShortBuffer] with Comparable[ShortBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Short]): ShortBuffer = this.put(src, 0, src.length)

  override def toString = "ShortBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}

object ShortBuffer {
  def allocate(capacity: Int): ShortBuffer = NativeShortBuffer.allocate(capacity)

  def wrap(array: Array[Short]): ShortBuffer = NativeShortBuffer.wrap(array)
  def wrap(array: Array[Short], offset: Int, length: Int): ShortBuffer = NativeShortBuffer.wrap(array, offset, length)
}

