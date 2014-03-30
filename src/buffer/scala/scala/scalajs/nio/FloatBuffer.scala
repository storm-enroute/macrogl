package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class FloatBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[FloatBuffer] {
  def asReadOnlyBuffer(): FloatBuffer
  def compact(): FloatBuffer
  def compareTo(that: FloatBuffer): Int
  def duplicate(): FloatBuffer
  def equals(ob: Any): Boolean
  def get(): Float
  def get(dst: Array[Float]): FloatBuffer
  def get(dst: Array[Float], offset: Int, length: Int): FloatBuffer
  def get(index: Int): Float
  def hashCode(): Int
  def order(): ByteOrder
  def put(f: Float): FloatBuffer
  def put(src: Array[Float]): FloatBuffer
  def put(src: Array[Float], offset: Int, length: Int): FloatBuffer
  def put(src: FloatBuffer): FloatBuffer
  def put(index: Int, f: Float): FloatBuffer
  def slice(): FloatBuffer
  
  def array(): Array[Float] // optional
  def arrayOffset(): Int // optional
  def hasArray(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}

object FloatBuffer {
  def allocate(capacity: Int): FloatBuffer = NativeFloatBuffer.allocate(capacity)

  def wrap(array: Array[Float]): FloatBuffer = NativeFloatBuffer.wrap(array)
  def wrap(array: Array[Float], offset: Int, length: Int): FloatBuffer = NativeFloatBuffer.wrap(array, offset, length)
}