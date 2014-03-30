package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of read/write in the JS buffer using an arbitrary endianness (fixed at initialization).
 * Significantly slower than the native version of the class, even if the target endianness is the same.
 * Warning: there is no guarantee that the Typed Array view will correctly reflect the content of this buffer (Typed Arrays use the native endianness of the platform).
 */
class AdaptiveFloatBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int, cByteOrder: ByteOrder) extends NativeFloatBuffer(cMark, cPosition, cLimit, cCapacity, cBuffer, cBufferOffset) {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int, byteOrder: ByteOrder) = this(-1, 0, lim, cap, buffer, bufferOffset, byteOrder)

  protected val littleEndian: Boolean = cByteOrder == ByteOrder.LITTLE_ENDIAN
  protected def iGet(index: Int): Float = this.dataView.getFloat32(index * this.bytes_per_element, this.littleEndian).asInstanceOf[js.Number].floatValue
  protected def iSet(index: Int, value: Float): Unit = this.dataView.setFloat32(index * this.bytes_per_element, value, this.littleEndian)

  override def asReadOnlyBuffer(): AdaptiveFloatBuffer = throw new NotImplementedError

  override def compact(): AdaptiveFloatBuffer = {
    for (i <- 0 until this.remaining) {
      this.iSet(0 + i, this.iGet(this.position + i))
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark
    this
  }

  override def duplicate(): AdaptiveFloatBuffer = {
    new AdaptiveFloatBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset, this.cByteOrder)
  }

  override def get(): Float = iGet(this.nextGetIndex)

  override def get(dst: Array[Float]): AdaptiveFloatBuffer = this.get(dst, 0, dst.length)

  override def get(dst: Array[Float], offset: Int, length: Int): AdaptiveFloatBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.iGet(this.position + i)
    }
    this.position(this.position + length)
    this
  }

  override def get(index: Int): Float = {
    this.iGet(this.checkIndex(index))
  }

  override def order(): ByteOrder = this.cByteOrder

  override def put(f: Float): AdaptiveFloatBuffer = {
    this.iSet(this.nextPutIndex, f)
    this
  }

  override def put(src: Array[Float]): AdaptiveFloatBuffer = this.put(src, 0, src.length)

  override def put(src: Array[Float], offset: Int, length: Int): AdaptiveFloatBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.iSet(this.position + i, src(offset + i))
    }
    this
  }

  override def put(src: FloatBuffer): AdaptiveFloatBuffer = {
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

  override def put(index: Int, f: Float): AdaptiveFloatBuffer = {
    this.iSet(this.checkIndex(index), f)
    this
  }

  override def slice(): AdaptiveFloatBuffer = {
    new AdaptiveFloatBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element), this.cByteOrder)
  }

  override def toString(): String = "AdaptiveFloatBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + " endian=" + this.order + "]"
}

object AdaptiveFloatBuffer {
  def allocate(capacity: Int): NativeFloatBuffer = this.allocate(capacity, ByteOrder.nativeOrder)
  def allocate(capacity: Int, byteOrder: ByteOrder): NativeFloatBuffer = byteOrder match {
    case x if (x == ByteOrder.nativeOrder) => NativeFloatBuffer.allocate(capacity)
    case ByteOrder.LITTLE_ENDIAN | ByteOrder.BIG_ENDIAN => {
      val jsBuffer = g.ArrayBuffer(capacity * NativeFloatBuffer.BYTES_PER_ELEMENT)
      val floatBuffer = new AdaptiveFloatBuffer(-1, 0, capacity, capacity, jsBuffer, 0, byteOrder)
      floatBuffer
    }
    case _ => throw new IllegalArgumentException("Unknown endianness: " + byteOrder)
  }

  def wrap(array: Array[Float]): NativeFloatBuffer = this.wrap(array, ByteOrder.nativeOrder)
  def wrap(array: Array[Float], byteOrder: ByteOrder): NativeFloatBuffer = wrap(array, 0, array.length, byteOrder)
  def wrap(array: Array[Float], offset: Int, length: Int): NativeFloatBuffer = this.wrap(array, offset, length, ByteOrder.nativeOrder)
  def wrap(array: Array[Float], offset: Int, length: Int, byteOrder: ByteOrder): NativeFloatBuffer = {
    val floatBuffer = this.allocate(length, byteOrder)
    for (i <- 0 until length) {
      floatBuffer.put(i, array(i + offset))
    }
    floatBuffer
  }
}