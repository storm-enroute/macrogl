package org.macrogl
package ex


import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._



class StreamBuffer(val copies: Int, val attributes: Int, val size: Int)(implicit gl: Macrogl)
extends Handle {
  private val buffers = new Array[AttributeBuffer](copies)
  for (i <- 0 until buffers.length) buffers(i) = new AttributeBuffer(Macroglex.STREAM_COPY, size, attributes)
  private var last = 0

  def currentCopy = buffers(last)

  def advanceCopy() = last = (last + 1) % buffers.length

  def acquire() {
    for (b <- buffers) b.acquire()
  }

  def release() {
    for (b <- buffers) b.release()
  }

}
