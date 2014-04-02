package scala.scalajs.nio

import scala.scalajs.js

class AdaptiveDoubleBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: js.Dynamic, mBufferOffset: Int,
    mByteOrder: ByteOrder) extends NativeDoubleBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Double = {
    this.dataView.getFloat64(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].toDouble
  }
  override protected def iSet(index: Int, value: Double): Unit = {
    this.dataView.setFloat64(index * this.bytes_per_element, value, this.littleEndian)
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