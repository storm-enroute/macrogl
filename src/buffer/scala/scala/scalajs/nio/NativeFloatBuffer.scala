package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

class NativeFloatBuffer(protected var mCapacity: Int, protected var mLimit: Int, protected var mPosition: Int,
    protected var mMark: Int, mBuffer: js.Dynamic, mBufferOffset: Int) extends FloatBuffer
    with TypedBufferBehaviour[Float, FloatBuffer] with JsNativeBuffer[Float] {

  // Completing internal implementation of TypedBufferBehaviour
  protected def iGet(index: Int): Float = {
    typedArray(index).toFloat
  }
  protected def iSet(index: Int, value: Float): Unit = {
    typedArray(index) = value
  }
  protected def iCmpElements(v1: Float, v2: Float): Int = {
    if (v1 == Float.NaN && v2 == Float.NaN) {
      0
    } else if (v1 > v2) {
      +1
    } else if (v1 < v2) {
      -1
    } else {
      0
    }
  }
  protected def toBufferType(ob: Any): Option[FloatBuffer] = {
    ob match {
      case fb: FloatBuffer => Some(fb)
      case _ => None
    }
  }
  protected def contentTypeToInt(value: Float): Int = value.toInt

  // Completing public methods of TypedBufferBehaviour
  def duplicate(): FloatBuffer = new NativeFloatBuffer(this.mCapacity, this.mLimit, this.mPosition, this.mMark,
      this.mBuffer, this.mBufferOffset)
  def slice(): FloatBuffer = new NativeFloatBuffer(this.remaining, this.remaining, 0, -1,
      this.mBuffer, this.mBufferOffset + (this.mPosition * this.bytes_per_element))
  def asReadOnlyBuffer(): FloatBuffer = new ReadOnlyFloatBuffer(this.duplicate)
  def order(): ByteOrder = ByteOrder.nativeOrder

  override def toString = "NativeFloatBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specifics
  def hasJsArray(): Boolean = true
  protected val typedArray = g.Float32Array(mBuffer, mBufferOffset, mCapacity).asInstanceOf[js.Array[js.Number]]
  def jsArray(): js.Array[js.Number] = typedArray

  def jsBuffer(): js.Dynamic = mBuffer
  def jsBufferOffset(): Int = mBufferOffset

  protected val dataView: js.Dynamic = g.DataView(this.mBuffer, this.mBufferOffset, this.mCapacity * this.bytes_per_element)
}

object NativeFloatBuffer {
  def allocate(capacity: Int): NativeFloatBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity * BYTES_PER_ELEMENT)
    val floatBuffer = new NativeFloatBuffer(capacity, capacity, 0, -1, jsBuffer, 0)
    floatBuffer
  }

  def wrap(array: Array[Float]): NativeFloatBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Float], offset: Int, length: Int): NativeFloatBuffer = {
    val floatBuffer = allocate(length)
    val internalJsArray = floatBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    floatBuffer
  }

  val BYTES_PER_ELEMENT: Int = g.Float32Array.BYTES_PER_ELEMENT.asInstanceOf[js.Number].intValue
}