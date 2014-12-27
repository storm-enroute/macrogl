package org.macrogl
package ex



import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._



class Program
  (n: String)(ss: org.macrogl.Program.Shader*)(implicit val glex: Macroglex)
extends org.macrogl.Program(n)(ss: _*)(glex) {

  def dispatch(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) {
    assert(shaders.length == 1, "Only a single compute shader allowed.")
    shaders.head match {
      case Program.Shader.Compute(_, _) =>
        val opidx = gl.getCurrentProgram()
        if (gl.differentPrograms(opidx, token)) gl.useProgram(token)
        try glex.dispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
        finally if (gl.differentPrograms(opidx, token)) gl.useProgram(opidx)
      case _ =>
        throw new org.macrogl.Program.Exception(this,
          "Can only dispatch a compute shader.")
    }
  }

}


object Program {

  object Shader {

    case class Geometry
      (source: String, afterAttach: Token.Program => Unit = x => {})
    extends org.macrogl.Program.Shader {
      def name = "Geometry shader"
      def mode = Macroglex.GEOMETRY_SHADER
    }

    case class Compute
      (source: String, afterAttach: Token.Program => Unit = x => {})
    extends org.macrogl.Program.Shader {
      def name = "Compute shader"
      def mode = Macroglex.COMPUTE_SHADER
    }

  }

}
