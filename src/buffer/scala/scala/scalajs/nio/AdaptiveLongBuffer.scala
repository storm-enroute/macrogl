package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of read/write in the JS buffer using an arbitrary endianness (fixed at initialization).
 * Significantly slower than the native version of the class, even if the target endianness is the same.
 * Warning: there is no guarantee that the Typed Array view will correctly reflect the content of this buffer (Typed Arrays use the native endianness of the platform).
 */
class AdaptiveLongBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int, cByteOrder: ByteOrder) extends NativeLongBuffer(cMark, cPosition, cLimit, cCapacity, cBuffer, cBufferOffset) {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int, byteOrder: ByteOrder) = this(-1, 0, lim, cap, buffer, bufferOffset, byteOrder)

  override protected val littleEndian: Boolean = cByteOrder == ByteOrder.LITTLE_ENDIAN
  override protected def iGet(index: Int): Long = {
    val (lo, hi) = if (this.littleEndian) {
      (this.dataView.getInt32(index * this.bytes_per_element + 0, this.littleEndian).asInstanceOf[js.Number].intValue,
       this.dataView.getInt32(index * this.bytes_per_element + 4, this.littleEndian).asInstanceOf[js.Number].intValue)
    } else {
      (this.dataView.getInt32(index * this.bytes_per_element + 4, this.littleEndian).asInstanceOf[js.Number].intValue,
       this.dataView.getInt32(index * this.bytes_per_element + 0, this.littleEndian).asInstanceOf[js.Number].intValue)
    }
    Bits.pairIntToLong(hi, lo)
  }
  override protected def iSet(index: Int, value: Long): Unit = {
    val (hi, lo) = Bits.longToPairInt(value)
    if(this.littleEndian) {
      this.dataView.setInt32(index * this.bytes_per_element + 0, lo, this.littleEndian)
      this.dataView.setInt32(index * this.bytes_per_element + 4, hi, this.littleEndian)
    } else {
      this.dataView.setInt32(index * this.bytes_per_element + 4, lo, this.littleEndian)
      this.dataView.setInt32(index * this.bytes_per_element + 0, hi, this.littleEndian)
    }
  }
  override def asReadOnlyBuffer(): AdaptiveLongBuffer = throw new NotImplementedError

  override def compact(): AdaptiveLongBuffer = {
    for (i <- 0 until this.remaining) {
      this.iSet(0 + i, this.iGet(this.position + i))
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark
    this
  }

  override def duplicate(): AdaptiveLongBuffer = {
    new AdaptiveLongBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset, this.cByteOrder)
  }

  override def get(): Long = iGet(this.nextGetIndex)

  override def get(dst: Array[Long]): AdaptiveLongBuffer = this.get(dst, 0, dst.length)

  override def get(dst: Array[Long], offset: Int, length: Int): AdaptiveLongBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.iGet(this.position + i)
    }
    this.position(this.position + length)
    this
  }

  override def get(index: Int): Long = {
    this.iGet(this.checkIndex(index))
  }

  override def order(): ByteOrder = this.cByteOrder

  override def put(f: Long): AdaptiveLongBuffer = {
    this.iSet(this.nextPutIndex, f)
    this
  }

  override def put(src: Array[Long]): AdaptiveLongBuffer = this.put(src, 0, src.length)

  override def put(src: Array[Long], offset: Int, length: Int): AdaptiveLongBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.iSet(this.position + i, src(offset + i))
    }
    this
  }

  override def put(src: LongBuffer): AdaptiveLongBuffer = {
    // TODO Is there an efficient way to do that if the other byteBuffer is backed by another JS array buffer? Didn't find anything related..
    val srcLength = src.remaining
    if (srcLength > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until srcLength) {
      this.iSet(this.position + i, src.get)
    }
    this.position(this.position + srcLength)
    this
  }

  override def put(index: Int, f: Long): AdaptiveLongBuffer = {
    this.iSet(this.checkIndex(index), f)
    this
  }

  override def slice(): AdaptiveLongBuffer = {
    new AdaptiveLongBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element), this.cByteOrder)
  }

  override def toString(): String = "AdaptiveLongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + " endian=" + this.order + "]"
}

object AdaptiveLongBuffer {
  def allocate(capacity: Int): NativeLongBuffer = this.allocate(capacity, ByteOrder.nativeOrder)
  def allocate(capacity: Int, byteOrder: ByteOrder): NativeLongBuffer = byteOrder match {
    case x if (x == ByteOrder.nativeOrder) => NativeLongBuffer.allocate(capacity)
    case ByteOrder.LITTLE_ENDIAN | ByteOrder.BIG_ENDIAN => {
      val jsBuffer = g.ArrayBuffer(capacity * NativeLongBuffer.BYTES_PER_ELEMENT)
      val longBuffer = new AdaptiveLongBuffer(-1, 0, capacity, capacity, jsBuffer, 0, byteOrder)
      longBuffer
    }
    case _ => throw new IllegalArgumentException("Unknown endianness: " + byteOrder)
  }

  def wrap(array: Array[Long]): NativeLongBuffer = this.wrap(array, ByteOrder.nativeOrder)
  def wrap(array: Array[Long], byteOrder: ByteOrder): NativeLongBuffer = wrap(array, 0, array.length, byteOrder)
  def wrap(array: Array[Long], offset: Int, length: Int): NativeLongBuffer = this.wrap(array, offset, length, ByteOrder.nativeOrder)
  def wrap(array: Array[Long], offset: Int, length: Int, byteOrder: ByteOrder): NativeLongBuffer = {
    val longBuffer = this.allocate(length, byteOrder)
    for (i <- 0 until length) {
      longBuffer.put(i, array(i + offset))
    }
    longBuffer
  }
}