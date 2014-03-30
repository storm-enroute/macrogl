package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class DoubleBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[DoubleBuffer] {
  def asReadOnlyBuffer(): DoubleBuffer
  def compact(): DoubleBuffer
  def compareTo(that: DoubleBuffer): Int
  def duplicate(): DoubleBuffer
  def equals(ob: Any): Boolean
  def get(): Double
  def get(dst: Array[Double]): DoubleBuffer
  def get(dst: Array[Double], offset: Int, length: Int): DoubleBuffer
  def get(index: Int): Double
  def hashCode(): Int
  def order(): ByteOrder
  def put(f: Double): DoubleBuffer
  def put(src: Array[Double]): DoubleBuffer
  def put(src: Array[Double], offset: Int, length: Int): DoubleBuffer
  def put(src: DoubleBuffer): DoubleBuffer
  def put(index: Int, f: Double): DoubleBuffer
  def slice(): DoubleBuffer
  
  def array(): Array[Double] // optional
  def arrayOffset(): Int // optional
  def hasArray(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}

object DoubleBuffer {
  def allocate(capacity: Int): DoubleBuffer = NativeDoubleBuffer.allocate(capacity)

  def wrap(array: Array[Double]): DoubleBuffer = NativeDoubleBuffer.wrap(array)
  def wrap(array: Array[Double], offset: Int, length: Int): DoubleBuffer = NativeDoubleBuffer.wrap(array, offset, length)
}