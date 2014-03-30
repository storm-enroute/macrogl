package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

/**
 * Currently unsupported features:
 * - ReadOnly
 * - Direct
 * - Array (& array offset)
 */
trait JsNativeBuffer[T] {
  self:Buffer =>
  
  // Normally abstract, but in our case, we already know the deal
  def array(): Nothing = throw new UnsupportedOperationException // optional
  def arrayOffset(): Int = throw new UnsupportedOperationException // optional
  def hasArray(): Boolean = false
  def isDirect(): Boolean = true
  def isReadOnly(): Boolean = false

  // ScalaJS specific methods
  def jsBuffer(): js.Dynamic
  def jsBufferOffset(): Int

  def jsDataView(): js.Dynamic = dataView
  protected val dataView: js.Dynamic = g.DataView(JsNativeBuffer.this.jsBuffer, JsNativeBuffer.this.jsBufferOffset, JsNativeBuffer.this.capacity * JsNativeBuffer.this.bytes_per_element)
  
  def bytes_per_element:Int = JsNativeBuffer.this match {
    case b:ByteBuffer => NativeByteBuffer.BYTES_PER_ELEMENT
    case b:ShortBuffer => NativeShortBuffer.BYTES_PER_ELEMENT
    case b:IntBuffer => NativeIntBuffer.BYTES_PER_ELEMENT
    case b:LongBuffer => NativeLongBuffer.BYTES_PER_ELEMENT
    case b:FloatBuffer => NativeFloatBuffer.BYTES_PER_ELEMENT
    case b:DoubleBuffer => NativeDoubleBuffer.BYTES_PER_ELEMENT
    case _ => throw new RuntimeException("Not available for this type of buffer")
  }
  
  // abstract
  def hasJsArray(): Boolean
  def jsArray(): js.Array[js.Number]
}