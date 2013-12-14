package org.macrogl



import scala.collection._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._



final class RenderBuffer(val format: Int, val width: Int, val height: Int)
extends Handle {
  private var rbindex = -1
  private val result = new Array[Int](1)

  def index = rbindex

  def acquire() {
    release()
    rbindex = glGenRenderbuffers()
    allocateStorage()
  }

  private def allocateStorage() {
    val oldbinding = org.lwjgl.opengl.GL11.glGetInteger(GL_RENDERBUFFER_BINDING)
    glBindRenderbuffer(GL_RENDERBUFFER, this.index)
    glRenderbufferStorage(GL_RENDERBUFFER, format, width, height)
    glBindFramebuffer(GL_RENDERBUFFER, oldbinding)
  }

  def release() {
    if (rbindex != -1) {
      glDeleteRenderbuffers(rbindex)
      rbindex = -1
    }
  }

}
