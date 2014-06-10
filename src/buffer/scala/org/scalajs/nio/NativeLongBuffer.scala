package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class NativeLongBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
  protected var mMark: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int) extends LongBuffer
  with TypedBufferBehaviour[Long, LongBuffer] with JsNativeBuffer[Long] {

  protected val littleEndian: Boolean = ByteOrder.nativeOrder == LittleEndian

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Long = {
    val (lo, hi) =
      if (this.littleEndian) {
        (this.typedArray(index * 2 + 0).toInt,
          this.typedArray(index * 2 + 1).toInt)
      } else {
        (this.typedArray(index * 2 + 1).toInt,
          this.typedArray(index * 2 + 0).toInt)
      }
    Bits.pairIntToLong(hi, lo)
  }
  protected def iSet(index: Int, value: Long): Unit = {
    val (hi, lo) = Bits.longToPairInt(value)
    if (this.littleEndian) {
      this.typedArray(index * 2 + 0) = lo
      this.typedArray(index * 2 + 1) = hi
    } else {
      this.typedArray(index * 2 + 1) = lo
      this.typedArray(index * 2 + 0) = hi
    }
  }
  protected def iCmpElements(v1: Long, v2: Long): Int = {
    if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[LongBuffer] = {
    ob match {
      case fb: LongBuffer => Some(fb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Long): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): LongBuffer = new NativeLongBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
    this.mBuffer, this.mBufferOffset)
  def slice(): LongBuffer = new NativeLongBuffer(this.remaining, this.remaining, 0, -1,
    this.mBuffer, this.mBufferOffset + (this.mPosition * this.bytes_per_element))
  def asReadOnlyBuffer(): LongBuffer = new ReadOnlyLongBuffer(this.duplicate)
  def order(): ByteOrder = ByteOrder.nativeOrder

  override def toString = "NativeLongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specifics
  def hasJsArray(): Boolean = false
  protected val typedArray = new dom.Int32Array(mBuffer, mBufferOffset, mCapacity * 2)
  def jsArray(): Nothing = throw new UnsupportedOperationException

  def jsBuffer(): dom.ArrayBuffer = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: dom.DataView = new dom.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeLongBuffer {
  def allocate(capacity: Int): NativeLongBuffer = {
    val jsBuffer = js.Dynamic.newInstance(g.ArrayBuffer)(capacity * BYTES_PER_ELEMENT).asInstanceOf[dom.ArrayBuffer]
    val longBuffer = new NativeLongBuffer(capacity, capacity, 0, -1, jsBuffer, 0)
    longBuffer
  }

  def wrap(array: Array[Long]): NativeLongBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Long], offset: Int, length: Int): NativeLongBuffer = {
    val longBuffer = allocate(length)
    var i = 0
    while (i < length) {
      longBuffer.put(array(i + offset))
      i += 1
    }
    longBuffer.reset
    longBuffer
  }

  val BYTES_PER_ELEMENT: Int = dom.Int32Array.BYTES_PER_ELEMENT.toInt * 2
}