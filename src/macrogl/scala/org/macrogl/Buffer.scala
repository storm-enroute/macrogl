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



