package org.macrogl

import org.scalajs.dom
import org.scalajs.nio

// See https://github.com/scala-js/scala-js-dom/blob/master/src/main/scala/org/scalajs/dom/WebGL.scala for documentation about the WebGL DOM for ScalaJS

class Macrogl private[macrogl] (implicit gl: org.scalajs.dom.WebGLRenderingContext) {

  /* public API */

  final def bytesPerFloat = 4

  final def genBuffers(): Token.Buffer = {
    val buffer = gl.createBuffer()
    // TODO how to check the return value?
    buffer
  }

  final def bindBuffer(target: Int, buffer: Token.Buffer) {
    gl.bindBuffer(target, buffer)
  }

  final def bufferData(target: Int, totalBytes: Long, usage: Int) {
    gl.bufferData(target, totalBytes, usage)
  }
  
  //Added from the original api-opengl
  final def bufferData(target: Int, data: Data.Float, usage: Int) {
  	gl.bufferData(target, data.jsDataView, usage)
  }

  final def deleteBuffers(buffer: Token.Buffer) {
    gl.deleteBuffer(buffer)
  }

  final def bufferSubData(target: Int, offset: Long, data: Data.Float) {
    ???
  }

  final def getBufferSubData(target: Int, offset: Long, data: Data.Float) {
    ???
  }

  final def enableVertexAttribArray(index: Int) {
    ???
  }

  final def disableVertexAttribArray(index: Int) {
    ???
  }

  final def vertexAttribPointer(index: Int, numComponents: Int, componentType: Int, normalized: Boolean, stride: Int, byteOffset: Long) {
    ???
  }

  final def drawArrays(mode: Int, first: Int, count: Int) {
    ???
  }

  final def getCurrentProgram(): Token.Program = {
    ???
  }

  final def useProgram(program: Token.Program) {
    ???
  }

  final def getUniformLocation(program: Token.Program, varname: String): Token.UniformLocation = {
    ???
  }

  final def uniform1f(loc: Token.UniformLocation, v: Float) {
    ???
  }

  final def uniform2f(loc: Token.UniformLocation, x: Float, y: Float) {
    ???
  }

  final def uniform3f(loc: Token.UniformLocation, x: Float, y: Float, z: Float) {
    ???
  }

  final def uniform4f(loc: Token.UniformLocation, x: Float, y: Float, z: Float, w: Float) {
    ???
  }

  final def uniform1i(loc: Token.UniformLocation, v: Int) {
    ???
  }

  final def uniform2i(loc: Token.UniformLocation, x: Int, y: Int) {
    ???
  }

  final def uniform3i(loc: Token.UniformLocation, x: Int, y: Int, z: Int) {
    ???
  }

  final def uniform4i(loc: Token.UniformLocation, x: Int, y: Int, z: Int, w: Int) {
    ???
  }

  final def uniformMatrix4(loc: Token.UniformLocation, transpose: Boolean, matrix: Data.Float) {
    ???
  }

  final def createProgram(): Token.Program = {
    ???
  }

  final def deleteProgram(program: Token.Program) {
    ???
  }

  final def getProgrami(program: Token.Program, parameterName: Int): Int = {
    ???
  }

  final def getProgramInfoLog(program: Token.Program, maxLength: Int) {
    ???
  }

  final def linkProgram(program: Token.Program) {
    ???
  }

  final def validateProgram(program: Token.Program) {
    ???
  }

  final def createShader(mode: Int): Token.Shader = {
    ???
  }

  final def deleteShader(shader: Token.Shader) {
    ???
  }

  final def shaderSource(shader: Token.Shader, srcarray: Array[CharSequence]) {
    ???
  }

  final def compileShader(shader: Token.Shader) {
    ???
  }

  final def getShaderi(shader: Token.Shader, parameterName: Int): Int = {
    ???
  }

  final def getShaderInfoLog(shader: Token.Shader, maxLength: Int) {
    ???
  }

  final def attachShader(program: Token.Program, s: Token.Shader) {
    ???
  }

