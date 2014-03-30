package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Capable of efficient read/write in the JS buffer using the native endianness of the plaform (use ByteOrder.nativeOrder to discover it).
 */
class NativeDoubleBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int) extends DoubleBuffer(cMark, cPosition, cLimit, cCapacity) with JsNativeBuffer[Double] {
  def this(lim: Int, cap: Int, buffer: js.Dynamic, bufferOffset: Int) = this(-1, 0, lim, cap, buffer, bufferOffset)

  def compareElement(t1: Double, t2: Double): Int = {
    if (t1 == Double.NaN && t2 == Double.NaN) {
      0
    } else {
      if (t1 < t2)
        -1
      else if (t1 > t2)
        +1
      else
        0
    }
  }

  // java.nio.DoubleBuffer shared methods

  def asReadOnlyBuffer(): NativeDoubleBuffer = throw new NotImplementedError
  def compact(): NativeDoubleBuffer = {
    for (i <- 0 until this.remaining) {
      this.typedArray(0 + i) = this.typedArray(this.position + i)
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark()
    this
  }
  def duplicate(): NativeDoubleBuffer = new NativeDoubleBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset)
  def get(): Double = this.typedArray(this.nextGetIndex).doubleValue
  def get(dst: Array[Double]): NativeDoubleBuffer = this.get(dst, 0, dst.length)
  def get(dst: Array[Double], offset: Int, length: Int): NativeDoubleBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.typedArray(this.position + i).doubleValue
    }
    this.position(this.position + length)
    this
  }
  def get(index: Int): Double = {
    this.typedArray(this.checkIndex(index)).doubleValue
  }
  def order(): ByteOrder = ByteOrder.nativeOrder
  def put(f: Double): NativeDoubleBuffer = {
    this.typedArray(this.nextPutIndex) = f
    this
  }
  def put(src: Array[Double]): NativeDoubleBuffer = this.put(src, 0, src.length)
  def put(src: Array[Double], offset: Int, length: Int): NativeDoubleBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.typedArray(this.position + i) = src(offset + i)
    }
    this
  }
  def put(src: DoubleBuffer): NativeDoubleBuffer = {
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
  def put(index: Int, f: Double): NativeDoubleBuffer = {
    this.typedArray(this.checkIndex(index)) = f
    this
  }
  def slice(): NativeDoubleBuffer = {
    new NativeDoubleBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + (this.position * this.bytes_per_element))
  }

  def compareTo(that: DoubleBuffer): Int = {
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
    case that: DoubleBuffer => {
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

  override def toString(): String = "NativeDoubleBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specific methods
  def jsBuffer(): js.Dynamic = this.cBuffer
  def jsBufferOffset(): Int = this.cBufferOffset

  def hasJsArray(): Boolean = true
  def jsArray(): js.Array[js.Number] = typedArray

  // ScalaJS internals
  protected val typedArray: js.Array[js.Number] = g.Float64Array(cBuffer, cBufferOffset, cCapacity).asInstanceOf[js.Array[js.Number]]
}

object NativeDoubleBuffer {
  def allocate(capacity: Int): NativeDoubleBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT)
    val doubleBuffer = new NativeDoubleBuffer(-1, 0, capacity, capacity, jsBuffer, 0)
    doubleBuffer
  }

  def wrap(array: Array[Double]): NativeDoubleBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Double], offset: Int, length: Int): NativeDoubleBuffer = {
    val doubleBuffer = allocate(length)
    val internalJsArray = doubleBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    doubleBuffer
  }

  val BYTES_PER_ELEMENT: Int = g.Float64Array.BYTES_PER_ELEMENT.asInstanceOf[js.Number].intValue
}
