package org.macrogl



import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opencl._
import org.lwjgl.opencl.CL10._
import org.lwjgl.opencl.CL10GL._
import org.lwjgl.BufferUtils._



class MeshBuffer(val usage: Int, val capacityVertices: Int)
extends Handle {
  private var vindex = -1
  private var result = new Array[Int](2)

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

  def bytesPerFloat = 4

  def components = MeshBuffer.COMPONENTS

  def totalBytes = capacityVertices * components * bytesPerFloat

  def send(offset: Long, data: FloatBuffer) {
    glBindBuffer(GL_ARRAY_BUFFER, vindex)
    glBufferSubData(GL_ARRAY_BUFFER, offset, data)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
  }

  def receive(offset: Long, data: FloatBuffer) {
    glBindBuffer(GL_ARRAY_BUFFER, vindex)
    glGetBufferSubData(GL_ARRAY_BUFFER, offset, data)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
  }

  def enableAttributeArrays() {
    glEnableVertexAttribArray(0)
    glEnableVertexAttribArray(1)
    glEnableVertexAttribArray(2)
  }

  def disableAttributeArrays() {
    glDisableVertexAttribArray(0)
    glDisableVertexAttribArray(1)
    glDisableVertexAttribArray(2)
  }

  def enableVertexArray() {
    glEnableVertexAttribArray(0)
  }

  def disableVertexArray() {
    glDisableVertexAttribArray(0)
  }

  def setAttributePointers() {
    val stride = components * bytesPerFloat
    glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0 * bytesPerFloat)
    glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * bytesPerFloat)
    glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * bytesPerFloat)
  }

  def setVertexPointer() {
    val stride = components * bytesPerFloat
    glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0)
  }

  object access extends MeshBuffer.Access {
    def render(mode: Int) {
      try {
        enableAttributeArrays()
        setAttributePointers()
        glDrawArrays(mode, 0, capacityVertices)
      } finally {
        disableAttributeArrays()
      }
    }
    def renderVertices(mode: Int) {
      try {
        enableVertexArray()
        setVertexPointer()
        glDrawArrays(mode, 0, capacityVertices)
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

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _) = c.macroApplication

    val r = reify {
      val m = (c.Expr[MeshBuffer](mesh)).splice
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
      gl.bindShaderStorageBuffer((c.Expr[Int](layoutIndex)).splice, (c.Expr[MeshBuffer](mesh)).splice.index)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}
