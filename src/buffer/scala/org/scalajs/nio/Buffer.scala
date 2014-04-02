package org.scalajs.nio

import scala.scalajs.js

abstract class Buffer extends Object {
  def capacity(): Int
  def clear(): Buffer
  def flip(): Buffer
  def hasRemaining(): Boolean
  def isDirect(): Boolean
  def isReadOnly(): Boolean
  def limit(): Int
  def limit(newLimit: Int): Buffer
  def mark(): Buffer
  def position(): Int
  def position(newPosition: Int): Buffer
  def remaining(): Int
  def reset(): Buffer
  def rewind(): Buffer

  def hasArray(): Boolean
  def array(): Object // optional (throw UnsupportedOperationException if hasArray() is false)
  def arrayOffset(): Int // optional (throw UnsupportedOperationException if hasArray() is false)

  // ScalaJS specific
  // You don't need the offset for Typed Array or Data View, it is taken care internally
  def hasJsArray(): Boolean
  def jsArray(): js.Array[js.Number] // optional (throw UnsupportedOperationException if hasJsArray() is false)

  def hasJsBuffer(): Boolean
  def jsBuffer(): js.Dynamic // optional (throw UnsupportedOperationException if hasJsBuffer() is false)
  def jsBufferOffset(): Int // optional (throw UnsupportedOperationException if hasJsBuffer() is false)
  def jsDataView(): js.Dynamic // optional (throw UnsupportedOperationException if hasJsBuffer() is false)

  override def toString = "Buffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}