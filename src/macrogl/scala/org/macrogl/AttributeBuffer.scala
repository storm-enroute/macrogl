package org.macrogl



import language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._

class Buffer(implicit protected val gl: Macrogl) extends Handle {
  private var vtoken = Token.Buffer.none

  def token = vtoken

  def acquire() {
    vtoken = gl.createBuffer
    gl.checkError()
  }

  def release() {
    gl.deleteBuffer(vtoken)
    vtoken = Token.Buffer.none
  }
}

trait BufferAccess {
  this: Buffer =>

  val capacity: Int

  trait BufferAccessInner {
    val target: Int

    def allocate(usage: Int) {
      gl.bufferData(target, capacity, usage)
    }
    def send(offset: Long, data: Data.Byte): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Int): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Float): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Double): Unit = {
      gl.bufferSubData(target, offset, data)
    }
  }
}

trait TextureBufferAccess extends BufferAccess {
  this: Buffer =>

  object textureBufferAccess extends TextureBufferAccessInner

  trait TextureBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.TEXTURE_BUFFER
  }
}

trait TransformFeedbackBufferAccess extends BufferAccess {
  this: Buffer =>

  object transformFeedbackBufferAccess extends TransformFeedbackBufferAccessInner

  trait TransformFeedbackBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.TRANSFORM_FEEDBACK_BUFFER
  }
}

trait IndexBufferAccess extends BufferAccess {
  this: Buffer =>

  val elementCount: Int
  val elementType:  Int

  object indexBufferAccess extends IndexBufferAccessInner

  trait IndexBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.ELEMENT_ARRAY_BUFFER

    def draw(mode: Int, offset: Int): Unit = {
      gl.drawElements(mode, elementCount, elementType, offset)
    }
  }
}

trait VertexBufferAccess extends BufferAccess {
  this: Buffer =>

  val vertexCount: Int
  val attributeCount: Int

  object vertexBufferAccess extends VertexBufferAccessInner

  trait VertexBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.ARRAY_BUFFER

    def enableAttributeArrays(attribs: Array[(Int, Int)]) {
      var i = 0
      while (i < attribs.length) {
        gl.enableVertexAttribArray(i)
        i += 1
      }
    }

    def disableAttributeArrays(attribs: Array[(Int, Int)]) {
      var i = 0
      while (i < attribs.length) {
        gl.disableVertexAttribArray(i)
        i += 1
      }
    }

    def setAttributePointers(attribs: Array[(Int, Int)]) {
      val stride = attributeCount * gl.bytesPerFloat
      var i = 0
      while (i < attribs.length) {
        val byteOffset = attribs(i)._1 * gl.bytesPerFloat
        val num = attribs(i)._2
        gl.vertexAttribPointer(i, num, Macrogl.FLOAT, false, stride, byteOffset)
        i += 1
      }
    }

    def render(mode: Int, attribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(attribs)
        setAttributePointers(attribs)
        gl.drawArrays(mode, 0, vertexCount)
      } finally {
        disableAttributeArrays(attribs)
      }
    }
  }

}

class AttributeBuffer(val usage: Int, val capacity: Int, val attributes: Int)(implicit gl: Macrogl)
extends Handle {
  private var vtoken = Token.Buffer.invalid
  private var result = new Array[Int](1)
  private var totalelems = 0

  def acquire() {
    release()
    vtoken = gl.createBuffer
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

  def token = vtoken

  def totalBytes = capacity * attributes * gl.bytesPerFloat

  def send(offset: Long, data: Data.Float) {
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    gl.bufferSubData(Macrogl.ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
  }

  def enableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      gl.enableVertexAttribArray(i)
      i += 1
    }
  }

  def disableAttributeArrays(attribs: Array[(Int, Int)]) {
    var i = 0
    while (i < attribs.length) {
      gl.disableVertexAttribArray(i)
      i += 1
    }
  }

  def setAttributePointers(attribs: Array[(Int, Int)]) {
    val stride = attributes * gl.bytesPerFloat
    var i = 0
    while (i < attribs.length) {
      val byteOffset = attribs(i)._1 * gl.bytesPerFloat
      val num = attribs(i)._2
      gl.vertexAttribPointer(i, num, Macrogl.FLOAT, false, stride, byteOffset)
      i += 1
    }
  }

  object access extends AttributeBuffer.Access {
    def render(mode: Int, attribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(attribs)
        setAttributePointers(attribs)
        gl.drawArrays(mode, 0, capacity)
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

  import Macros._

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _), _) = c.macroApplication

    val r = reify {
      val m = (c.Expr[AttributeBuffer](mesh)).splice
      gl.splice.bindBuffer(Macrogl.ARRAY_BUFFER, m.token)
      try f.splice(m.access)
      finally {
        gl.splice.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
      }
      ()
    }

    c.inlineAndReset(r)
  }

}