  final def genFrameBuffers(): Token.FrameBuffer = {
    ???
  }

  final def deleteFrameBuffers(fb: Token.FrameBuffer) {
    ???
  }

  final def bindFrameBuffer(target: Int, fb: Token.FrameBuffer) {
    ???
  }

  final def frameBufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Token.Texture, level: Int) {
    ???
  }

  final def frameBufferRenderBuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Token.RenderBuffer) {
    ???
  }

  final def getInteger(flag: Int): Int = {
    ???
  }

  final def getInteger(flag: Int, data: Data.Int) {
    ???
  }

  final def getFloat(flag: Int, data: Data.Float) {
    ???
  }

  final def getDouble(flag: Int, data: Data.Double) {
    ???
  }

  final def getRenderBufferBinding(): Int = {
    ???
  }

  final def genRenderBuffers(): Token.RenderBuffer = {
    ???
  }

  final def deleteRenderBuffers(rb: Token.RenderBuffer) {
    ???
  }

  final def bindRenderBuffer(target: Int, rb: Token.RenderBuffer) {
    ???
  }

  final def renderBufferStorage(target: Int, format: Int, width: Int, height: Int) {
    ???
  }

  final def genTextures(): Token.Texture = {
    ???
  }

  final def deleteTextures(t: Token.Texture) {
    ???
  }

  final def activeTexture(num: Int) {
    ???
  }

  final def bindTexture(target: Int, texture: Token.Texture) {
    ???
  }

  final def texParameterf(target: Int, name: Int, v: Float) {
    ???
  }

  final def texParameteri(target: Int, name: Int, v: Int) {
    ???
  }

  final def getTexParameteri(target: Int, name: Int): Int = {
    ???
  }

  final def texImage1D(target: Int, level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int, data: Data.Int) {
    ???
  }

  final def texImage2D(target: Int, level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int, data: Data.Int) {
    ???
  }

  final def texImage2D(target: Int, level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int, data: Data.Byte) {
    ???
  }

  final def viewport(x: Int, y: Int, w: Int, h: Int) {
    ???
  }

  final def enable(flag: Int) {
    ???
  }

  final def disable(flag: Int) {
    ???
  }

  final def isEnabled(flag: Int): Boolean = {
    ???
  }

  final def validProgram(program: Token.Program): Boolean = {
    ???
  }

  final def validShader(shader: Token.Shader): Boolean = {
    ???
  }

  final def validBuffer(buffer: Token.Buffer): Boolean = {
    ???
  }

  final def validUniformLocation(uloc: Token.UniformLocation): Boolean = {
    ???
  }

  final def validFrameBuffer(fb: Token.FrameBuffer): Boolean = {
    ???
  }

  final def validRenderBuffer(rb: Token.RenderBuffer): Boolean = {
    ???
  }

  final def differentPrograms(p1: Token.Program, p2: Token.Program): Boolean = {
    ???
  }

  final def clear(bits: Int) {
    ???
  }

  final def color4f(r: Float, g: Float, b: Float, a: Float) {
    ???
  }

  final def cullFace(flag: Int) {
    ???
  }

  final def drawBuffers(ib: Data.Int) {
    ???
  }

  final def readBuffer(b: Int) {
    ???
  }

  final def begin(mode: Int) {
    ???
  }

  final def end() {
    ???
  }

  final def matrixMode(mode: Int) {
    ???
  }

  final def pushMatrix() {
    ???
  }

  final def popMatrix() {
    ???
  }

  final def loadMatrix(data: Data.Double) {
    ???
  }

  final def loadIdentity() {
    ???
  }

  final def frustum(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double) {
    ???
  }

  final def ortho(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double) {
    ???
  }

  final def lookAt(xfrom: Float, yfrom: Float, zfrom: Float, xto: Float, yto: Float, zto: Float, xup: Float, yup: Float, zup: Float) {
    ???
  }

  final def blendFunc(srcFactor: Int, dstFactor: Int) {
    ???
  }

  final def checkError() {
    ???
  }

  final def errorMessage(): String = {
    ???
  }

  final def framebufferStatus(target: Int): String = {
    ???
  }

}


