package org.macrogl



import org.lwjgl.opengl._



class Macrogl private () {

  final def bindShaderStorageBuffer(target: Int, layoutIndex: Int, buffer: Token.Buffer) = {
    import org.lwjgl.opengl.GL43._
    GL30.glBindBufferBase(target, layoutIndex, buffer.index)
  }

}


object Macrogl {

  val GL_SHADER_STORAGE_BUFFER = GL43.GL_SHADER_STORAGE_BUFFER

}


