package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class NativeShortBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
    protected var mMark: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int) extends ShortBuffer
    with TypedBufferBehaviour[Short, ShortBuffer] with JsNativeBuffer[Short] {

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Short = {
    typedArray(index).toShort
  }
  protected def iSet(index: Int, value: Short): Unit = {
    typedArray(index) = value
  }
  protected def iCmpElements(v1: Short, v2: Short): Int = {
    if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[ShortBuffer] = {
    ob match {
      case fb: ShortBuffer => Some(fb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Short): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): ShortBuffer = new NativeShortBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
      this.mBuffer, this.mBufferOffset)
  def slice(): ShortBuffer = new NativeShortBuffer(this.remaining, this.remaining, 0, -1,
      this.mBuffer, this.mBufferOffset + (this.mPosition * this.bytes_per_element))
  def asReadOnlyBuffer(): ShortBuffer = new ReadOnlyShortBuffer(this.duplicate)
  def order(): ByteOrder = ByteOrder.nativeOrder

  override def toString = "NativeShortBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  override def put(src: ShortBuffer): ShortBuffer = {
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
  protected val typedArray = new dom.Int16Array(mBuffer, mBufferOffset, mCapacity)
  def jsArray(): dom.Int16Array = typedArray

  def jsBuffer(): dom.ArrayBuffer = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: dom.DataView = new dom.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeShortBuffer {
  def allocate(capacity: Int): NativeShortBuffer = {
    val jsBuffer = js.Dynamic.newInstance(g.ArrayBuffer)(capacity * BYTES_PER_ELEMENT).asInstanceOf[dom.ArrayBuffer]
    val shortBuffer = new NativeShortBuffer(capacity, capacity, 0, -1, jsBuffer, 0)
    shortBuffer
  }

  def wrap(array: Array[Short]): NativeShortBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Short], offset: Int, length: Int): NativeShortBuffer = {
    val shortBuffer = allocate(length)
    val internalJsArray = shortBuffer.jsArray
    var i = 0
    while (i < length) {
      internalJsArray(i) = array(i + offset)
      i += 1
    }
    shortBuffer
  }

  val BYTES_PER_ELEMENT: Int = dom.Int16Array.BYTES_PER_ELEMENT.toInt
}