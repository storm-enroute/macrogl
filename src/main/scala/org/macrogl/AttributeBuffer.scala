package org.macrogl



import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils._



class AttributeBuffer(val usage: Int, val capacity: Int, val attributes: Int)
extends Handle {
  private var vindex = -1
  private var result = new Array[Int](1)
  private var totalelems = 0

  def acquire() {
    release()
    vindex = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vindex)
    glBufferData(GL_ARRAY_BUFFER, totalBytes, usage)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    status.check()
  }

  def release() {
    if (vindex != -1) {
      glDeleteBuffers(vindex)
      vindex = -1
    }
  }

  def index = vindex

  def totalBytes = capacity * attributes * gl.bytesPerFloat

  def send(offset: Long, data: FloatBuffer) {
    glBindBuffer(GL_ARRAY_BUFFER, vindex)
    glBufferSubData(GL_ARRAY_BUFFER, offset, data)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
  }

  def enableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      glEnableVertexAttribArray(i)
      i += 1
    }
  }

  def disableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      glDisableVertexAttribArray(i)
      i += 1
    }
  }

  def setAttributePointers(attribs: Array[(Int, Int)]) {
    val stride = attributes * gl.bytesPerFloat
    var i = 0
    while (i < attribs.length) {
      val byteOffset = attribs(i)._1 * gl.bytesPerFloat
      val num = attribs(i)._2
      glVertexAttribPointer(i, num, GL_FLOAT, false, stride, byteOffset)
      i += 1
    }
  }

  object access extends AttributeBuffer.Access {
    def render(mode: Int, attribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(attribs)
        setAttributePointers(attribs)
        glDrawArrays(mode, 0, capacity)
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

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _) = c.macroApplication

    val r = reify {
      val m = (c.Expr[AttributeBuffer](mesh)).splice
      glBindBuffer(GL_ARRAY_BUFFER, m.index)
      try f.splice(m.access)
      finally {
        glBindBuffer(GL_ARRAY_BUFFER, 0)
      }
      ()
    }

    c.inlineAndReset(r)
  }

  def computing[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(Apply(_, List(mesh)), List(layoutIndex)), _), _), _) = c.macroApplication

    val r = reify {
      gl.bindShaderStorageBuffer((c.Expr[Int](layoutIndex)).splice, (c.Expr[AttributeBuffer](mesh)).splice.index)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}
