package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class NativeIntBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
    protected var mMark: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int) extends IntBuffer
    with TypedBufferBehaviour[Int, IntBuffer] with JsNativeBuffer[Int] {

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Int = {
    typedArray(index).toInt
  }
  protected def iSet(index: Int, value: Int): Unit = {
    typedArray(index) = value
  }
  protected def iCmpElements(v1: Int, v2: Int): Int = {
    if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[IntBuffer] = {
    ob match {
      case fb: IntBuffer => Some(fb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Int): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): IntBuffer = new NativeIntBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
      this.mBuffer, this.mBufferOffset)
  def slice(): IntBuffer = new NativeIntBuffer(this.remaining, this.remaining, 0, -1, this.mBuffer,
      this.mBufferOffset + (this.mPosition * this.bytes_per_element))
  def asReadOnlyBuffer(): IntBuffer = new ReadOnlyIntBuffer(this.duplicate)
  def order(): ByteOrder = ByteOrder.nativeOrder

  override def toString = "NativeIntBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specifics
  def hasJsArray(): Boolean = true
  protected val typedArray = new dom.Int32Array(mBuffer, mBufferOffset, mCapacity)
  def jsArray(): dom.Int32Array = typedArray

  def jsBuffer(): dom.ArrayBuffer = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: dom.DataView = new dom.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeIntBuffer {
  def allocate(capacity: Int): NativeIntBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT).asInstanceOf[dom.ArrayBuffer]
    val intBuffer = new NativeIntBuffer(capacity, capacity, 0, -1, jsBuffer, 0)
    intBuffer
  }

  def wrap(array: Array[Int]): NativeIntBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Int], offset: Int, length: Int): NativeIntBuffer = {
    val intBuffer = allocate(length)
    val internalJsArray = intBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    intBuffer
  }

  val BYTES_PER_ELEMENT: Int = dom.Int32Array.BYTES_PER_ELEMENT.toInt
}