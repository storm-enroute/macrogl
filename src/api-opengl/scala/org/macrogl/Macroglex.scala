package org.macrogl



import org.lwjgl.opengl._



class Macroglex private[macrogl] () extends Macrogl() {

  final def bindShaderStorageBuffer(target: Int, layoutIndex: Int, buffer: Token.Buffer) = {
    import org.lwjgl.opengl.GL43._
    GL30.glBindBufferBase(target, layoutIndex, buffer)
  }

  final def dispatchCompute(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) {
    GL43.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
  }

}


object Macroglex {

  /* public API - constants */

  val GL_GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER

  val GL_COMPUTE_SHADER = GL43.GL_COMPUTE_SHADER

  val GL_SHADER_STORAGE_BUFFER = GL43.GL_SHADER_STORAGE_BUFFER

  /* public API - implicits */

  implicit def default = new Macroglex()

}


