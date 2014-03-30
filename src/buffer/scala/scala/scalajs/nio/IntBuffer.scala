package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class IntBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[IntBuffer] {
  def asReadOnlyBuffer(): IntBuffer
  def compact(): IntBuffer
  def compareTo(that: IntBuffer): Int
  def duplicate(): IntBuffer
  def equals(ob: Any): Boolean
  def get(): Int
  def get(dst: Array[Int]): IntBuffer
  def get(dst: Array[Int], offset: Int, length: Int): IntBuffer
  def get(index: Int): Int
  def hashCode(): Int
  def order(): ByteOrder
  def put(f: Int): IntBuffer
  def put(src: Array[Int]): IntBuffer
  def put(src: Array[Int], offset: Int, length: Int): IntBuffer
  def put(src: IntBuffer): IntBuffer
  def put(index: Int, f: Int): IntBuffer
  def slice(): IntBuffer
  
  def array(): Array[Int] // optional
  def arrayOffset(): Int // optional
  def hasArray(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}

object IntBuffer {
  def allocate(capacity: Int): IntBuffer = ???

  def wrap(array: Array[Int]): IntBuffer = ???
  def wrap(array: Array[Int], offset: Int, length: Int): IntBuffer = ???
}