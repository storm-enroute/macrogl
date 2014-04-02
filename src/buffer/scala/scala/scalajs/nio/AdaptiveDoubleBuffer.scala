package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

class AdaptiveDoubleBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: js.Dynamic, mBufferOffset: Int,
    mByteOrder: ByteOrder) extends NativeDoubleBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Double = {
    this.dataView.getDouble64(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].toDouble
  }
  override protected def iSet(index: Int, value: Double): Unit = {
    this.dataView.setDouble64(index * this.bytes_per_element, value, this.littleEndian)
  }

  override def duplicate(): DoubleBuffer = {
    new AdaptiveDoubleBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
        this.mBuffer, this.mBufferOffset, mByteOrder)
  }
  override def slice(): DoubleBuffer = {
    new AdaptiveDoubleBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
        this.mBufferOffset + (this.mPosition * this.bytes_per_element), mByteOrder)
  }
  override def asReadOnlyBuffer(): DoubleBuffer = {
    new ReadOnlyDoubleBuffer(this.duplicate)
  }
  override def order(): ByteOrder = {
    if (littleEndian)
      LittleEndian
    else
      BigEndian
  }

  override def toString = "AdaptiveDoubleBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}

object AdaptiveDoubleBuffer {
  def allocate(capacity: Int): NativeDoubleBuffer = this.allocate(capacity, ByteOrder.nativeOrder)
  def allocate(capacity: Int, byteOrder: ByteOrder): NativeDoubleBuffer = {
    if (byteOrder == ByteOrder.nativeOrder){
      NativeDoubleBuffer.allocate(capacity)
    } else {
      val jsBuffer = g.ArrayBuffer(capacity * NativeDoubleBuffer.BYTES_PER_ELEMENT)
      val doubleBuffer = new AdaptiveDoubleBuffer(capacity, capacity, 0, -1, jsBuffer, 0, byteOrder)
      doubleBuffer
    }
  }

  def wrap(array: Array[Double]): NativeDoubleBuffer = this.wrap(array, ByteOrder.nativeOrder)
  def wrap(array: Array[Double], byteOrder: ByteOrder): NativeDoubleBuffer = wrap(array, 0, array.length, byteOrder)
  def wrap(array: Array[Double], offset: Int, length: Int): NativeDoubleBuffer = this.wrap(array, offset, length, ByteOrder.nativeOrder)
  def wrap(array: Array[Double], offset: Int, length: Int, byteOrder: ByteOrder): NativeDoubleBuffer = {
    val doubleBuffer = this.allocate(length, byteOrder)
    for (i <- 0 until length) {
      doubleBuffer.put(i, array(i + offset))
    }
    doubleBuffer
  }
}