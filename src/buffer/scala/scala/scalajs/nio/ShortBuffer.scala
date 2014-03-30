package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class ShortBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[ShortBuffer] {
  def asReadOnlyBuffer(): ShortBuffer
  def compact(): ShortBuffer
  def compareTo(that: ShortBuffer): Int
  def duplicate(): ShortBuffer
  def equals(ob: Any): Boolean
  def get(): Short
  def get(dst: Array[Short]): ShortBuffer
  def get(dst: Array[Short], offset: Int, length: Int): ShortBuffer
  def get(index: Int): Short
  def hashCode(): Int
  def order(): ByteOrder
  def put(f: Short): ShortBuffer
  def put(src: Array[Short]): ShortBuffer
  def put(src: Array[Short], offset: Int, length: Int): ShortBuffer
  def put(src: ShortBuffer): ShortBuffer
  def put(index: Int, f: Short): ShortBuffer
  def slice(): ShortBuffer
  
  def array(): Array[Short] // optional
  def arrayOffset(): Int // optional
  def hasArray(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}

object ShortBuffer {
  def allocate(capacity: Int): ShortBuffer = ???

  def wrap(array: Array[Short]): ShortBuffer = ???
  def wrap(array: Array[Short], offset: Int, length: Int): ShortBuffer = ???
}