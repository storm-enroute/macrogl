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

  final def bufferSubData(target: Int, offset: Long, data: Data.Float) {
    GL15.glBufferSubData(target, offset, data)
  }

  final def getBufferSubData(target: Int, offset: Long, data: Data.Float) {
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

  final def uniformMatrix4(loc: Token.UniformLocation, transpose: Boolean, matrix: Data.Float) {
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

  final def getProgramInfoLog(program: Token.Program, maxLength: Int) = {
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

  final def getShaderInfoLog(shader: Token.Shader, maxLength: Int) = {
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

  final def frameBufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Token.Texture, level: Int) {
    GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level)
  }

  final def frameBufferRenderBuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Token.RenderBuffer) {
    GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
  }

  final def getInteger(flag: Int): Int = {
    GL11.glGetInteger(flag)
  }

  final def getInteger(flag: Int, data: Data.Int) {
    GL11.glGetInteger(flag, data)
  }

  final def getFloat(flag: Int, data: Data.Float) {
    GL11.glGetFloat(flag, data)
  }

  final def getDouble(flag: Int, data: Data.Double) {
    GL11.glGetDouble(flag, data)
  }

  final def getRenderBufferBinding(): Int = {
    GL11.glGetInteger(Macrogl.GL_RENDERBUFFER_BINDING)
  }

  final def genRenderBuffers(): Token.RenderBuffer = {
    val index = GL30.glGenRenderbuffers()
    if (index > 0) index
    else throw new MacroglException(s"Render buffer could not be created: $index")
  }

  final def deleteRenderBuffers(rb: Token.RenderBuffer) {
    GL30.glDeleteRenderbuffers(rb)
  }

  final def bindRenderBuffer(target: Int, rb: Token.RenderBuffer) {
    GL30.glBindRenderbuffer(target, rb)
  }

  final def renderBufferStorage(target: Int, format: Int, width: Int, height: Int) {
    GL30.glRenderbufferStorage(target, format, width, height)
  }

  final def genTextures(): Token.Texture = {
    val index = GL11.glGenTextures()
    if (index > 0) index
    else throw new MacroglException(s"Texture could not be created: $index")
  }

  final def deleteTextures(t: Token.Texture) {
    GL11.glDeleteTextures(t)
  }

  final def activeTexture(num: Int) {
    GL13.glActiveTexture(num)
  }

  final def bindTexture(target: Int, texture: Token.Texture) {
    GL11.glBindTexture(target, texture)
  }

  final def texParameterf(target: Int, name: Int, v: Float) {
    GL11.glTexParameterf(target, name, v)
  }

  final def texParameteri(target: Int, name: Int, v: Int) {
    GL11.glTexParameteri(target, name, v)
  }

  final def getTexParameteri(target: Int, name: Int): Int = {
    GL11.glGetTexParameteri(target, name)
  }

  final def texImage1D(target: Int, level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int, data: Data.Int) {
    GL11.glTexImage1D(target, level, internalFormat, wdt, border, format, dataType, data)
  }

  final def texImage2D(target: Int, level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int, data: Data.Int) {
    GL11.glTexImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, data)
  }

  final def texImage2D(target: Int, level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int, data: Data.Byte) {
    GL11.glTexImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, data)
  }

  final def viewport(x: Int, y: Int, w: Int, h: Int) {
    GL11.glViewport(x, y, w, h)
  }

  final def enable(flag: Int) {
    GL11.glEnable(flag)
  }

  final def disable(flag: Int) {
    GL11.glDisable(flag)
  }

  final def isEnabled(flag: Int): Boolean = {
    GL11.glIsEnabled(flag)
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
    uloc != -1
  }

  final def validFrameBuffer(fb: Token.FrameBuffer): Boolean = {
    fb > 0
  }

  final def validRenderBuffer(rb: Token.RenderBuffer): Boolean = {
    rb > 0
  }

  final def differentPrograms(p1: Token.Program, p2: Token.Program): Boolean = {
    p1 != p2
  }

  final def clear(bits: Int) {
    GL11.glClear(bits)
  }

  final def color4f(r: Float, g: Float, b: Float, a: Float) {
    GL11.glColor4f(r, g, b, a)
  }

  final def cullFace(flag: Int) {
    GL11.glCullFace(flag)
  }

  final def drawBuffers(ib: Data.Int) {
    GL20.glDrawBuffers(ib)
  }

  final def readBuffer(b: Int) {
    GL11.glReadBuffer(b)
  }

  final def begin(mode: Int) {
    GL11.glBegin(mode)
  }

  final def end() {
    GL11.glEnd()
  }

  final def matrixMode(mode: Int) {
    GL11.glMatrixMode(mode)
  }

  final def pushMatrix() {
    GL11.glPushMatrix()
  }

  final def popMatrix() {
    GL11.glPopMatrix()
  }

  final def loadMatrix(data: Data.Double) {
    GL11.glLoadMatrix(data)
  }

  final def loadIdentity() {
    GL11.glLoadIdentity()
  }

  final def frustum(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double) {
    GL11.glFrustum(left, right, bottom, top, nearPlane, farPlane)
  }

  final def ortho(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double) {
    GL11.glOrtho(left, right, bottom, top, nearPlane, farPlane)
  }

  final def lookAt(xfrom: Float, yfrom: Float, zfrom: Float, xto: Float, yto: Float, zto: Float, xup: Float, yup: Float, zup: Float) {
    GLU.gluLookAt(xfrom, yfrom, zfrom, xto, yto, zto, xup, yup, zup)
  }

  final def blendFunc(srcFactor: Int, dstFactor: Int) {
    GL11.glBlendFunc(srcFactor, dstFactor)
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

  val GL_MATRIX_MODE = GL11.GL_MATRIX_MODE

  val GL_PROJECTION = GL11.GL_PROJECTION

  val GL_PROJECTION_MATRIX = GL11.GL_PROJECTION_MATRIX

  val GL_MODELVIEW = GL11.GL_MODELVIEW

  val GL_MODELVIEW_MATRIX = GL11.GL_MODELVIEW_MATRIX

  val GL_TEXTURE = GL11.GL_TEXTURE

  val GL_TEXTURE_MATRIX = GL11.GL_TEXTURE_MATRIX

  val GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER

  val GL_FLOAT = GL11.GL_FLOAT

  val GL_CURRENT_PROGRAM = GL20.GL_CURRENT_PROGRAM

  val GL_NO_ERROR = GL11.GL_NO_ERROR

  val GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER

  val GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER

  val GL_FRAMEBUFFER = GL30.GL_FRAMEBUFFER

  val GL_FRAMEBUFFER_BINDING = GL30.GL_FRAMEBUFFER_BINDING

  val GL_RENDERBUFFER = GL30.GL_RENDERBUFFER

  val GL_RENDERBUFFER_BINDING = GL30.GL_RENDERBUFFER_BINDING

  val GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS

  val GL_LINK_STATUS = GL20.GL_LINK_STATUS

  val GL_VALIDATE_STATUS = GL20.GL_VALIDATE_STATUS

  val GL_STREAM_COPY = GL15.GL_STREAM_COPY

  val GL_TEXTURE_1D = GL11.GL_TEXTURE_1D

  val GL_TEXTURE_BINDING_1D = GL11.GL_TEXTURE_BINDING_1D

  val GL_TEXTURE_2D = GL11.GL_TEXTURE_2D

  val GL_TEXTURE_BINDING_2D = GL11.GL_TEXTURE_BINDING_2D

  val GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER

  val GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER

  val GL_TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S

  val GL_TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T

  val GL_TEXTURE_COMPARE_MODE = GL14.GL_TEXTURE_COMPARE_MODE

  val GL_TEXTURE_COMPARE_FUNC = GL14.GL_TEXTURE_COMPARE_FUNC

  val GL_DEPTH_TEXTURE_MODE = GL14.GL_DEPTH_TEXTURE_MODE

  val GL_CURRENT_COLOR = GL11.GL_CURRENT_COLOR

  val GL_CULL_FACE_MODE = GL11.GL_CULL_FACE_MODE

  val GL_VIEWPORT = GL11.GL_VIEWPORT

  val GL_BLEND_SRC = GL11.GL_BLEND_SRC

  val GL_BLEND_DST = GL11.GL_BLEND_DST

  /* public API - methods */

  final def createFloatData(sz: Int): Data.Float = {
    org.lwjgl.BufferUtils.createFloatBuffer(sz)
  }

  final def createIntData(sz: Int): Data.Int = {
    org.lwjgl.BufferUtils.createIntBuffer(sz)
  }

  final def createDoubleData(sz: Int): Data.Double = {
    org.lwjgl.BufferUtils.createDoubleBuffer(sz)
  }

  /* public API - implicits */

  implicit val default = new Macrogl()

  /* implementation-specific methods */

}













