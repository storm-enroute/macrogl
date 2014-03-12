package org.macrogl



import org.lwjgl.opengl._



class Macrogles private () {

  final def genBuffers(): Token.Buffer = {
    val index = GL15.glGenBuffers()
    if (index > 0) new Token.Buffer(index)
    else throw MacroglException("Buffer could not be created.")
  }

  final def bindBuffer(target: Int, buffer: Token.Buffer) {
    GL15.glBindBuffer(target, buffer.index)
  }

  final def bufferData(target: Int, totalBytes: Long, usage: Int) {
    GL15.glBufferData(target, totalBytes, usage)
  }

  final def deleteBuffers(buffer: Token.Buffer) {
    GL15.glDeleteBuffers(buffer.index)
  }

  final def bufferSubData(target: Int, offset: Long, data: Buffer.Float) {
    GL15.glBufferSubData(target, offset, data)
  }

  final def enableVertexAttribArray(index: Int) {
    GL20.glEnableVertexAttribArray(index)
  }

  final def disableVertexAttribArray(index: Int) {
    GL20.glDisableVertexAttribArray(index)
  }

  final def vertexAttribPointer(index: Int, numComponents: Int, componentType: Int, normalized: Boolean, stride: Int, byteOffset: Long) {
    GL20.glVertexAttribPointer(index, numComponents, componentType, normalized, stride, byteOffset)
  }

  final def drawArrays(mode: Int, first: Int, count: Int) {
    GL11.glDrawArrays(mode, first, count)
  }

}


object Macrogles {

  val GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER

  val GL_FLOAT = GL11.GL_FLOAT

  implicit val default = new Macrogles()

}

