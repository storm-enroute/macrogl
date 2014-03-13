package org.macrogl



import org.lwjgl.opengl._
import org.lwjgl.util.glu._



class Macrogl private[macrogl] () {

  /* public API */

  final def bytesPerFloat = 4

  final def genBuffers(): Token.Buffer = {
    val index = GL15.glGenBuffers()
    if (index > 0) index
    else throw MacroglException(s"Buffer could not be created: $index")
  }

  final def bindBuffer(target: Int, buffer: Token.Buffer) {
    GL15.glBindBuffer(target, buffer)
  }

  final def bufferData(target: Int, totalBytes: Long, usage: Int) {
    GL15.glBufferData(target, totalBytes, usage)
  }

  final def deleteBuffers(buffer: Token.Buffer) {
    GL15.glDeleteBuffers(buffer)
  }

  final def bufferSubData(target: Int, offset: Long, data: Buffer.Float) {
    GL15.glBufferSubData(target, offset, data)
  }

  final def getBufferSubData(target: Int, offset: Long, data: Buffer.Float) {
    GL15.glGetBufferSubData(target, offset, data)
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

  final def getCurrentProgram(): Token.Program = {
    GL11.glGetInteger(Macrogl.GL_CURRENT_PROGRAM)
  }

  final def useProgram(program: Token.Program) {
    GL20.glUseProgram(program)
  }

  final def getUniformLocation(program: Token.Program, varname: String): Token.UniformLocation = {
    GL20.glGetUniformLocation(program, varname)
  }

  final def uniform1f(loc: Token.UniformLocation, v: Float) {
    GL20.glUniform1f(loc, v)
  }

  final def uniform2f(loc: Token.UniformLocation, x: Float, y: Float) {
    GL20.glUniform2f(loc, x, y)
  }

  final def uniform3f(loc: Token.UniformLocation, x: Float, y: Float, z: Float) {
    GL20.glUniform3f(loc, x, y, z)
  }

  final def uniform4f(loc: Token.UniformLocation, x: Float, y: Float, z: Float, w: Float) {
    GL20.glUniform4f(loc, x, y, z, w)
  }

  final def uniform1i(loc: Token.UniformLocation, v: Int) {
    GL20.glUniform1i(loc, v)
  }

  final def uniform2i(loc: Token.UniformLocation, x: Int, y: Int) {
    GL20.glUniform2i(loc, x, y)
  }

  final def uniform3i(loc: Token.UniformLocation, x: Int, y: Int, z: Int) {
    GL20.glUniform3i(loc, x, y, z)
  }

  final def uniform4i(loc: Token.UniformLocation, x: Int, y: Int, z: Int, w: Int) {
    GL20.glUniform4i(loc, x, y, z, w)
  }

  final def uniformMatrix4(loc: Token.UniformLocation, transpose: Boolean, matrix: Buffer.Float) {
    GL20.glUniformMatrix4(loc, transpose, matrix)
  }

  final def createProgram(): Token.Program = {
    val index = GL20.glCreateProgram()
    if (index > 0) index
    else throw new MacroglException(s"Program could not be created: $index")
  }

  final def deleteProgram(program: Token.Program) {
    GL20.glDeleteProgram(program)
  }

  final def getProgrami(program: Token.Program, parameterName: Int): Int = {
    GL20.glGetProgrami(program, parameterName)
  }

  final def getProgramInfoLog(program: Token.Program, maxLength: Int) {
    GL20.glGetShaderInfoLog(program, maxLength)
  }

  final def linkProgram(program: Token.Program) {
    GL20.glLinkProgram(program)
  }

  final def validateProgram(program: Token.Program) {
    GL20.glValidateProgram(program)
  }

  final def createShader(mode: Int): Token.Shader = {
    val index = GL20.glCreateShader(mode)
    if (index > 0) index
    else throw new MacroglException(s"Shader could not be created: $index")
  }

  final def deleteShader(shader: Token.Shader) {
    GL20.glDeleteShader(shader)
  }

  final def shaderSource(shader: Token.Shader, srcarray: Array[CharSequence]) {
    GL20.glShaderSource(shader, srcarray)
  }

  final def compileShader(shader: Token.Shader) {
    GL20.glCompileShader(shader)
  }

  final def getShaderi(shader: Token.Shader, parameterName: Int): Int = {
    GL20.glGetShaderi(shader, parameterName)
  }

  final def getShaderInfoLog(shader: Token.Shader, maxLength: Int) {
    GL20.glGetShaderInfoLog(shader, maxLength)
  }

  final def attachShader(program: Token.Program, s: Token.Shader) {
    GL20.glAttachShader(program, s)
  }

  final def genFrameBuffers(): Token.FrameBuffer = {
    val index = GL30.glGenFramebuffers()
    if (index > 0) index
    else throw new MacroglException(s"Frame buffer could not be created: $index")
  }

  final def deleteFrameBuffers(fb: Token.FrameBuffer) {
    GL30.glDeleteFramebuffers(fb)
  }

  final def bindFrameBuffer(target: Int, fb: Token.FrameBuffer) {
    GL30.glBindFramebuffer(target, fb)
  }

  final def frameBufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int) {
    GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level)
  }

  final def frameBufferRenderBuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int) {
    GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
  }

  final def getInteger(flag: Int): Int = {
    GL11.glGetInteger(flag)
  }

  final def validProgram(program: Token.Program): Boolean = {
    program > 0
  }

  final def validShader(shader: Token.Shader): Boolean = {
    shader > 0
  }

  final def validBuffer(buffer: Token.Buffer): Boolean = {
    buffer > 0
  }

  final def validUniformLocation(uloc: Token.UniformLocation): Boolean = {
    uloc > 0
  }

  final def validFrameBuffer(fb: Token.FrameBuffer): Boolean = {
    fb > 0
  }

  final def differentPrograms(p1: Token.Program, p2: Token.Program): Boolean = {
    p1 != p2
  }

  final def checkError() {
    val code = GL11.glGetError()
    if (code != Macrogl.GL_NO_ERROR) {
      val msg = GLU.gluErrorString(code)
      throw new Exception(s"error: $code - $msg")
    }
  }

  final def errorMessage(): String = {
    val code = GL11.glGetError()
    val msg = GLU.gluErrorString(code)
    s"error: $code - $msg"
  }

  final def framebufferStatus(target: Int): String = {
    val code = GL30.glCheckFramebufferStatus(target)
    s"status: $code"
  }

}


object Macrogl {

  /* public API - constants */

  val GL_FALSE = GL11.GL_FALSE

  val GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER

  val GL_FLOAT = GL11.GL_FLOAT

  val GL_CURRENT_PROGRAM = GL20.GL_CURRENT_PROGRAM

  val GL_NO_ERROR = GL11.GL_NO_ERROR

  val GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER

  val GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER

  val GL_FRAMEBUFFER = GL30.GL_FRAMEBUFFER

  val GL_FRAMEBUFFER_BINDING = GL30.GL_FRAMEBUFFER_BINDING

  val GL_RENDERBUFFER = GL30.GL_RENDERBUFFER

  val GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS

  val GL_LINK_STATUS = GL20.GL_LINK_STATUS

  val GL_VALIDATE_STATUS = GL20.GL_VALIDATE_STATUS

  /* public API - methods */

  /* public API - implicits */

  implicit val default = new Macrogl()

  /* implementation-specific methods */

}













