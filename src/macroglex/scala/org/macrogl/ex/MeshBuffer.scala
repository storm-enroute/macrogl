package org.macrogl
package ex

import language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._

class MeshBuffer(usage: Int, capacityVertices: Int)(implicit glex: Macroglex) extends org.macrogl.MeshBuffer(usage, capacityVertices)(glex) {
  def receive(offset: Long, data: Data.Float) {
    glex.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    glex.getBufferSubData(Macrogl.ARRAY_BUFFER, offset, data)
    glex.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
  }
}

object MeshBuffer {

  import Macros._

  def computing[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(glex: c.Expr[Macroglex]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(Apply(_, List(mesh)), List(layoutIndex)), _), _), _), _) = c.macroApplication

    val r = reify {
      glex.splice.bindShaderStorageBuffer(Macroglex.SHADER_STORAGE_BUFFER, (c.Expr[Int](layoutIndex)).splice, (c.Expr[MeshBuffer](mesh)).splice.token)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}

