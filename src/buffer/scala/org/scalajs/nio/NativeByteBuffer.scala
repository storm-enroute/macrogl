package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class NativeByteBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
  protected var mMark: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int, cByteOrder: ByteOrder) extends ByteBuffer
  with TypedBufferBehaviour[Byte, ByteBuffer] with JsNativeBuffer[Byte] {

  protected var littleEndian: Boolean = _
  order(cByteOrder)

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Byte = {
    typedArray(index).toByte
  }
  protected def iSet(index: Int, value: Byte): Unit = {
    typedArray(index) = value
  }
  protected def iCmpElements(v1: Byte, v2: Byte): Int = {
    if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[ByteBuffer] = {
    ob match {
      case bb: ByteBuffer => Some(bb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Byte): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): ByteBuffer = new NativeByteBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
    this.mBuffer, this.mBufferOffset, this.order)
  def slice(): ByteBuffer = new NativeByteBuffer(this.remaining, this.remaining, 0, -1,
    this.mBuffer, this.mBufferOffset + (this.mPosition * this.bytes_per_element), this.order)
  def asReadOnlyBuffer(): ByteBuffer = new ReadOnlyByteBuffer(this.duplicate)

  def order(): ByteOrder = {
    if (this.littleEndian)
      LittleEndian
    else
      BigEndian
  }
  def order(bo: ByteOrder): ByteBuffer = {
    this.littleEndian = bo match {
      case LittleEndian => true
      case BigEndian => false
    }
    this
  }

  override def toString = "NativeByteBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // Completing public methods of ByteBuffer
  def asCharBuffer(): Nothing = throw new NotImplementedError // CharBuffer not implemented
  def asShortBuffer(): ShortBuffer = {
    val shortCapacity = this.capacity / NativeShortBuffer.BYTES_PER_ELEMENT

    if (this.order == ByteOrder.nativeOrder)
      new NativeShortBuffer(shortCapacity, shortCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveShortBuffer(shortCapacity, shortCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asIntBuffer(): IntBuffer = {
    val intCapacity = this.capacity / NativeIntBuffer.BYTES_PER_ELEMENT

    if (this.order == ByteOrder.nativeOrder)
      new NativeIntBuffer(intCapacity, intCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveIntBuffer(intCapacity, intCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asLongBuffer(): LongBuffer = {
    val longCapacity = this.capacity / NativeLongBuffer.BYTES_PER_ELEMENT

    if (this.order == ByteOrder.nativeOrder)
      new NativeLongBuffer(longCapacity, longCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveLongBuffer(longCapacity, longCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asFloatBuffer(): FloatBuffer = {
    val floatCapacity = this.capacity / NativeFloatBuffer.BYTES_PER_ELEMENT

    if (this.order == ByteOrder.nativeOrder)
      new NativeFloatBuffer(floatCapacity, floatCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveFloatBuffer(floatCapacity, floatCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asDoubleBuffer(): DoubleBuffer = {
    val doubleCapacity = this.capacity / NativeDoubleBuffer.BYTES_PER_ELEMENT

    if (this.order == ByteOrder.nativeOrder)
      new NativeDoubleBuffer(doubleCapacity, doubleCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveDoubleBuffer(doubleCapacity, doubleCapacity, 0, -1, this.jsBuffer, this.jsBufferOffset, this.order)
  }

  def getChar(): Char = throw new NotImplementedError // Char?
  def getChar(index: Int): Char = throw new NotImplementedError // Char?
  def getShort(): Short = this.getShort(this.nextIndex(true, NativeShortBuffer.BYTES_PER_ELEMENT))
  def getShort(index: Int): Short = {
    this.checkIndex(index, NativeShortBuffer.BYTES_PER_ELEMENT)
    this.dataView.getInt16(index, this.littleEndian).toShort
  }
  def getInt(): Int = this.getInt(this.nextIndex(true, NativeIntBuffer.BYTES_PER_ELEMENT))
  def getInt(index: Int): Int = {
    this.checkIndex(index, NativeIntBuffer.BYTES_PER_ELEMENT)
    this.dataView.getInt32(index, this.littleEndian).toInt
  }
  def getLong(): Long = this.getLong(this.nextIndex(true, NativeLongBuffer.BYTES_PER_ELEMENT))
  def getLong(index: Int): Long = {
    this.checkIndex(index, NativeLongBuffer.BYTES_PER_ELEMENT)

    val (lower, upper) = {
      if (littleEndian)
        (this.dataView.getInt32(index + 0, this.littleEndian).toInt,
          this.dataView.getInt32(index + 4, this.littleEndian).toInt)
      else
        (this.dataView.getInt32(index + 4, this.littleEndian).toInt,
          this.dataView.getInt32(index + 0, this.littleEndian).toInt)
    }

    Bits.pairIntToLong(upper, lower)
  }
  def getFloat(): Float = this.getFloat(this.nextIndex(true, NativeFloatBuffer.BYTES_PER_ELEMENT))
  def getFloat(index: Int): Float = {
    this.checkIndex(index, NativeFloatBuffer.BYTES_PER_ELEMENT)
    this.dataView.getFloat32(index, this.littleEndian).toFloat
  }
  def getDouble(): Double = this.getDouble(this.nextIndex(true, NativeDoubleBuffer.BYTES_PER_ELEMENT))
  def getDouble(index: Int): Double = {
    this.checkIndex(index, NativeDoubleBuffer.BYTES_PER_ELEMENT)
    this.dataView.getFloat64(index, this.littleEndian).toDouble
  }

  def putChar(value: Char): ByteBuffer = throw new NotImplementedError // Char?
  def putChar(index: Int, value: Char): ByteBuffer = throw new NotImplementedError // Char?
  def putShort(value: Short): ByteBuffer = this.putShort(this.nextIndex(false, NativeShortBuffer.BYTES_PER_ELEMENT), value)
  def putShort(index: Int, value: Short): ByteBuffer = {
    this.checkIndex(index, NativeShortBuffer.BYTES_PER_ELEMENT)
    this.dataView.setInt16(index, value, this.littleEndian)
    this
  }
  def putInt(value: Int): ByteBuffer = this.putInt(this.nextIndex(false, NativeIntBuffer.BYTES_PER_ELEMENT), value)
  def putInt(index: Int, value: Int): ByteBuffer = {
    this.checkIndex(index, NativeIntBuffer.BYTES_PER_ELEMENT)
    this.dataView.setInt32(index, value, this.littleEndian)
    this
  }
  def putLong(value: Long): ByteBuffer = this.putLong(this.nextIndex(false, NativeLongBuffer.BYTES_PER_ELEMENT), value)
  def putLong(index: Int, value: Long): ByteBuffer = {
    this.checkIndex(index, NativeLongBuffer.BYTES_PER_ELEMENT)

    val (upper, lower) = Bits.longToPairInt(value)

    if (littleEndian) {
      this.dataView.setInt32(index + 0, lower, this.littleEndian)
      this.dataView.setInt32(index + 4, upper, this.littleEndian)
    } else {
      this.dataView.setInt32(index + 4, lower, this.littleEndian)
      this.dataView.setInt32(index + 0, upper, this.littleEndian)
    }
    this
  }
  def putFloat(value: Float): ByteBuffer = this.putFloat(this.nextIndex(false, NativeFloatBuffer.BYTES_PER_ELEMENT), value)
  def putFloat(index: Int, value: Float): ByteBuffer = {
    this.checkIndex(index, NativeFloatBuffer.BYTES_PER_ELEMENT)
    this.dataView.asInstanceOf[js.Dynamic].setFloat32(index, value, this.littleEndian)
    //this.dataView.setFloat32(index, value, this.littleEndian) // Incorrect in the new version of scala-dom, correct once it's ok
    this
  }
  def putDouble(value: Double): ByteBuffer = this.putDouble(this.nextIndex(false, NativeDoubleBuffer.BYTES_PER_ELEMENT), value)
  def putDouble(index: Int, value: Double): ByteBuffer = {
    this.checkIndex(index, NativeDoubleBuffer.BYTES_PER_ELEMENT)
    this.dataView.asInstanceOf[js.Dynamic].setFloat64(index, value, this.littleEndian)
    //this.dataView.setFloat64(index, value, this.littleEndian) // Incorrect in the new version of scala-dom, correct once it's ok
    this
  }

  override def put(src: ByteBuffer): ByteBuffer = {
    if (src.hasJsArray) { // optimized version
      val srcLength = src.remaining
      if (srcLength > this.remaining)
        throw new BufferOverflowException

      val srcSlice = src.slice
      val thisSlice = this.slice

      thisSlice.jsArray.set(srcSlice.jsArray)
      this.position(this.position + srcLength)
      this
    } else { // Fall back to generic version
      super.put(src)
    }
  }

  // ScalaJS specifics
  def hasJsArray(): Boolean = true
  protected val typedArray = new dom.Int8Array(mBuffer, mBufferOffset, mCapacity)
  def jsArray(): dom.Int8Array = typedArray

  def jsBuffer(): dom.ArrayBuffer = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: dom.DataView = new dom.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeByteBuffer {
  def allocate(capacity: Int): NativeByteBuffer = {
    val jsBuffer = js.Dynamic.newInstance(g.ArrayBuffer)(capacity).asInstanceOf[dom.ArrayBuffer]
    // Why not native endianness? No idea, ask Oracle!
    val nativeByteBuffer = new NativeByteBuffer(capacity, capacity, 0, -1, jsBuffer, 0, ByteOrder.BIG_ENDIAN)
    nativeByteBuffer
  }
  def allocateDirect(capacity: Int): NativeByteBuffer = allocate(capacity)

  /**
   * WARNING: Behavior is different from the original wrap() methods from java.nio._
   * as the content of the array and the buffer are NOT shared in this implementation (unlike in Java).
   * This is basically just an helper method to create a buffer from an array, not an actual wrapper for an array.
   */
  def wrap(array: Array[Byte]): NativeByteBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Byte], offset: Int, length: Int): NativeByteBuffer = {
    val byteBuffer = allocate(length)
    val internalJsArray = byteBuffer.jsArray
    var i = 0
    while (i < length) {
      internalJsArray(i) = array(i + offset)
      i += 1
    }
    byteBuffer
  }

  // ScalaJS specific methods

  val BYTES_PER_ELEMENT: Int = dom.Int8Array.BYTES_PER_ELEMENT.toInt
}
