package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

class NativeDoubleBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
    protected var mMark: Int, mBuffer: dom.ArrayBuffer, mBufferOffset: Int) extends DoubleBuffer
    with TypedBufferBehaviour[Double, DoubleBuffer] with JsNativeBuffer[Double] {

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Double = {
    typedArray(index).toDouble
  }
  protected def iSet(index: Int, value: Double): Unit = {
    typedArray(index) = value
  }
  protected def iCmpElements(v1: Double, v2: Double): Int = {
    if (v1 == Double.NaN && v2 == Double.NaN) {
      0
    } else if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[DoubleBuffer] = {
    ob match {
      case fb: DoubleBuffer => Some(fb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Double): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): DoubleBuffer = new NativeDoubleBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
      this.mBuffer, this.mBufferOffset)
  def slice(): DoubleBuffer = new NativeDoubleBuffer(this.remaining, this.remaining, 0, -1,
      this.mBuffer, this.mBufferOffset + (this.mPosition * this.bytes_per_element))
  def asReadOnlyBuffer(): DoubleBuffer = new ReadOnlyDoubleBuffer(this.duplicate)
  def order(): ByteOrder = ByteOrder.nativeOrder

  override def toString = "NativeDoubleBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specifics
  def hasJsArray(): Boolean = true
  protected val typedArray = new dom.Float64Array(mBuffer, mBufferOffset, mCapacity)
  def jsArray(): dom.Float64Array = typedArray

  def jsBuffer(): dom.ArrayBuffer = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: dom.DataView = new dom.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeDoubleBuffer {
  def allocate(capacity: Int): NativeDoubleBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT).asInstanceOf[dom.ArrayBuffer]
    val doubleBuffer = new NativeDoubleBuffer(capacity, capacity, 0, -1, jsBuffer, 0)
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

  val BYTES_PER_ELEMENT: Int = dom.Float64Array.BYTES_PER_ELEMENT.toInt
}