package scala.scalajs.nio

import scala.scalajs.js

class AdaptiveIntBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: js.Dynamic, mBufferOffset: Int,
    mByteOrder: ByteOrder) extends NativeIntBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Int = {
    this.dataView.getInt32(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].toInt
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