package org.scalajs.nio

abstract class FloatBuffer extends Buffer with TypedBuffer[Float, FloatBuffer] with Comparable[FloatBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Float]): FloatBuffer = this.put(src, 0, src.length)

  override def toString = "FloatBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}

object FloatBuffer {
  def allocate(capacity: Int): FloatBuffer = NativeFloatBuffer.allocate(capacity)

  def wrap(array: Array[Float]): FloatBuffer = NativeFloatBuffer.wrap(array)
  def wrap(array: Array[Float], offset: Int, length: Int): FloatBuffer = NativeFloatBuffer.wrap(array, offset, length)
}

