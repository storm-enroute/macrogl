package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



abstract class VertexBuffer
extends Handle {
  def enableForRender(): Unit
  def disableForRender(): Unit
  def token: Token.Buffer
  def access: VertexBuffer.Access
}


object VertexBuffer {
  trait Access {
    def render(mode: Int): Unit
  }

  import Macros._

  def using[U: c.WeakTypeTag](c: Context)(f: c.Expr[Access => U])
    (gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(mesh)), _), _), _), _) =
      c.macroApplication

    val r = reify {
      val m = (c.Expr[VertexBuffer](mesh)).splice
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
