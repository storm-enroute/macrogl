package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of efficient read/write in the JS buffer using the native endianness of the plaform (use ByteOrder.nativeOrder to discover it).
 * Actually, as JavaScript does not have 64 bits integer, this not really efficient, but it will be as close as we get.
 */
class NativeLongBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int) extends LongBuffer(cMark, cPosition, cLimit, cCapacity) with JsNativeBuffer[Long] {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int) = this(-1, 0, lim, cap, buffer, bufferOffset)

  protected val littleEndian: Boolean = ByteOrder.nativeOrder == ByteOrder.LITTLE_ENDIAN
  protected def iGet(index: Int): Long = {
    val (lo, hi) =
      if (this.littleEndian) {
    	  (this.typedArray(index * 2 + 0).asInstanceOf[js.Number].intValue,
    	   this.typedArray(index * 2 + 1).asInstanceOf[js.Number].intValue)
      } else {
    	  (this.typedArray(index * 2 + 1).asInstanceOf[js.Number].intValue,
    	   this.typedArray(index * 2 + 0).asInstanceOf[js.Number].intValue)
      }
    Bits.pairIntToLong(hi, lo)
  }
  protected def iSet(index: Int, value: Long): Unit = {
    val (hi, lo) = Bits.longToPairInt(value)
    if(this.littleEndian) {
      this.typedArray(index * 2 + 0) = lo
      this.typedArray(index * 2 + 1) = hi
    } else {
      this.typedArray(index * 2 + 1) = lo
      this.typedArray(index * 2 + 0) = hi
    }
  }

  def compareElement(t1: Long, t2: Long): Int = {
    if (t1 < t2)
      -1
    else if (t1 > t2)
      +1
    else
      0
  }

  // java.nio.LongBuffer shared methods

  def asReadOnlyBuffer(): NativeLongBuffer = throw new NotImplementedError
  def compact(): NativeLongBuffer = {
    for (i <- 0 until this.remaining) {
      this.iSet(0 + i, this.iGet(this.position + i))
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark()
    this
  }
  def duplicate(): NativeLongBuffer = new NativeLongBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset)
  def get(): Long = this.iGet(this.nextGetIndex)
  def get(dst: Array[Long]): NativeLongBuffer = this.get(dst, 0, dst.length)
  def get(dst: Array[Long], offset: Int, length: Int): NativeLongBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.iGet(this.position + i)
    }
    this.position(this.position + length)
    this
  }
  def get(index: Int): Long = {
    this.iGet(this.checkIndex(index))
  }
  def order(): ByteOrder = ByteOrder.nativeOrder
  def put(f: Long): NativeLongBuffer = {
    this.iSet(this.nextPutIndex, f)
    this
  }
  def put(src: Array[Long]): NativeLongBuffer = this.put(src, 0, src.length)
  def put(src: Array[Long], offset: Int, length: Int): NativeLongBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.iSet(this.position + i, src(offset + i))
    }
    this
  }
  def put(src: LongBuffer): NativeLongBuffer = {
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
  def put(index: Int, f: Long): NativeLongBuffer = {
    this.iSet(this.checkIndex(index), f)
    this
  }
  def slice(): NativeLongBuffer = {
    new NativeLongBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element))
  }

  def compareTo(that: LongBuffer): Int = {
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
    case that: LongBuffer => {
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

  override def toString(): String = "NativeLongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specific methods
  def jsBuffer(): js.Dynamic = this.cBuffer
  def jsBufferOffset(): Int = this.cBufferOffset

  // Special case for Long: there is not true Typed Array in javascript to handle a 64 bits signed type
  def hasJsArray(): Boolean = false
  def jsArray(): js.Array[js.Number] = throw new UnsupportedOperationException

  // ScalaJS internals
  protected val typedArray: js.Array[js.Number] = g.Int32Array(cBuffer, cBufferOffset, cCapacity * 2).asInstanceOf[js.Array[js.Number]]
}

object NativeLongBuffer {
  def allocate(capacity: Int): NativeLongBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT)
    val longBuffer = new NativeLongBuffer(-1, 0, capacity, capacity, jsBuffer, 0)
    longBuffer
  }

  def wrap(array: Array[Long]): NativeLongBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Long], offset: Int, length: Int): NativeLongBuffer = {
    val longBuffer = allocate(length)
    val internalJsArray = longBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    longBuffer
  }

  val BYTES_PER_ELEMENT: Int = g.Int32Array.BYTES_PER_ELEMENT.asInstanceOf[js.Number].intValue * 2
}
