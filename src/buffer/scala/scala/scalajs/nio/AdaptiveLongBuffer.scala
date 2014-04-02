package scala.scalajs.nio

import scala.scalajs.js

class AdaptiveLongBuffer(cap: Int, lim: Int, pos: Int, mar: Int,mBuffer: js.Dynamic,
    mBufferOffset: Int, mByteOrder: ByteOrder) extends NativeLongBuffer(cap, lim, pos, mar, mBuffer, mBufferOffset) {
  override protected val littleEndian: Boolean = mByteOrder == LittleEndian

  override protected def iGet(index: Int): Long = {
    val (lo, hi) =
      if (this.littleEndian) {
        (this.dataView.getInt32(index * 2 + 0, this.littleEndian).asInstanceOf[js.Number].toInt,
         this.dataView.getInt32(index * 2 + 4, this.littleEndian).asInstanceOf[js.Number].toInt)
      } else {
        (this.dataView.getInt32(index * 2 + 4, this.littleEndian).asInstanceOf[js.Number].toInt,
         this.dataView.getInt32(index * 2 + 0, this.littleEndian).asInstanceOf[js.Number].toInt)
      }
    Bits.pairIntToLong(hi, lo)
  }
  override protected def iSet(index: Int, value: Long): Unit = {
    val (hi, lo) = Bits.longToPairInt(value)
    if (this.littleEndian) {
      this.dataView.setInt32(index * 2 + 0, lo, this.littleEndian)
      this.dataView.setInt32(index * 2 + 4, hi, this.littleEndian)
    } else {
      this.dataView.setInt32(index * 2 + 4, lo, this.littleEndian)
      this.dataView.setInt32(index * 2 + 0, hi, this.littleEndian)
    }
  }

  override def duplicate(): LongBuffer = {
    new AdaptiveLongBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
        this.mBuffer, this.mBufferOffset, mByteOrder)
  }
  override def slice(): LongBuffer = {
    new AdaptiveLongBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
        this.mBufferOffset + (this.mPosition * this.bytes_per_element), mByteOrder)
  }
  override def asReadOnlyBuffer(): LongBuffer = {
    new ReadOnlyLongBuffer(this.duplicate)
  }
  override def order(): ByteOrder = {
    if (littleEndian)
      LittleEndian
    else
      BigEndian
  }

  override def toString = "AdaptiveLongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}