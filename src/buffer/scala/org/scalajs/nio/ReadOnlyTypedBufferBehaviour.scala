package org.scalajs.nio

import scala.scalajs.js
import org.scalajs.dom

trait ReadOnlyTypedBufferBehaviour[ContentType <: AnyVal, BufferType <: TypedBuffer[ContentType, BufferType]] {
  self: BufferType =>

  protected val internalBuffer: BufferType

  def hasArray(): Boolean = false
  def array(): Array[ContentType] = throw new ReadOnlyBufferException
  def arrayOffset(): Int = throw new ReadOnlyBufferException

  def capacity(): Int = internalBuffer.capacity
  def clear(): BufferType = {
    internalBuffer.clear
    this
  }
  def flip(): BufferType = {
    internalBuffer.flip
    this
  }
  def hasRemaining(): Boolean = internalBuffer.hasRemaining
  def isDirect(): Boolean = internalBuffer.isDirect
  def isReadOnly(): Boolean = true
  def limit(): Int = internalBuffer.limit
  def limit(newLimit: Int): BufferType = {
    internalBuffer.limit(newLimit)
    this
  }
  def mark(): BufferType = {
    internalBuffer.mark
    this
  }
  def position(): Int = internalBuffer.position
  def position(newPosition: Int): BufferType = {
    internalBuffer.position(newPosition)
    this
  }
  def remaining(): Int = internalBuffer.remaining
  def reset(): BufferType = {
    internalBuffer.reset
    this
  }
  def rewind(): BufferType = {
    internalBuffer.rewind
    this
  }

  override def equals(ob: Any): Boolean = internalBuffer.equals(ob)
  def compareTo(that: BufferType): Int = internalBuffer.compareTo(that)
  override def hashCode(): Int = internalBuffer.hashCode()

  def order(): ByteOrder = internalBuffer.order

  def get(): ContentType = internalBuffer.get
  def get(dst: Array[ContentType]): BufferType = internalBuffer.get(dst)
  def get(dst: Array[ContentType], offset: Int, length: Int): BufferType = internalBuffer.get(dst, offset, length)
  def get(index: Int): ContentType = internalBuffer.get(index)
  // those two put methods conflict after type erasure
  def put(value: ContentType): BufferType = throw new ReadOnlyBufferException
  //def put(src: Array[ContentType]): BufferType
  def put(src: Array[ContentType], offset: Int, length: Int): BufferType = throw new ReadOnlyBufferException
  def put(src: BufferType): BufferType = throw new ReadOnlyBufferException
  def put(index: Int, value: ContentType): BufferType = throw new ReadOnlyBufferException

  def compact(): BufferType = throw new ReadOnlyBufferException

  // ScalaJS specific
  def hasJsArray(): Boolean = false
  def jsArray(): Nothing = throw new ReadOnlyBufferException

  def hasJsBuffer(): Boolean = false
  def jsBuffer(): dom.ArrayBuffer = throw new ReadOnlyBufferException
  def jsBufferOffset(): Int = throw new ReadOnlyBufferException
  def jsDataView(): dom.DataView = throw new ReadOnlyBufferException

  // Still abstract
  def asReadOnlyBuffer(): BufferType
  def duplicate(): BufferType
  def slice(): BufferType
}