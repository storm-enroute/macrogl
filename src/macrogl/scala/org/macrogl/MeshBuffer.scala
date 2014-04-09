package org.macrogl



import language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



class MeshBuffer(val usage: Int, val capacityVertices: Int)(implicit gl: Macrogl)
extends Handle {
  private var vtoken = Token.Buffer.invalid
  private var result = new Array[Int](2)

  def acquire() {
    release()
    vtoken = gl.genBuffers()
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, vtoken)
    gl.bufferData(Macrogl.GL_ARRAY_BUFFER, totalBytes, usage)
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, Token.Buffer.none)
    gl.checkError()
  }

  def release() {
    if (gl.validBuffer(vtoken)) {
      gl.deleteBuffers(vtoken)
      vtoken = Token.Buffer.invalid
    }
  }

  def token = vtoken

  def bytesPerFloat = 4

  def components = MeshBuffer.COMPONENTS

  def totalBytes = capacityVertices * components * bytesPerFloat

  def send(offset: Long, data: Data.Float) {
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, vtoken)
    gl.bufferSubData(Macrogl.GL_ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, Token.Buffer.none)
  }

  def receive(offset: Long, data: Data.Float) {
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, vtoken)
    gl.getBufferSubData(Macrogl.GL_ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.GL_ARRAY_BUFFER, Token.Buffer.none)
  }

  def enableAttributeArrays() {
    gl.enableVertexAttribArray(0)
    gl.enableVertexAttribArray(1)
    gl.enableVertexAttribArray(2)
  }

  def disableAttributeArrays() {
    gl.disableVertexAttribArray(0)
    gl.disableVertexAttribArray(1)
    gl.disableVertexAttribArray(2)
  }

  def enableVertexArray() {
    gl.enableVertexAttribArray(0)
  }

  def disableVertexArray() {
    gl.disableVertexAttribArray(0)
  }

  def setAttributePointers() {
    val stride = components * bytesPerFloat
    gl.vertexAttribPointer(0, 3, Macrogl.GL_FLOAT, false, stride, 0 * bytesPerFloat)
    gl.vertexAttribPointer(1, 3, Macrogl.GL_FLOAT, false, stride, 3 * bytesPerFloat)
    gl.vertexAttribPointer(2, 2, Macrogl.GL_FLOAT, false, stride, 6 * bytesPerFloat)
  }

  def setVertexPointer() {
    val stride = components * bytesPerFloat
    gl.vertexAttribPointer(0, 3, Macrogl.GL_FLOAT, false, stride, 0)
  }

  object access extends MeshBuffer.Access {
    def render(mode: Int) {
      try {
        enableAttributeArrays()
        setAttributePointers()
        gl.drawArrays(mode, 0, capacityVertices)
      } finally {
        disableAttributeArrays()
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

  trait Access {
    def render(mode: Int): Unit
    def renderVertices(mode: Int): Unit
  }

  import Macros._

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _), _) = c.macroApplication

    val r = reify {
      val m = (c.Expr[MeshBuffer](mesh)).splice
      gl.splice.bindBuffer(Macrogl.GL_ARRAY_BUFFER, m.token)
      try f.splice(m.access)
      finally {
        gl.splice.bindBuffer(Macrogl.GL_ARRAY_BUFFER, Token.Buffer.none)
      }
      ()
    }

    c.inlineAndReset(r)
  }

}
