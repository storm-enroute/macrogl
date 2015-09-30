package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



class MeshBuffer
  (val usage: Int, val capacityVertices: Int)(implicit gl: Macrogl)
extends VertexBuffer {
  protected var vtoken = Token.Buffer.invalid
  protected var result = new Array[Int](2)

  def acquire() {
    release()
    vtoken = gl.createBuffer()
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    gl.bufferData(Macrogl.ARRAY_BUFFER, totalBytes, usage)
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
    gl.checkError()
  }

  def release() {
    if (gl.validBuffer(vtoken)) {
      gl.deleteBuffer(vtoken)
      vtoken = Token.Buffer.invalid
    }
  }

  def token: Token.Buffer = vtoken

  def bytesPerFloat = 4

  def components = MeshBuffer.COMPONENTS

  def totalBytes = capacityVertices * components * bytesPerFloat

  def send(offset: Long, data: Data.Float) {
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    gl.bufferSubData(Macrogl.ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
  }

  private def enableAttributeArrays() {
    gl.enableVertexAttribArray(0)
    gl.enableVertexAttribArray(1)
    gl.enableVertexAttribArray(2)
  }

  private def disableAttributeArrays() {
    gl.disableVertexAttribArray(0)
    gl.disableVertexAttribArray(1)
    gl.disableVertexAttribArray(2)
  }

  private def enableVertexArray() {
    gl.enableVertexAttribArray(0)
  }

  private def disableVertexArray() {
    gl.disableVertexAttribArray(0)
  }

  private def setAttributePointers() {
    val stride = components * bytesPerFloat
    gl.vertexAttribPointer(0, 3, Macrogl.FLOAT, false, stride, 0 * bytesPerFloat)
    gl.vertexAttribPointer(1, 3, Macrogl.FLOAT, false, stride, 3 * bytesPerFloat)
    gl.vertexAttribPointer(2, 2, Macrogl.FLOAT, false, stride, 6 * bytesPerFloat)
  }

  def setVertexPointer() {
    val stride = components * bytesPerFloat
    gl.vertexAttribPointer(0, 3, Macrogl.FLOAT, false, stride, 0)
  }

  def enableForRender() {
    enableAttributeArrays()
    setAttributePointers()
  }

  def disableForRender() {
    disableAttributeArrays()
  }

  object access extends VertexBuffer.Access {
    def render(mode: Int) {
      try {
        enableForRender()
        gl.drawArrays(mode, 0, capacityVertices)
      } finally {
        disableForRender()
      }
    }
    def renderVertices(mode: Int) {
      try {
        enableVertexArray()
        setVertexPointer()
        gl.drawArrays(mode, 0, capacityVertices)
      } finally {
        disableVertexArray()
      }
    }
  }

}


object MeshBuffer {

  val COMPONENTS = 8

}