object Buffer {

  import Macros._

  class Macros(val c: Context) {
    def vertexBuffer(f: c.Expr[VertexBufferAccess # VertexBufferAccessInner => Unit])(gl: c.Expr[Macrogl]) = {
      import c.universe._

      val q"$_($buffer).$_($func)($_)" = c.macroApplication

      val inlined: c.Tree = c inlineAndSubstituteArguments(func, q"$buffer.vertexBufferAccess")
      reifyBinding(reify(Macrogl.ARRAY_BUFFER), buffer, inlined, gl)
    }

    def indexBuffer(f: c.Expr[IndexBufferAccess # IndexBufferAccessInner => Unit])(gl: c.Expr[Macrogl]) = {
      import c.universe._

      val q"$_($buffer).$_($func)($_)" = c.macroApplication

      val inlined: c.Tree = c inlineAndSubstituteArguments(func, q"$buffer.indexBufferAccess")
      reifyBinding(reify(Macrogl.ELEMENT_ARRAY_BUFFER), buffer, inlined, gl)
    }

    def textureBuffer(f: c.Expr[TextureBufferAccess # TextureBufferAccessInner => Unit])(gl: c.Expr[Macrogl]) = {
      import c.universe._

      val q"$_($texnum, $buffer).$_($func)($_)" = c.macroApplication

      val inlined: c.Tree = c inlineAndSubstituteArguments(func, q"$buffer.textureBufferAccess")

      val binding = reifyBinding(reify(Macrogl.TEXTURE_BUFFER), buffer, inlined, gl)

      reify {
        gl.splice.activeTexture(c.Expr[Int](texnum).splice)
        binding.splice
      }
    }

    def transformFeedbackBuffer(f: c.Expr[TransformFeedbackBufferAccess # TransformFeedbackBufferAccessInner => Unit])(gl: c.Expr[Macrogl]) = {
      import c.universe._

      val q"$_($index, $buffer).$_($func)($_)" = c.macroApplication

      val inlined: c.Tree = c inlineAndSubstituteArguments(func, q"$buffer.transformFeedbackBufferAccess")
      reifyIndexedBinding(reify(Macrogl.TRANSFORM_FEEDBACK_BUFFER), buffer, index, inlined, gl)
    }

    def reifyBinding(target: c.Expr[Int], buffer: c.Tree, func: c.Tree, gl: c.Expr[Macrogl]) = {
      import c.universe._
      reify {
        gl.splice.bindBuffer(target.splice, c.Expr[Buffer](buffer).splice.token)
        try c.Expr[Unit](func).splice
        finally {
          gl.splice.bindBuffer(target.splice, Token.Buffer.none)
        }
        ()
      }
    }

    def reifyIndexedBinding(target: c.Expr[Int], buffer: c.Tree, index: c.Tree, func: c.Tree, gl: c.Expr[Macrogl]) = {
      import c.universe._
      reify {
        gl.splice.bindBufferBase(target.splice, c.Expr[Int](index).splice, c.Expr[Buffer](buffer).splice.token)
        try c.Expr[Unit](func).splice
        finally {
          gl.splice.bindBuffer(target.splice, Token.Buffer.none)
        }
        ()
      }
    }
  }

}



