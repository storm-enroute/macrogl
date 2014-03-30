package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of efficient read/write in the JS buffer using the native endianness of the plaform (use ByteOrder.nativeOrder to discover it).
 */
class NativeShortBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int) extends ShortBuffer(cMark, cPosition, cLimit, cCapacity) with JsNativeBuffer[Short] {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int) = this(-1, 0, lim, cap, buffer, bufferOffset)

  def compareElement(t1: Short, t2: Short): Int = {
    if (t1 < t2)
      -1
    else if (t1 > t2)
      +1
    else
      0
  }

  // java.nio.ShortBuffer shared methods

  def asReadOnlyBuffer(): NativeShortBuffer = throw new NotImplementedError
  def compact(): NativeShortBuffer = {
    for (i <- 0 until this.remaining) {
      this.typedArray(0 + i) = this.typedArray(this.position + i)
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark()
    this
  }
  def duplicate(): NativeShortBuffer = new NativeShortBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset)
  def get(): Short = this.typedArray(this.nextGetIndex).shortValue
  def get(dst: Array[Short]): NativeShortBuffer = this.get(dst, 0, dst.length)
  def get(dst: Array[Short], offset: Int, length: Int): NativeShortBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.typedArray(this.position + i).shortValue
    }
    this.position(this.position + length)
    this
  }
  def get(index: Int): Short = {
    this.typedArray(this.checkIndex(index)).shortValue
  }
  def order(): ByteOrder = ByteOrder.nativeOrder
  def put(f: Short): NativeShortBuffer = {
    this.typedArray(this.nextPutIndex) = f
    this
  }
  def put(src: Array[Short]): NativeShortBuffer = this.put(src, 0, src.length)
  def put(src: Array[Short], offset: Int, length: Int): NativeShortBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.typedArray(this.position + i) = src(offset + i)
    }
    this
  }
  def put(src: ShortBuffer): NativeShortBuffer = {
    // TODO Is there an efficient way to do that if the other byteBuffer is backed by another JS array buffer? Didn't find anything related..
    val srcLength = src.remaining
    if (srcLength > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until srcLength) {
      this.typedArray(this.position + i) = src.get
    }
    this.position(this.position + srcLength)
    this
  }
  def put(index: Int, f: Short): NativeShortBuffer = {
    this.typedArray(this.checkIndex(index)) = f
    this
  }
  def slice(): NativeShortBuffer = {
    new NativeShortBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element))
  }

  def compareTo(that: ShortBuffer): Int = {
    val length = Math.min(this.remaining, that.remaining)

    val i = this.position
    val j = that.position

    for (k <- 0 until length) {
      val curI = this.get(i + k)
      val curJ = that.get(j + k)

      val cmp = compareElement(curI, curJ)

      if (cmp != 0)
        return cmp
    }

    this.remaining - that.remaining
  }
  override def equals(ob: Any): Boolean = ob match {
    case that: ShortBuffer => {
      if (this.remaining != that.remaining)
        return false
      val length = this.remaining

      val i = this.position
      val j = that.position

      for (k <- 0 until length) {
        val curI = this.get(i + k)
        val curJ = that.get(j + k)

        if (compareElement(curI, curJ) != 0)
          return false
      }

      return true
    }
    case _ => false
  }
  override def hashCode(): Int = {
    var h: Int = 1
    val length = this.remaining

    for (i <- 0 until length) {
      h = 31 * h + this.get(this.position + i).toInt
    }

    (h & 0xFFFFFFFF) // just making just the JavaScript Number (Double) doesn't play a trick on us here
  }

  override def toString(): String = "NativeShortBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specific methods
  def jsBuffer(): js.Dynamic = this.cBuffer
  def jsBufferOffset(): Int = this.cBufferOffset

  def hasJsArray(): Boolean = true
  def jsArray(): js.Array[js.Number] = typedArray

  // ScalaJS internals
  protected val typedArray: js.Array[js.Number] = g.Int16Array(cBuffer, cBufferOffset, cCapacity).asInstanceOf[js.Array[js.Number]]
}

object NativeShortBuffer {
  def allocate(capacity: Int): NativeShortBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT)
    val shortBuffer = new NativeShortBuffer(-1, 0, capacity, capacity, jsBuffer, 0)
    shortBuffer
  }

  def wrap(array: Array[Short]): NativeShortBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Short], offset: Int, length: Int): NativeShortBuffer = {
    val shortBuffer = allocate(length)
    val internalJsArray = shortBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    shortBuffer
  }

  val BYTES_PER_ELEMENT: Int = g.Int16Array.BYTES_PER_ELEMENT.asInstanceOf[js.Number].intValue
}
