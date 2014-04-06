package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class AdaptiveIntBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int,
    mByteOrder: ByteOrder) extends NativeIntBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Int = {
    this.dataView.getInt32(index * this.bytes_per_element, this.littleEndian).toInt
  }
  override protected def iSet(index: Int, value: Int): Unit = {
    this.dataView.setInt32(index * this.bytes_per_element, value, this.littleEndian)
  }

  override def duplicate(): IntBuffer = {
    new AdaptiveIntBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
        this.mBuffer, this.mBufferOffset, mByteOrder)
  }
  override def slice(): IntBuffer = {
    new AdaptiveIntBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
        this.mBufferOffset + (this.mPosition * this.bytes_per_element), mByteOrder)
  }
  override def asReadOnlyBuffer(): IntBuffer = {
    new ReadOnlyIntBuffer(this.duplicate)
  }
  override def order(): ByteOrder = {
    if (littleEndian)
      LittleEndian
    else
      BigEndian
  }

  override def toString = "AdaptiveIntBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}

object AdaptiveIntBuffer {
  def allocate(capacity: Int): NativeIntBuffer = this.allocate(capacity, ByteOrder.nativeOrder)
  def allocate(capacity: Int, byteOrder: ByteOrder): NativeIntBuffer = {
    if (byteOrder == ByteOrder.nativeOrder){
      NativeIntBuffer.allocate(capacity)
    } else {
      val jsBuffer = g.ArrayBuffer(capacity * NativeIntBuffer.BYTES_PER_ELEMENT).asInstanceOf[dom.ArrayBuffer]
      val intBuffer = new AdaptiveIntBuffer(capacity, capacity, 0, -1, jsBuffer, 0, byteOrder)
      intBuffer
    }
  }

  def wrap(array: Array[Int]): NativeIntBuffer = this.wrap(array, ByteOrder.nativeOrder)
  def wrap(array: Array[Int], byteOrder: ByteOrder): NativeIntBuffer = wrap(array, 0, array.length, byteOrder)
  def wrap(array: Array[Int], offset: Int, length: Int): NativeIntBuffer = this.wrap(array, offset, length, ByteOrder.nativeOrder)
  def wrap(array: Array[Int], offset: Int, length: Int, byteOrder: ByteOrder): NativeIntBuffer = {
    val intBuffer = this.allocate(length, byteOrder)
    for (i <- 0 until length) {
      intBuffer.put(i, array(i + offset))
    }
    intBuffer
  }
}