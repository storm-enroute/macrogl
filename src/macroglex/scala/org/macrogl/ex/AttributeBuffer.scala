package org.macrogl
package ex



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



object AttributeBuffer {

  import Macros._

  def computing[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(glex: c.Expr[Macroglex]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(layoutIndex, mesh)), _), _), _), _) = c.macroApplication

    val r = reify {
      glex.splice.bindShaderStorageBuffer(Macroglex.SHADER_STORAGE_BUFFER, (c.Expr[Int](layoutIndex)).splice, (c.Expr[AttributeBuffer](mesh)).splice.token)
      try f.splice(())
      finally {
      }
      ()
    }

    c.inlineAndReset(r)
  }

}
