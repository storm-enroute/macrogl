package org.macrogl



import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL14._
import org.lwjgl.opengl.GL15._



class StreamBuffer(val copies: Int, val attributes: Int, val size: Int)(implicit gles: Macrogl)
extends Handle {
  private val buffers = new Array[AttributeBuffer](copies)
  for (i <- 0 until buffers.length) buffers(i) = new AttributeBuffer(GL_STREAM_COPY, size, attributes)
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
