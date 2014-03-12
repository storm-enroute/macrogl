package org.macrogl



import org.lwjgl.opengl._



class Macroglex private[macrogl] () extends Macrogl() {

  final def bindShaderStorageBuffer(target: Int, layoutIndex: Int, buffer: Token.Buffer) = {
    import org.lwjgl.opengl.GL43._
    GL30.glBindBufferBase(target, layoutIndex, buffer.index)
  }

}


object Macroglex {

  val GL_SHADER_STORAGE_BUFFER = GL43.GL_SHADER_STORAGE_BUFFER

  implicit def default = new Macroglex()

}


