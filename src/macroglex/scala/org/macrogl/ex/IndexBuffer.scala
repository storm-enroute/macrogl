package org.macrogl
package ex



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



class IndexBuffer
  (val usage: Int, val totalIndices: Int)(implicit gl: Macroglex)
extends Handle {
  private var vtoken = Token.Buffer.invalid
  private var vaotoken = Token.VertexArray.none
  private var result = new Array[Int](1)
  private var totalelems = 0

  def acquire() {
    release()
    vtoken = gl.createBuffer
    vaotoken = gl.createVertexArray
    gl.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, vtoken)
    gl.bufferData(Macrogl.ELEMENT_ARRAY_BUFFER, totalBytes, usage)
    gl.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, Token.Buffer.none)
    gl.checkError()
  }

  def release() {
    if (gl.validBuffer(vtoken)) {
      gl.deleteBuffer(vtoken)
      vtoken = Token.Buffer.invalid
      gl.deleteVertexArray(vaotoken)
      vaotoken = Token.Buffer.none
    }
  }

  def token: Token.Buffer = vtoken

  def vertexArrayToken: Token.VertexArray = vaotoken

  def totalBytes = totalIndices * gl.bytesPerInt

  def send(offset: Long, data: Data.Int) {
    gl.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, vtoken)
    gl.bufferSubData(Macrogl.ELEMENT_ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, Token.Buffer.none)
  }

  object access extends IndexBuffer.Access {
    def render(mode: Int, vertexBuffer: VertexBuffer) {
      try {
        vertexBuffer.enableForRender()
        gl.drawElements(mode, totalIndices, Macrogl.UNSIGNED_INT, 0)
      } finally {
        vertexBuffer.disableForRender()
      }
    }
  }

}


object IndexBuffer {

  trait Access {
    def render(mode: Int, vertexBuffer: VertexBuffer): Unit
  }

  import Macros._

  def using[U: c.WeakTypeTag](c: Context)
    (f: c.Expr[Access => U])(gl: c.Expr[Macroglex]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _), _) =
      c.macroApplication

    val r = reify {
      val m = (c.Expr[IndexBuffer](mesh)).splice
      gl.splice.bindVertexArray(m.vertexArrayToken)
      gl.splice.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, m.token)
      try f.splice(m.access)
      finally {
        gl.splice.bindBuffer(Macrogl.ELEMENT_ARRAY_BUFFER, Token.Buffer.none)
        gl.splice.bindVertexArray(Token.VertexArray.none)
      }
      ()
    }

    c.inlineAndReset(r)
  }

}
