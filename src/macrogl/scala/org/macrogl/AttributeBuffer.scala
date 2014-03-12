package org.macrogl



import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._



class AttributeBuffer(val usage: Int, val capacity: Int, val attributes: Int)(implicit gles: Macrogles)
extends Handle {
  private var vtoken = Token.Buffer.invalid
  private var result = new Array[Int](1)
  private var totalelems = 0

  def acquire() {
    release()
    vtoken = gles.genBuffers()
    gles.bindBuffer(Macrogles.GL_ARRAY_BUFFER, vtoken)
    gles.bufferData(Macrogles.GL_ARRAY_BUFFER, totalBytes, usage)
    gles.bindBuffer(Macrogles.GL_ARRAY_BUFFER, Token.Buffer.invalid)
    status.check()
  }

  def release() {
    if (vtoken != Token.Buffer.invalid) {
      gles.deleteBuffers(vtoken)
      vtoken = Token.Buffer.invalid
    }
  }

  def token = vtoken

  def totalBytes = capacity * attributes * gl.bytesPerFloat

  def send(offset: Long, data: Buffer.Float) {
    gles.bindBuffer(Macrogles.GL_ARRAY_BUFFER, vtoken)
    gles.bufferSubData(Macrogles.GL_ARRAY_BUFFER, offset, data)
    gles.bindBuffer(Macrogles.GL_ARRAY_BUFFER, Token.Buffer.none)
  }

  def enableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      gles.enableVertexAttribArray(i)
      i += 1
    }
  }

  def disableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      gles.disableVertexAttribArray(i)
      i += 1
    }
  }

  def setAttributePointers(attribs: Array[(Int, Int)]) {
    val stride = attributes * gl.bytesPerFloat
    var i = 0
    while (i < attribs.length) {
      val byteOffset = attribs(i)._1 * gl.bytesPerFloat
      val num = attribs(i)._2
      gles.vertexAttribPointer(i, num, Macrogles.GL_FLOAT, false, stride, byteOffset)
      i += 1
    }
  }

  object access extends AttributeBuffer.Access {
    def render(mode: Int, attribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(attribs)
        setAttributePointers(attribs)
        gles.drawArrays(mode, 0, capacity)
      } finally {
        disableAttributeArrays(attribs)
      }
    }
  }

}


object AttributeBuffer {

  trait Access {
    def render(mode: Int, attribs: Array[(Int, Int)]): Unit
  }

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U])(gles: c.Expr[Macrogles]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _), List(gles)) = c.macroApplication

    val r = reify {
      val m = (c.Expr[AttributeBuffer](mesh)).splice
      val g = (c.Expr[Macrogles](gles)).splice
      g.bindBuffer(Macrogles.GL_ARRAY_BUFFER, m.token)
      try f.splice(m.access)
      finally {
        g.bindBuffer(Macrogles.GL_ARRAY_BUFFER, Token.Buffer.none)
      }
      ()
    }

    c.inlineAndReset(r)
  }

  def computing[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(Apply(_, List(mesh)), List(layoutIndex)), _), _), _) = c.macroApplication

    val r = reify {
      // TODO Macrogl.bindShaderStorageBuffer((c.Expr[Int](layoutIndex)).splice, (c.Expr[AttributeBuffer](mesh)).splice.token)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}



