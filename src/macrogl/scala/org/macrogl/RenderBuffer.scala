package org.macrogl



import scala.collection._



final class RenderBuffer(val format: Int, val width: Int, val height: Int)(implicit gl: Macrogl)
extends Handle {
  private var rbtoken = Token.RenderBuffer.invalid
  private val result = new Array[Int](1)

  def token = rbtoken

  def acquire() {
    release()
    rbtoken = gl.genRenderBuffers()
    allocateStorage()
  }

  private def allocateStorage() {
    val oldbinding = gl.getRenderBufferBinding()
    gl.bindRenderBuffer(Macrogl.GL_RENDERBUFFER, this.token)
    gl.renderBufferStorage(Macrogl.GL_RENDERBUFFER, format, width, height)
    gl.bindRenderBuffer(Macrogl.GL_RENDERBUFFER, oldbinding)
  }

  def release() {
    if (!gl.validRenderBuffer(rbtoken)) {
      gl.deleteRenderBuffers(rbtoken)
      rbtoken = Token.RenderBuffer.invalid
    }
  }

}
