package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class LongBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[LongBuffer] {
  def asReadOnlyBuffer(): LongBuffer
  def compact(): LongBuffer
  def compareTo(that: LongBuffer): Int
  def duplicate(): LongBuffer
  def equals(ob: Any): Boolean
  def get(): Long
  def get(dst: Array[Long]): LongBuffer
  def get(dst: Array[Long], offset: Int, length: Int): LongBuffer
  def get(index: Int): Long
  def hashCode(): Int
  def order(): ByteOrder
  def put(f: Long): LongBuffer
  def put(src: Array[Long]): LongBuffer
  def put(src: Array[Long], offset: Int, length: Int): LongBuffer
  def put(src: LongBuffer): LongBuffer
  def put(index: Int, f: Long): LongBuffer
  def slice(): LongBuffer
  
  def array(): Array[Long] // optional
  def arrayOffset(): Int // optional
  def hasArray(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}

object LongBuffer {
  def allocate(capacity: Int): LongBuffer = NativeLongBuffer.allocate(capacity)

  def wrap(array: Array[Long]): LongBuffer = NativeLongBuffer.wrap(array)
  def wrap(array: Array[Long], offset: Int, length: Int): LongBuffer = NativeLongBuffer.wrap(array, offset, length)
}