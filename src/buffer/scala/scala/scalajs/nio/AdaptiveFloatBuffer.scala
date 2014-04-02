package scala.scalajs.nio

import scala.scalajs.js

class AdaptiveFloatBuffer(cap: Int, lim: Int, pos: Int, mar: Int, mBuffer: js.Dynamic, mBufferOffset: Int,
    mByteOrder: ByteOrder) extends NativeFloatBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Float = {
    this.dataView.getFloat32(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].toFloat
  }
  override protected def iSet(index: Int, value: Float): Unit = {
    this.dataView.setFloat32(index * this.bytes_per_element, value, this.littleEndian)
  }

  override def duplicate(): FloatBuffer = {
    new AdaptiveFloatBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
        this.mBuffer, this.mBufferOffset, mByteOrder)
  }
  override def slice(): FloatBuffer = {
    new AdaptiveFloatBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
        this.mBufferOffset + (this.mPosition * this.bytes_per_element), mByteOrder)
  }
  override def asReadOnlyBuffer(): FloatBuffer = {
    new ReadOnlyFloatBuffer(this.duplicate)
  }
  override def order(): ByteOrder = {
    if (littleEndian)
      LittleEndian
    else
      BigEndian
  }

  override def toString = "AdaptiveFloatBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}