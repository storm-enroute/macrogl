package org.macrogl



import org.lwjgl.opengl._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._



object gl {
  def bytesPerFloat = 4
  def currentProgram = GL11.glGetInteger(GL_CURRENT_PROGRAM)
  def useProgram(pidx: Int) = glUseProgram(pidx)
  def bindShaderStorageBuffer(layoutIndex: Int, bufferId: Int) = {
    import org.lwjgl.opengl.GL43._
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, layoutIndex, bufferId)
  }
  def status = org.macrogl.status
  def getVertexAttrib(idx: Int, flag: Int) = {
    Results.intResult.clear()
    glGetVertexAttrib(idx, flag, Results.intResult)
    Results.intResult.get(0)
  }
}

