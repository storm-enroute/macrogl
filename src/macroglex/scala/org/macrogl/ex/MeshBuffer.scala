package org.macrogl
package ex



import language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



object MeshBuffer {

  import Macros._

  def computing[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(glex: c.Expr[Macroglex]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(Apply(_, List(mesh)), List(layoutIndex)), _), _), _), _) = c.macroApplication

    val r = reify {
      glex.splice.bindShaderStorageBuffer(Macroglex.GL_SHADER_STORAGE_BUFFER, (c.Expr[Int](layoutIndex)).splice, (c.Expr[MeshBuffer](mesh)).splice.token)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}