// Those constants are actually retrieved from the rendering context in WebGL, this may be a problem to keep them in an object
object Macrogl {

  /* public API - constants */

  val GL_FALSE = false

  val GL_MATRIX_MODE = ??? // deprecated?

  val GL_PROJECTION = ??? // deprecated?

  val GL_PROJECTION_MATRIX = ??? // deprecated?

  val GL_MODELVIEW = ??? // deprecated?

  val GL_MODELVIEW_MATRIX = ??? // deprecated?

  val GL_TEXTURE = gl.TEXTURE.toInt

  val GL_TEXTURE_MATRIX = ??? // deprecated?

  val GL_ARRAY_BUFFER = gl.ARRAY_BUFFER.toInt

  val GL_FLOAT = gl.FLOAT.toInt

  val GL_CURRENT_PROGRAM = gl.CURRENT_PROGRAM.toInt

  val GL_NO_ERROR = gl.NO_ERROR.toInt

  val GL_VERTEX_SHADER = gl.VERTEX_SHADER.toInt

  val GL_FRAGMENT_SHADER = gl.FRAGMENT_SHADER.toInt

  val GL_FRAMEBUFFER = gl.FRAMEBUFFER.toInt

  val GL_FRAMEBUFFER_BINDING = gl.FRAMEBUFFER_BINDING.toInt

  val GL_RENDERBUFFER = gl.RENDERBUFFER.toInt

  val GL_RENDERBUFFER_BINDING = gl.RENDERBUFFER_BINDING.toInt

  val GL_COMPILE_STATUS = gl.COMPILE_STATUS.toInt

  val GL_LINK_STATUS = gl.LINK_STATUS.toInt

  val GL_VALIDATE_STATUS = gl.VALIDATE_STATUS.toInt

  val GL_STREAM_COPY = ??? // deprecated?

  val GL_TEXTURE_1D = ??? // deprecated?

  val GL_TEXTURE_BINDING_1D = ??? // deprecated?

  val GL_TEXTURE_2D = gl.TEXTURE_2D.toInt

  val GL_TEXTURE_BINDING_2D = gl.TEXTURE_BINDING_2D.toInt

  val GL_TEXTURE_MIN_FILTER = gl.TEXTURE_MIN_FILTER.toInt

  val GL_TEXTURE_MAG_FILTER = gl.TEXTURE_MAG_FILTER.toInt

  val GL_TEXTURE_WRAP_S = gl.TEXTURE_WRAP_S.toInt

  val GL_TEXTURE_WRAP_T = gl.TEXTURE_WRAP_T.toInt

  val GL_TEXTURE_COMPARE_MODE = ??? // deprecated?

  val GL_TEXTURE_COMPARE_FUNC = ??? // deprecated?

  val GL_DEPTH_TEXTURE_MODE = ??? // deprecated?

  val GL_CURRENT_COLOR = ??? // deprecated?

  val GL_CULL_FACE_MODE = gl.CULL_FACE_MODE.toInt

  val GL_VIEWPORT = gl.VIEWPORT.toInt

  val GL_BLEND_SRC = ??? // maybe gl.BLEND_SRC_RGB

  val GL_BLEND_DST = ??? // maybe BLEND_DST_RGB

  /* public API - methods */

  final def createFloatData(sz: Int): Data.Float = {
    org.scalajs.nio.NativeFloatBuffer.allocate(sz)
  }

  final def createIntData(sz: Int): Data.Int = {
    org.scalajs.nio.NativeIntBuffer.allocate(sz)
  }

  final def createDoubleData(sz: Int): Data.Double = {
    org.scalajs.nio.NativeDoubleBuffer.allocate(sz)
  }

  /* public API - implicits */

  implicit val default = new Macrogl()

  /* implementation-specific methods */

}













