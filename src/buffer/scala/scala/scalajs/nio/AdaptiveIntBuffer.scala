package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of read/write in the JS buffer using an arbitrary endianness (fixed at initialization).
 * Significantly slower than the native version of the class, even if the target endianness is the same.
 * Warning: there is no guarantee that the Typed Array view will correctly reflect the content of this buffer (Typed Arrays use the native endianness of the platform).
 */
class AdaptiveIntBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int, cByteOrder: ByteOrder) extends NativeIntBuffer(cMark, cPosition, cLimit, cCapacity, cBuffer, cBufferOffset) {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int, byteOrder: ByteOrder) = this(-1, 0, lim, cap, buffer, bufferOffset, byteOrder)

  protected val littleEndian: Boolean = cByteOrder == ByteOrder.LITTLE_ENDIAN
  protected def iGet(index: Int): Int = this.dataView.getInt32(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].intValue
  protected def iSet(index: Int, value: Int): Unit = this.dataView.setInt32(index * this.bytes_per_element, value, this.littleEndian)

  override def asReadOnlyBuffer(): AdaptiveIntBuffer = throw new NotImplementedError

  override def compact(): AdaptiveIntBuffer = {
    for (i <- 0 until this.remaining) {
      this.iSet(0 + i, this.iGet(this.position + i))
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark
    this
  }

  override def duplicate(): AdaptiveIntBuffer = {
    new AdaptiveIntBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset, this.cByteOrder)
  }

  override def get(): Int = iGet(this.nextGetIndex)

  override def get(dst: Array[Int]): AdaptiveIntBuffer = this.get(dst, 0, dst.length)

  override def get(dst: Array[Int], offset: Int, length: Int): AdaptiveIntBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.iGet(this.position + i)
    }
    this.position(this.position + length)
    this
  }

  override def get(index: Int): Int = {
    this.iGet(this.checkIndex(index))
  }

  override def order(): ByteOrder = this.cByteOrder

  override def put(f: Int): AdaptiveIntBuffer = {
    this.iSet(this.nextPutIndex, f)
    this
  }

  override def put(src: Array[Int]): AdaptiveIntBuffer = this.put(src, 0, src.length)

  override def put(src: Array[Int], offset: Int, length: Int): AdaptiveIntBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.iSet(this.position + i, src(offset + i))
    }
    this
  }

  override def put(src: IntBuffer): AdaptiveIntBuffer = {
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

  override def put(index: Int, f: Int): AdaptiveIntBuffer = {
    this.iSet(this.checkIndex(index), f)
    this
  }

  override def slice(): AdaptiveIntBuffer = {
    new AdaptiveIntBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element), this.cByteOrder)
  }

  override def toString(): String = "AdaptiveIntBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + " endian=" + this.order + "]"
}

object AdaptiveIntBuffer {
  def allocate(capacity: Int): NativeIntBuffer = this.allocate(capacity, ByteOrder.nativeOrder)
  def allocate(capacity: Int, byteOrder: ByteOrder): NativeIntBuffer = byteOrder match {
    case x if (x == ByteOrder.nativeOrder) => NativeIntBuffer.allocate(capacity)
    case ByteOrder.LITTLE_ENDIAN | ByteOrder.BIG_ENDIAN => {
      val jsBuffer = g.ArrayBuffer(capacity * NativeIntBuffer.BYTES_PER_ELEMENT)
      val intBuffer = new AdaptiveIntBuffer(-1, 0, capacity, capacity, jsBuffer, 0, byteOrder)
      intBuffer
    }
    case _ => throw new IllegalArgumentException("Unknown endianness: " + byteOrder)
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