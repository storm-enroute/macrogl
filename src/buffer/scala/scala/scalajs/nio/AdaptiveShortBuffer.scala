package scala.scalajs.nio

import scala.scalajs.js

class AdaptiveShortBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: js.Dynamic,
    mBufferOffset: Int, mByteOrder: ByteOrder) extends NativeShortBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Short = {
    this.dataView.getInt16(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].toShort
  }
  override protected def iSet(index: Int, value: Short): Unit = {
    this.dataView.setInt16(index * this.bytes_per_element, value, this.littleEndian)
  }

  override def duplicate(): ShortBuffer = {
    new AdaptiveShortBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
        this.mBuffer, this.mBufferOffset, mByteOrder)
  }
  override def slice(): ShortBuffer = {
    new AdaptiveShortBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
        this.mBufferOffset + (this.mPosition * this.bytes_per_element), mByteOrder)
  }
  override def asReadOnlyBuffer(): ShortBuffer = {
    new ReadOnlyShortBuffer(this.duplicate)
  }
  override def order(): ByteOrder = {
    if (littleEndian)
      LittleEndian
    else
      BigEndian
  }

  override def toString = "AdaptiveShortBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}