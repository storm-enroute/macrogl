package org.scalajs.nio

import org.scalajs.dom

abstract class DoubleBuffer extends Buffer with TypedBuffer[Double, DoubleBuffer] with Comparable[DoubleBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Double]): DoubleBuffer = this.put(src, 0, src.length)

  override def toString = "DoubleBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
  
  def array(): Array[Double]
  def jsArray(): dom.Float64Array
}

object DoubleBuffer {
  def allocate(capacity: Int): DoubleBuffer = NativeDoubleBuffer.allocate(capacity)

  def wrap(array: Array[Double]): DoubleBuffer = NativeDoubleBuffer.wrap(array)
  def wrap(array: Array[Double], offset: Int, length: Int): DoubleBuffer = NativeDoubleBuffer.wrap(array, offset, length)
}

