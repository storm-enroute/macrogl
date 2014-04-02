package scala.scalajs.nio

import scala.scalajs.js

trait TypedBuffer[ContentType <: AnyVal, BufferType <: TypedBuffer[ContentType, BufferType]] extends Buffer {
  def equals(ob: Any): Boolean
  def compareTo(that: BufferType): Int
  def hashCode(): Int

  def asReadOnlyBuffer(): BufferType
  def compact(): BufferType
  def duplicate(): BufferType
  def slice(): BufferType

  def order(): ByteOrder

  def get(): ContentType
  def get(dst: Array[ContentType]): BufferType
  def get(dst: Array[ContentType], offset: Int, length: Int): BufferType
  def get(index: Int): ContentType
  // those two put methods conflict after type erasure
  def put(value: ContentType): BufferType
  //def put(src: Array[ContentType]): BufferType
  def put(src: Array[ContentType], offset: Int, length: Int): BufferType
  def put(src: BufferType): BufferType
  def put(index: Int, value: ContentType): BufferType
}