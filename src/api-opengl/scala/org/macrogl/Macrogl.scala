package org.macrogl



import org.lwjgl.opengl._
import org.lwjgl.util.glu._



class Macrogl() {
  private val maxResultSize = 16
  private val tmpByte = Macrogl.createByteData(maxResultSize)
  private val tmpShort = Macrogl.createShortData(maxResultSize)
  private val tmpInt = Macrogl.createIntData(maxResultSize)
  private val tmpFloat = Macrogl.createFloatData(maxResultSize)
  private val tmpDouble = Macrogl.createDoubleData(maxResultSize)
  val invalidUniformLocation = -1

  /* public API */
  final def getWebGLRenderingContext(): Nothing =
    throw new UnsupportedOperationException("Available only in Scala.js")

  final def bytesPerShort = 2
  final def bytesPerInt = 4
  final def bytesPerFloat = 4
  final def bytesPerDouble = 8

  final def activeTexture(texture: Int) = {
    GL13.glActiveTexture(texture)
  }

  final def attachShader(program: Token.Program, shader: Token.Shader) = {
    GL20.glAttachShader(program, shader)
  }

  final def bindAttribLocation(p: Token.Program, index: Int, name: String) = {
    GL20.glBindAttribLocation(p, index, name)
  }

  final def bindBuffer(target: Int, buffer: Token.Buffer) = {
    GL15.glBindBuffer(target, buffer)
  }

  final def bindFramebuffer(target: Int, framebuffer: Token.FrameBuffer) = {
    GL30.glBindFramebuffer(target, framebuffer)
  }

  final def bindRenderbuffer(target: Int, renderbuffer: Token.RenderBuffer) = {
    GL30.glBindRenderbuffer(target, renderbuffer)
  }

  final def bindTexture(target: Int, texture: Token.Texture) = {
    GL11.glBindTexture(target, texture)
  }

  final def blendColor(red: Float, green: Float, blue: Float, alpha: Float) = {
    GL14.glBlendColor(red, green, blue, alpha)
  }

  final def blendEquation(mode: Int) = {
    GL14.glBlendEquation(mode)
  }

  final def blendEquationSeparate(modeRGB: Int, modeAlpha: Int) = {
    GL20.glBlendEquationSeparate(modeRGB, modeAlpha)
  }

  final def blendFunc(sfactor: Int, dfactor: Int) = {
    GL11.glBlendFunc(sfactor, dfactor)
  }

  final def blendFuncSeparate(srcfactorRGB: Int, dstfactorRGB: Int, srcfactorAlpha: Int, dstfactorAlpha: Int) = {
    GL14.glBlendFuncSeparate(srcfactorRGB, dstfactorRGB, srcfactorAlpha, dstfactorAlpha)
  }

  final def bufferData(target: Int, totalBytes: Long, usage: Int) {
    GL15.glBufferData(target, totalBytes, usage)
  }

  final def bufferData(target: Int, data: Data.Byte, usage: Int) = {
    GL15.glBufferData(target, data, usage)
  }
  final def bufferData(target: Int, data: Data.Short, usage: Int) = {
    GL15.glBufferData(target, data, usage)
  }
  final def bufferData(target: Int, data: Data.Int, usage: Int) = {
    GL15.glBufferData(target, data, usage)
  }
  final def bufferData(target: Int, data: Data.Float, usage: Int) = {
    GL15.glBufferData(target, data, usage)
  }
  final def bufferData(target: Int, data: Data.Double, usage: Int) = {
    GL15.glBufferData(target, data, usage)
  }

  final def bufferSubData(target: Int, offset: Long, data: Data.Byte) = {
    GL15.glBufferSubData(target, offset, data)
  }
  final def bufferSubData(target: Int, offset: Long, data: Data.Short) = {
    GL15.glBufferSubData(target, offset, data)
  }
  final def bufferSubData(target: Int, offset: Long, data: Data.Int) = {
    GL15.glBufferSubData(target, offset, data)
  }
  final def bufferSubData(target: Int, offset: Long, data: Data.Float) = {
    GL15.glBufferSubData(target, offset, data)
  }
  final def bufferSubData(target: Int, offset: Long, data: Data.Double) = {
    GL15.glBufferSubData(target, offset, data)
  }

  final def checkFramebufferStatus(target: Int): Int = {
    GL30.glCheckFramebufferStatus(target)
  }

  final def clear(mask: Int) = {
    GL11.glClear(mask)
  }

  final def clearColor(red: Float, green: Float, blue: Float, alpha: Float) = {
    GL11.glClearColor(red, green, blue, alpha)
  }

  final def clearDepth(depth: Double) = {
    GL11.glClearDepth(depth)
  }

  final def clearStencil(s: Int) = {
    GL11.glClearStencil(s)
  }

  final def colorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean) = {
    GL11.glColorMask(red, green, blue, alpha)
  }

  final def compileShader(shader: Token.Shader) = {
    GL20.glCompileShader(shader)
  }

  /*
   * Method compressedTexImage2D with signature glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int data_imageSize, long data_buffer_offset) discarded
   * Reason: not available in the API WebGL and the API GLES20 of Android
   */

  final def compressedTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    data: Data.Byte) = {
    GL13.glCompressedTexImage2D(target, level, internalformat, width, height, border, data)
  }

  final def compressedTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, data: Data.Byte) = {
    GL13.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, data)
  }

  final def copyTexImage2D(target: Int, level: Int, internalFormat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) = {
    GL11.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border)
  }

  final def copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int) = {
    GL11.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
  }

  final def createBuffer(): Token.Buffer = {
    GL15.glGenBuffers()
  }

  final def createFramebuffer(): Token.FrameBuffer = {
    GL30.glGenFramebuffers()
  }

  final def createProgram(): Token.Program = {
    GL20.glCreateProgram()
  }

  final def createRenderbuffer(): Token.RenderBuffer = {
    GL30.glGenRenderbuffers()
  }

  final def createShader(`type`: Int): Token.Shader = {
    GL20.glCreateShader(`type`)
  }

  final def createTexture(): Token.Texture = {
    GL11.glGenTextures()
  }

  final def cullFace(mode: Int) = {
    GL11.glCullFace(mode)
  }

  final def deleteBuffer(buffer: Token.Buffer) = {
    GL15.glDeleteBuffers(buffer)
  }

  final def deleteFramebuffer(framebuffer: Token.FrameBuffer) = {
    GL30.glDeleteFramebuffers(framebuffer)
  }

  final def deleteProgram(program: Token.Program) = {
    GL20.glDeleteProgram(program)
  }

  final def deleteRenderbuffer(renderbuffer: Token.RenderBuffer) = {
    GL30.glDeleteRenderbuffers(renderbuffer)
  }

  final def deleteShader(shader: Token.Shader) = {
    GL20.glDeleteShader(shader)
  }

  final def deleteTexture(texture: Token.Texture) = {
    GL11.glDeleteTextures(texture)
  }

  final def depthFunc(func: Int) = {
    GL11.glDepthFunc(func)
  }

  final def depthMask(flag: Boolean) = {
    GL11.glDepthMask(flag)
  }

  final def depthRange(zNear: Double, zFar: Double) = {
    GL11.glDepthRange(zNear, zFar)
  }

  final def detachShader(program: Token.Program, shader: Token.Shader) = {
    GL20.glDetachShader(program, shader)
  }

  final def disable(cap: Int) = {
    GL11.glDisable(cap)
  }

  final def disableVertexAttribArray(index: Int) = {
    GL20.glDisableVertexAttribArray(index)
  }

  final def drawArrays(mode: Int, first: Int, count: Int) = {
    GL11.glDrawArrays(mode, first, count)
  }

  /*
   * Method drawElements with signature glDrawElements(int mode, *Buffer indices) discarded
   * Reason: not available in the API WebGL
   * Note: available in the API GLES20 of Android with the signature glDrawElements(int mode, int count, int type, Buffer indices)
   * Note: the following available method requires the use of an element array buffer currently bound to ELEMENT_ARRAY_BUFFER
   */

  final def drawElements(mode: Int, count: Int, `type`: Int, offset: Long) = {
    // may be a good idea to check that an element array buffer is currently bound
    GL11.glDrawElements(mode, count, `type`, offset)
  }

  final def enable(cap: Int) = {
    GL11.glEnable(cap)
  }

  final def enableVertexAttribArray(index: Int) = {
    GL20.glEnableVertexAttribArray(index)
  }

  final def finish() {
    GL11.glFinish()
  }

  final def flush() {
    GL11.glFlush()
  }

  final def framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Token.RenderBuffer) = {
    GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
  }

  final def framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Token.Texture, level: Int) = {
    GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level)
  }

  final def frontFace(mode: Int) = {
    GL11.glFrontFace(mode)
  }

  final def generateMipmap(target: Int) = {
    GL30.glGenerateMipmap(target)
  }

  final def getActiveAttrib(program: Token.Program, index: Int): ActiveInfo = {
    val nameMaxSize = this.getProgramParameteri(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH)
    this.tmpInt.clear()
    val name = GL20.glGetActiveAttrib(program, index, nameMaxSize, this.tmpInt)
    ActiveInfo(this.tmpInt.get(0), this.tmpInt.get(1), name)
  }

  final def getActiveUniform(program: Token.Program, index: Int): ActiveInfo = {
    val nameMaxSize = this.getProgramParameteri(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH)
    this.tmpInt.clear()
    val name = GL20.glGetActiveUniform(program, index, nameMaxSize, this.tmpInt)
    ActiveInfo(this.tmpInt.get(0), this.tmpInt.get(1), name)
  }

  final def getAttachedShaders(program: Token.Program): Array[Token.Shader] = {
    val maxCount = this.getProgramParameteri(program, GL20.GL_ATTACHED_SHADERS)
    val buffer = Macrogl.createIntData(maxCount)
    this.tmpInt.clear()
    GL20.glGetAttachedShaders(program, this.tmpInt, buffer)
    val count = this.tmpInt.get(0)
    val array = new Array[Token.Shader](count)
    for (i <- 0 until count) {
      array(i) = buffer.get(i)
    }
    array
  }

  final def getAttribLocation(program: Token.Program, name: String): Int = {
    GL20.glGetAttribLocation(program, name)
  }

  final def getBufferParameteri(target: Int, pname: Int): Int = {
    GL15.glGetBufferParameteri(target, pname)
  }

  /*
   * This set of function is a big mess around the different systems due to the fact that the returned value can be pretty
   * much anything.
   * 
   * On good old C, LWJGL and Android GLES20: glGet is divided between glGetBooleanv, glGetFloatv, glGetDoublev,
   * glGetIntegerv and glGetString.
   * Please note that it can actually store SEVERAL values in the provided pointer/buffer.
   * 
   * On WebGL, things get a bit tricky. There is a single function getParameter that just return something of type Any (yay...
   * the joy of dynamic typing).
   */

  final def getParameterBuffer(pname: Int): Token.Buffer = {
    GL11.glGetInteger(pname)
  }

  final def getParameterTexture(pname: Int): Token.Texture = {
    GL11.glGetInteger(pname)
  }

  final def getParameterFramebuffer(pname: Int): Token.FrameBuffer = {
    GL11.glGetInteger(pname)
  }

  final def getParameterProgram(pname: Int): Token.Program = {
    GL11.glGetInteger(pname)
  }

  final def getParameterRenderbuffer(pname: Int): Token.RenderBuffer = {
    GL11.glGetInteger(pname)
  }

  final def getParameterShader(pname: Int): Token.Shader = {
    GL11.glGetInteger(pname)
  }

  final def getParameterString(pname: Int): String = {
    GL11.glGetString(pname)
  }

  final def getParameteri(pname: Int): Int = {
    GL11.glGetInteger(pname)
  }

  final def getParameteriv(pname: Int, outputs: Data.Int) = {
    GL11.glGetInteger(pname, outputs)
  }

  final def getParameterf(pname: Int): Float = {
    GL11.glGetFloat(pname)
  }

  final def getParameterfv(pname: Int, outputs: Data.Float) = {
    GL11.glGetFloat(pname, outputs)
  }

  final def getParameterb(pname: Int): Boolean = {
    GL11.glGetBoolean(pname)
  }

  final def getParameterbv(pname: Int, outputs: Data.Byte) = {
    GL11.glGetBoolean(pname, outputs)
  }

  final def getError(): Int = {
    GL11.glGetError()
  }

  final def getFramebufferAttachmentParameteri(target: Int, attachment: Int, pname: Int): Int = {
    GL30.glGetFramebufferAttachmentParameteri(target, attachment, pname)
  }

  final def getFramebufferAttachmentParameterRenderbuffer(target: Int, attachment: Int, pname: Int): Token.RenderBuffer = {
    GL30.glGetFramebufferAttachmentParameteri(target, attachment, pname)
  }

  final def getFramebufferAttachmentParameterTexture(target: Int, attachment: Int, pname: Int): Token.Texture = {
    GL30.glGetFramebufferAttachmentParameteri(target, attachment, pname)
  }

  final def getProgramParameteri(program: Token.Program, pname: Int): Int = {
    GL20.glGetProgrami(program, pname)
  }

  final def getProgramParameterb(program: Token.Program, pname: Int): Boolean = {
    GL20.glGetProgrami(program, pname) != Macrogl.FALSE
  }

  final def getProgramInfoLog(program: Token.Program): String = {
    val infoLogLength = this.getProgramParameteri(program, GL20.GL_INFO_LOG_LENGTH)
    GL20.glGetProgramInfoLog(program, infoLogLength)
  }

  final def getRenderbufferParameteri(target: Int, pname: Int): Int = {
    GL30.glGetRenderbufferParameteri(target, pname)
  }

  final def getShaderParameteri(shader: Token.Shader, pname: Int): Int = {
    GL20.glGetShaderi(shader, pname)
  }

  final def getShaderParameterb(shader: Token.Shader, pname: Int): Boolean = {
    GL20.glGetShaderi(shader, pname) != Macrogl.FALSE
  }

  final def getShaderPrecisionFormat(shadertype: Int, precisiontype: Int): PrecisionFormat = {
    this.tmpInt.clear()
    this.tmpInt.position(8)
    val tmpInt2 = tmpInt.slice() // No need to allocate a second buffer for this
    this.tmpInt.clear()
    this.tmpInt.limit(8)

    ARBES2Compatibility.glGetShaderPrecisionFormat(shadertype, precisiontype, tmpInt, tmpInt2)
    PrecisionFormat(tmpInt.get(0), tmpInt.get(1), tmpInt2.get(0))
  }

  final def getShaderInfoLog(shader: Token.Shader): String = {
    val infoLogLength = this.getShaderParameteri(shader, GL20.GL_INFO_LOG_LENGTH)
    GL20.glGetShaderInfoLog(shader, infoLogLength)
  }

  final def getShaderSource(shader: Token.Shader): String = {
    val sourceLength = this.getShaderParameteri(shader, GL20.GL_SHADER_SOURCE_LENGTH)
    GL20.glGetShaderSource(shader, sourceLength)
  }

  final def getTexParameteri(target: Int, pname: Int): Int = {
    GL11.glGetTexParameteri(target, pname)
  }

  final def getUniformi(program: Token.Program, location: Token.UniformLocation): Int = {
    this.tmpInt.clear()
    GL20.glGetUniform(program, location, this.tmpInt)
    this.tmpInt.get(0)
  }

  final def getUniformiv(program: Token.Program, location: Token.UniformLocation, outputs: Data.Int) = {
    GL20.glGetUniform(program, location, outputs)
  }

  final def getUniformf(program: Token.Program, location: Token.UniformLocation): Float = {
    this.tmpFloat.clear()
    GL20.glGetUniform(program, location, this.tmpFloat)
    this.tmpFloat.get(0)
  }

  final def getUniformfv(program: Token.Program, location: Token.UniformLocation, outputs: Data.Float) = {
    GL20.glGetUniform(program, location, outputs)
  }

  final def getUniformLocation(program: Token.Program, name: String): Token.UniformLocation = {
    GL20.glGetUniformLocation(program, name)

  }

  final def getVertexAttribi(index: Int, pname: Int): Int = {
    this.tmpInt.clear()
    GL20.glGetVertexAttrib(index, pname, this.tmpInt)
    this.tmpInt.get(0)
  }

  final def getVertexAttribf(index: Int, pname: Int): Float = {
    this.tmpFloat.clear()
    GL20.glGetVertexAttrib(index, pname, this.tmpFloat)
    this.tmpFloat.get(0)
  }

  final def getVertexAttribfv(index: Int, pname: Int, outputs: Data.Float) = {
    GL20.glGetVertexAttrib(index, pname, outputs)
  }

  final def getVertexAttribb(index: Int, pname: Int): Boolean = {
    this.getVertexAttribi(index, pname) != Macrogl.FALSE
  }

  /*
   * Method glGetVertexAttribPointer discarded
   * Reason: not available in the API GLES20 of Android
   * Note: (partially) present with the name getVertexAttribOffset in the API WebGL (limited to retrieving only the
   * offset, not the pointer)
   */

  final def hint(target: Int, mode: Int) = {
    GL11.glHint(target, mode)
  }

  final def isBuffer(buffer: Token.Buffer): Boolean = {
    GL15.glIsBuffer(buffer)
  }

  final def isEnabled(cap: Int): Boolean = {
    GL11.glIsEnabled(cap)
  }

  final def isFramebuffer(framebuffer: Token.FrameBuffer): Boolean = {
    GL30.glIsFramebuffer(framebuffer)
  }

  final def isProgram(program: Token.Program): Boolean = {
    GL20.glIsProgram(program)
  }

  final def isRenderbuffer(renderbuffer: Token.RenderBuffer): Boolean = {
    GL30.glIsRenderbuffer(renderbuffer)
  }

  final def isShader(shader: Token.Shader): Boolean = {
    GL20.glIsShader(shader)
  }

  final def isTexture(texture: Token.Texture): Boolean = {
    GL11.glIsTexture(texture)
  }

  final def lineWidth(width: Float) = {
    GL11.glLineWidth(width)
  }

  final def linkProgram(program: Token.Program) = {
    GL20.glLinkProgram(program)
  }

  final def pixelStorei(pname: Int, param: Int) = {
    GL11.glPixelStorei(pname, param)
  }

  final def polygonOffset(factor: Float, units: Float) = {
    GL11.glPolygonOffset(factor, units)
  }

  final def readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, `type`: Int, pixels: Data.Byte) = {
    GL11.glReadPixels(x, y, width, height, format, `type`, pixels)
  }
  final def readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, `type`: Int, pixels: Data.Short) = {
    GL11.glReadPixels(x, y, width, height, format, `type`, pixels)
  }
  final def readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, `type`: Int, pixels: Data.Int) = {
    GL11.glReadPixels(x, y, width, height, format, `type`, pixels)
  }
  final def readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, `type`: Int, pixels: Data.Float) = {
    GL11.glReadPixels(x, y, width, height, format, `type`, pixels)
  }
  final def readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, `type`: Int, pixels: Data.Double) = {
    GL11.glReadPixels(x, y, width, height, format, `type`, pixels)
  }

  final def renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) = {
    GL30.glRenderbufferStorage(target, internalformat, width, height)
  }

  final def sampleCoverage(value: Float, invert: Boolean) = {
    GL13.glSampleCoverage(value, invert)
  }

  final def scissor(x: Int, y: Int, width: Int, height: Int) = {
    GL11.glScissor(x, y, width, height)
  }

  final def shaderSource(shader: Token.Shader, source: String) = {
    GL20.glShaderSource(shader, source)
  }

  final def stencilFunc(func: Int, ref: Int, mask: Int) = {
    GL11.glStencilFunc(func, ref, mask)
  }

  final def stencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int) = {
    GL20.glStencilFuncSeparate(face, func, ref, mask)
  }

  final def stencilMask(mask: Int) = {
    GL11.glStencilMask(mask)
  }

  final def stencilMaskSeparate(face: Int, mask: Int) = {
    GL20.glStencilMaskSeparate(face, mask)
  }

  final def stencilOp(fail: Int, zfail: Int, zpass: Int) = {
    GL11.glStencilOp(fail, zfail, zpass)
  }

  final def stencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int) = {
    GL20.glStencilOpSeparate(face, sfail, dpfail, dppass)
  }

  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Byte) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)
  }
  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Short) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)
  }
  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Int) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)
  }
  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Float) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)
  }
  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Double) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)
  }
  final def texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
    format: Int, `type`: Int) = {
    GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, null: org.macrogl.Data.Byte)
  }

  final def texParameterf(target: Int, pname: Int, param: Float) = {
    GL11.glTexParameterf(target, pname, param)
  }

  final def texParameteri(target: Int, pname: Int, param: Int) = {
    GL11.glTexParameteri(target, pname, param)
  }

  final def texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, `type`: Int, pixels: Data.Byte) = {
    GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, `type`, pixels)
  }
  final def texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, `type`: Int, pixels: Data.Short) = {
    GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, `type`, pixels)
  }
  final def texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, `type`: Int, pixels: Data.Int) = {
    GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, `type`, pixels)
  }
  final def texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, `type`: Int, pixels: Data.Float) = {
    GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, `type`, pixels)
  }
  final def texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
    format: Int, `type`: Int, pixels: Data.Double) = {
    GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, `type`, pixels)
  }

  final def uniform1f(location: Token.UniformLocation, x: Float) = {
    GL20.glUniform1f(location, x)
  }

  final def uniform1fv(location: Token.UniformLocation, values: Data.Float) = {
    GL20.glUniform1(location, values)
  }

  final def uniform1i(location: Token.UniformLocation, x: Int) = {
    GL20.glUniform1i(location, x)
  }

  final def uniform1iv(location: Token.UniformLocation, values: Data.Int) = {
    GL20.glUniform1(location, values)
  }

  final def uniform2f(location: Token.UniformLocation, x: Float, y: Float) = {
    GL20.glUniform2f(location, x, y)
  }

  final def uniform2fv(location: Token.UniformLocation, values: Data.Float) = {
    GL20.glUniform2(location, values)
  }

  final def uniform2i(location: Token.UniformLocation, x: Int, y: Int) = {
    GL20.glUniform2i(location, x, y)
  }

  final def uniform2iv(location: Token.UniformLocation, values: Data.Int) = {
    GL20.glUniform2(location, values)
  }

  final def uniform3f(location: Token.UniformLocation, x: Float, y: Float, z: Float) = {
    GL20.glUniform3f(location, x, y, z)
  }

  final def uniform3fv(location: Token.UniformLocation, values: Data.Float) = {
    GL20.glUniform3(location, values)
  }

  final def uniform3i(location: Token.UniformLocation, x: Int, y: Int, z: Int) = {
    GL20.glUniform3i(location, x, y, z)
  }

  final def uniform3iv(location: Token.UniformLocation, values: Data.Int) = {
    GL20.glUniform3(location, values)
  }

  final def uniform4f(location: Token.UniformLocation, x: Float, y: Float, z: Float, w: Float) = {
    GL20.glUniform4f(location, x, y, z, w)
  }

  final def uniform4fv(location: Token.UniformLocation, values: Data.Float) = {
    GL20.glUniform4(location, values)
  }

  final def uniform4i(location: Token.UniformLocation, x: Int, y: Int, z: Int, w: Int) = {
    GL20.glUniform4i(location, x, y, z, w)
  }

  final def uniform4iv(location: Token.UniformLocation, values: Data.Int) = {
    GL20.glUniform4(location, values)
  }

  final def uniformMatrix2fv(location: Token.UniformLocation, transpose: Boolean, matrices: Data.Float) = {
    GL20.glUniformMatrix2(location, transpose, matrices)
  }

  final def uniformMatrix3fv(location: Token.UniformLocation, transpose: Boolean, matrices: Data.Float) = {
    GL20.glUniformMatrix3(location, transpose, matrices)
  }

  final def uniformMatrix4fv(location: Token.UniformLocation, transpose: Boolean, matrices: Data.Float) = {
    GL20.glUniformMatrix4(location, transpose, matrices)
  }

  final def useProgram(program: Token.Program) = {
    GL20.glUseProgram(program)
  }

  final def validateProgram(program: Token.Program) = {
    GL20.glValidateProgram(program)
  }

  final def vertexAttrib1f(index: Int, x: Float) = {
    GL20.glVertexAttrib1f(index, x)
  }

  final def vertexAttrib1fv(index: Int, values: Data.Float) = {
    val slice = values.slice
    GL20.glVertexAttrib1f(index, slice.get())
  }

  final def vertexAttrib2f(index: Int, x: Float, y: Float) = {
    GL20.glVertexAttrib2f(index, x, y)
  }

  final def vertexAttrib2fv(index: Int, values: Data.Float) = {
    val slice = values.slice
    GL20.glVertexAttrib2f(index, slice.get(), slice.get())
  }

  final def vertexAttrib3f(index: Int, x: Float, y: Float, z: Float) = {
    GL20.glVertexAttrib3f(index, x, y, z)
  }

  final def vertexAttrib3fv(index: Int, values: Data.Float) = {
    val slice = values.slice
    GL20.glVertexAttrib3f(index, slice.get(), slice.get(), slice.get())
  }

  final def vertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float) = {
    GL20.glVertexAttrib4f(index, x, y, z, w)
  }

  final def vertexAttrib4fv(index: Int, values: Data.Float) = {
    val slice = values.slice
    GL20.glVertexAttrib4f(index, slice.get(), slice.get(), slice.get(), slice.get())
  }

  /*
   * Method vertexAttribPointer with signature:
   * 
   *     glVertexAttribPointer(int index, int size, boolean normalized,
   *     int stride, *Buffer buffer) discarded
   * 
   * Reason: not available in the API WebGL
   * Note: available in the API GLES20 of Android
   * Note: the following available method requires the use of an array buffer currently
   * bound to ARRAY_BUFFER.
   */

  final def vertexAttribPointer(index: Int, size: Int, `type`: Int, normalized: Boolean,
    stride: Int, offset: Long) = {
    GL20.glVertexAttribPointer(index, size, `type`, normalized, stride, offset)
  }

  final def viewport(x: Int, y: Int, width: Int, height: Int) = {
    GL11.glViewport(x, y, width, height)
  }

  // Helper methods

  final def checkError() {
    val code = this.getError()
    if (code != Macrogl.NO_ERROR) {
      val msg = this.errorMessage(code)
      throw new MacroglException("Error " + code + " : " + msg)
    }
  }

  final def errorMessage(code: Int) = {
    val msg: String = GLU.gluErrorString(code)
    msg
  }

  final def errorMessage(): String = {
    val code = this.getError()
    this.errorMessage(code)
  }

  final def getCurrentProgram(): Token.Program = {
    this.getParameterProgram(Macrogl.CURRENT_PROGRAM)
  }

  final def getCurrentRenderbufferBinding(): Token.RenderBuffer = {
    this.getParameterRenderbuffer(Macrogl.RENDERBUFFER_BINDING)
  }

  final def shaderSource(shader: Token.Shader, srcarray: Array[CharSequence]) {
    this.shaderSource(shader, srcarray.mkString("\n"))
  }

  final def validProgram(program: Token.Program): Boolean = {
    (program > 0) && this.isProgram(program)
  }

  final def validShader(shader: Token.Shader): Boolean = {
    (shader > 0) && this.isShader(shader)
  }

  final def validBuffer(buffer: Token.Buffer): Boolean = {
    (buffer > 0) && this.isBuffer(buffer)
  }

  final def validUniformLocation(uloc: Token.UniformLocation): Boolean = {
    (uloc != invalidUniformLocation)
  }

  final def validFramebuffer(fb: Token.FrameBuffer): Boolean = {
    (fb > 0) && this.isFramebuffer(fb)
  }

  final def validRenderbuffer(rb: Token.RenderBuffer): Boolean = {
    (rb > 0) && this.isRenderbuffer(rb)
  }

  final def differentPrograms(p1: Token.Program, p2: Token.Program): Boolean = {
    p1 != p2
  }

  final def uniform2f(location: Token.UniformLocation, vec: org.macrogl.algebra.Vector2f): Unit = {
    this.uniform2f(location, vec.x, vec.y)
  }

  final def uniform3f(location: Token.UniformLocation, vec: org.macrogl.algebra.Vector3f): Unit = {
    this.uniform3f(location, vec.x, vec.y, vec.z)
  }

  final def uniform4f(location: Token.UniformLocation, vec: org.macrogl.algebra.Vector4f): Unit = {
    this.uniform4f(location, vec.x, vec.y, vec.z, vec.w)
  }

  final def uniformMatrix2f(location: Token.UniformLocation, mat: org.macrogl.algebra.Matrix2f): Unit = {
    this.tmpFloat.clear()
    mat.store(this.tmpFloat, org.macrogl.algebra.ColumnMajor)
    this.tmpFloat.flip()
    this.uniformMatrix2fv(location, false, this.tmpFloat.slice)
  }

  final def uniformMatrix3f(location: Token.UniformLocation, mat: org.macrogl.algebra.Matrix3f): Unit = {
    this.tmpFloat.clear()
    mat.store(this.tmpFloat, org.macrogl.algebra.ColumnMajor)
    this.tmpFloat.flip()
    this.uniformMatrix3fv(location, false, this.tmpFloat.slice)
  }

  final def uniformMatrix4f(location: Token.UniformLocation, mat: org.macrogl.algebra.Matrix4f): Unit = {
    this.tmpFloat.clear()
    mat.store(this.tmpFloat, org.macrogl.algebra.ColumnMajor)
    this.tmpFloat.flip()
    this.uniformMatrix4fv(location, false, this.tmpFloat.slice)
  }
}

object Macrogl {

  /* public API - constants */

  val FALSE = GL11.GL_FALSE
  val TRUE = GL11.GL_TRUE

  val DEPTH_BUFFER_BIT = GL11.GL_DEPTH_BUFFER_BIT
  val STENCIL_BUFFER_BIT = GL11.GL_STENCIL_BUFFER_BIT
  val COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT
  val POINTS = GL11.GL_POINTS
  val LINES = GL11.GL_LINES
  val LINE_LOOP = GL11.GL_LINE_LOOP
  val LINE_STRIP = GL11.GL_LINE_STRIP
  val TRIANGLES = GL11.GL_TRIANGLES
  val TRIANGLE_STRIP = GL11.GL_TRIANGLE_STRIP
  val TRIANGLE_FAN = GL11.GL_TRIANGLE_FAN
  val ZERO = GL11.GL_ZERO
  val ONE = GL11.GL_ONE
  val SRC_COLOR = GL11.GL_SRC_COLOR
  val ONE_MINUS_SRC_COLOR = GL11.GL_ONE_MINUS_SRC_COLOR
  val SRC_ALPHA = GL11.GL_SRC_ALPHA
  val ONE_MINUS_SRC_ALPHA = GL11.GL_ONE_MINUS_SRC_ALPHA
  val DST_ALPHA = GL11.GL_DST_ALPHA
  val ONE_MINUS_DST_ALPHA = GL11.GL_ONE_MINUS_DST_ALPHA
  val DST_COLOR = GL11.GL_DST_COLOR
  val ONE_MINUS_DST_COLOR = GL11.GL_ONE_MINUS_DST_COLOR
  val SRC_ALPHA_SATURATE = GL11.GL_SRC_ALPHA_SATURATE
  val FUNC_ADD = GL14.GL_FUNC_ADD
  val BLEND_EQUATION = GL14.GL_BLEND_EQUATION
  val BLEND_EQUATION_RGB = GL20.GL_BLEND_EQUATION_RGB
  val BLEND_EQUATION_ALPHA = GL20.GL_BLEND_EQUATION_ALPHA
  val FUNC_SUBTRACT = GL14.GL_FUNC_SUBTRACT
  val FUNC_REVERSE_SUBTRACT = GL14.GL_FUNC_REVERSE_SUBTRACT
  val BLEND_DST_RGB = GL14.GL_BLEND_DST_RGB
  val BLEND_SRC_RGB = GL14.GL_BLEND_SRC_RGB
  val BLEND_DST_ALPHA = GL14.GL_BLEND_DST_ALPHA
  val BLEND_SRC_ALPHA = GL14.GL_BLEND_SRC_ALPHA
  val CONSTANT_COLOR = GL11.GL_CONSTANT_COLOR
  val ONE_MINUS_CONSTANT_COLOR = GL11.GL_ONE_MINUS_CONSTANT_COLOR
  val CONSTANT_ALPHA = GL11.GL_CONSTANT_ALPHA
  val ONE_MINUS_CONSTANT_ALPHA = GL11.GL_ONE_MINUS_CONSTANT_ALPHA
  val BLEND_COLOR = GL14.GL_BLEND_COLOR
  val ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER
  val ELEMENT_ARRAY_BUFFER = GL15.GL_ELEMENT_ARRAY_BUFFER
  val ARRAY_BUFFER_BINDING = GL15.GL_ARRAY_BUFFER_BINDING
  val ELEMENT_ARRAY_BUFFER_BINDING = GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING
  val STREAM_DRAW = GL15.GL_STREAM_DRAW
  val STATIC_DRAW = GL15.GL_STATIC_DRAW
  val DYNAMIC_DRAW = GL15.GL_DYNAMIC_DRAW
  val BUFFER_SIZE = GL15.GL_BUFFER_SIZE
  val BUFFER_USAGE = GL15.GL_BUFFER_USAGE
  val CURRENT_VERTEX_ATTRIB = GL20.GL_CURRENT_VERTEX_ATTRIB
  val FRONT = GL11.GL_FRONT
  val BACK = GL11.GL_BACK
  val FRONT_AND_BACK = GL11.GL_FRONT_AND_BACK
  val CULL_FACE = GL11.GL_CULL_FACE
  val BLEND = GL11.GL_BLEND
  val DITHER = GL11.GL_DITHER
  val STENCIL_TEST = GL11.GL_STENCIL_TEST
  val DEPTH_TEST = GL11.GL_DEPTH_TEST
  val SCISSOR_TEST = GL11.GL_SCISSOR_TEST
  val POLYGON_OFFSET_FILL = GL11.GL_POLYGON_OFFSET_FILL
  val SAMPLE_ALPHA_TO_COVERAGE = GL13.GL_SAMPLE_ALPHA_TO_COVERAGE
  val SAMPLE_COVERAGE = GL13.GL_SAMPLE_COVERAGE
  val NO_ERROR = GL11.GL_NO_ERROR
  val INVALID_ENUM = GL11.GL_INVALID_ENUM
  val INVALID_VALUE = GL11.GL_INVALID_VALUE
  val INVALID_OPERATION = GL11.GL_INVALID_OPERATION
  val OUT_OF_MEMORY = GL11.GL_OUT_OF_MEMORY
  val CW = GL11.GL_CW
  val CCW = GL11.GL_CCW
  val LINE_WIDTH = GL11.GL_LINE_WIDTH
  val ALIASED_POINT_SIZE_RANGE = GL12.GL_ALIASED_POINT_SIZE_RANGE
  val ALIASED_LINE_WIDTH_RANGE = GL12.GL_ALIASED_LINE_WIDTH_RANGE
  val CULL_FACE_MODE = GL11.GL_CULL_FACE_MODE
  val FRONT_FACE = GL11.GL_FRONT_FACE
  val DEPTH_RANGE = GL11.GL_DEPTH_RANGE
  val DEPTH_WRITEMASK = GL11.GL_DEPTH_WRITEMASK
  val DEPTH_CLEAR_VALUE = GL11.GL_DEPTH_CLEAR_VALUE
  val DEPTH_FUNC = GL11.GL_DEPTH_FUNC
  val STENCIL_CLEAR_VALUE = GL11.GL_STENCIL_CLEAR_VALUE
  val STENCIL_FUNC = GL11.GL_STENCIL_FUNC
  val STENCIL_FAIL = GL11.GL_STENCIL_FAIL
  val STENCIL_PASS_DEPTH_FAIL = GL11.GL_STENCIL_PASS_DEPTH_FAIL
  val STENCIL_PASS_DEPTH_PASS = GL11.GL_STENCIL_PASS_DEPTH_PASS
  val STENCIL_REF = GL11.GL_STENCIL_REF
  val STENCIL_VALUE_MASK = GL11.GL_STENCIL_VALUE_MASK
  val STENCIL_WRITEMASK = GL11.GL_STENCIL_WRITEMASK
  val STENCIL_BACK_FUNC = GL20.GL_STENCIL_BACK_FUNC
  val STENCIL_BACK_FAIL = GL20.GL_STENCIL_BACK_FAIL
  val STENCIL_BACK_PASS_DEPTH_FAIL = GL20.GL_STENCIL_BACK_PASS_DEPTH_FAIL
  val STENCIL_BACK_PASS_DEPTH_PASS = GL20.GL_STENCIL_BACK_PASS_DEPTH_PASS
  val STENCIL_BACK_REF = GL20.GL_STENCIL_BACK_REF
  val STENCIL_BACK_VALUE_MASK = GL20.GL_STENCIL_BACK_VALUE_MASK
  val STENCIL_BACK_WRITEMASK = GL20.GL_STENCIL_BACK_WRITEMASK
  val VIEWPORT = GL11.GL_VIEWPORT
  val SCISSOR_BOX = GL11.GL_SCISSOR_BOX
  val COLOR_CLEAR_VALUE = GL11.GL_COLOR_CLEAR_VALUE
  val COLOR_WRITEMASK = GL11.GL_COLOR_WRITEMASK
  val UNPACK_ALIGNMENT = GL11.GL_UNPACK_ALIGNMENT
  val PACK_ALIGNMENT = GL11.GL_PACK_ALIGNMENT
  val MAX_TEXTURE_SIZE = GL11.GL_MAX_TEXTURE_SIZE
  val MAX_VIEWPORT_DIMS = GL11.GL_MAX_VIEWPORT_DIMS
  val SUBPIXEL_BITS = GL11.GL_SUBPIXEL_BITS
  val RED_BITS = GL11.GL_RED_BITS
  val GREEN_BITS = GL11.GL_GREEN_BITS
  val BLUE_BITS = GL11.GL_BLUE_BITS
  val ALPHA_BITS = GL11.GL_ALPHA_BITS
  val DEPTH_BITS = GL11.GL_DEPTH_BITS
  val STENCIL_BITS = GL11.GL_STENCIL_BITS
  val POLYGON_OFFSET_UNITS = GL11.GL_POLYGON_OFFSET_UNITS
  val POLYGON_OFFSET_FACTOR = GL11.GL_POLYGON_OFFSET_FACTOR
  val TEXTURE_BINDING_2D = GL11.GL_TEXTURE_BINDING_2D
  val SAMPLE_BUFFERS = GL13.GL_SAMPLE_BUFFERS
  val SAMPLES = GL13.GL_SAMPLES
  val SAMPLE_COVERAGE_VALUE = GL13.GL_SAMPLE_COVERAGE_VALUE
  val SAMPLE_COVERAGE_INVERT = GL13.GL_SAMPLE_COVERAGE_INVERT
  val COMPRESSED_TEXTURE_FORMATS = GL13.GL_COMPRESSED_TEXTURE_FORMATS
  val DONT_CARE = GL11.GL_DONT_CARE
  val FASTEST = GL11.GL_FASTEST
  val NICEST = GL11.GL_NICEST
  val GENERATE_MIPMAP_HINT = GL14.GL_GENERATE_MIPMAP_HINT
  val BYTE = GL11.GL_BYTE
  val UNSIGNED_BYTE = GL11.GL_UNSIGNED_BYTE
  val SHORT = GL11.GL_SHORT
  val UNSIGNED_SHORT = GL11.GL_UNSIGNED_SHORT
  val INT = GL11.GL_INT
  val UNSIGNED_INT = GL11.GL_UNSIGNED_INT
  val FLOAT = GL11.GL_FLOAT
  val DEPTH_COMPONENT = GL11.GL_DEPTH_COMPONENT
  val ALPHA = GL11.GL_ALPHA
  val RGB = GL11.GL_RGB
  val RGBA = GL11.GL_RGBA
  val LUMINANCE = GL11.GL_LUMINANCE
  val LUMINANCE_ALPHA = GL11.GL_LUMINANCE_ALPHA
  val UNSIGNED_SHORT_4_4_4_4 = GL12.GL_UNSIGNED_SHORT_4_4_4_4
  val UNSIGNED_SHORT_5_5_5_1 = GL12.GL_UNSIGNED_SHORT_5_5_5_1
  val UNSIGNED_SHORT_5_6_5 = GL12.GL_UNSIGNED_SHORT_5_6_5
  val FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER
  val VERTEX_SHADER = GL20.GL_VERTEX_SHADER
  val MAX_VERTEX_ATTRIBS = GL20.GL_MAX_VERTEX_ATTRIBS
  val MAX_VERTEX_UNIFORM_VECTORS = ARBES2Compatibility.GL_MAX_VERTEX_UNIFORM_VECTORS
  val MAX_VARYING_VECTORS = ARBES2Compatibility.GL_MAX_VARYING_VECTORS
  val MAX_COMBINED_TEXTURE_IMAGE_UNITS = GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
  val MAX_VERTEX_TEXTURE_IMAGE_UNITS = GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS
  val MAX_TEXTURE_IMAGE_UNITS = GL20.GL_MAX_TEXTURE_IMAGE_UNITS
  val MAX_FRAGMENT_UNIFORM_VECTORS = ARBES2Compatibility.GL_MAX_FRAGMENT_UNIFORM_VECTORS
  val SHADER_TYPE = GL20.GL_SHADER_TYPE
  val DELETE_STATUS = GL20.GL_DELETE_STATUS
  val LINK_STATUS = GL20.GL_LINK_STATUS
  val VALIDATE_STATUS = GL20.GL_VALIDATE_STATUS
  val ATTACHED_SHADERS = GL20.GL_ATTACHED_SHADERS
  val ACTIVE_UNIFORMS = GL20.GL_ACTIVE_UNIFORMS
  val ACTIVE_ATTRIBUTES = GL20.GL_ACTIVE_ATTRIBUTES
  val SHADING_LANGUAGE_VERSION = GL20.GL_SHADING_LANGUAGE_VERSION
  val CURRENT_PROGRAM = GL20.GL_CURRENT_PROGRAM
  val NEVER = GL11.GL_NEVER
  val LESS = GL11.GL_LESS
  val EQUAL = GL11.GL_EQUAL
  val LEQUAL = GL11.GL_LEQUAL
  val GREATER = GL11.GL_GREATER
  val NOTEQUAL = GL11.GL_NOTEQUAL
  val GEQUAL = GL11.GL_GEQUAL
  val ALWAYS = GL11.GL_ALWAYS
  val KEEP = GL11.GL_KEEP
  val REPLACE = GL11.GL_REPLACE
  val INCR = GL11.GL_INCR
  val DECR = GL11.GL_DECR
  val INVERT = GL11.GL_INVERT
  val INCR_WRAP = GL14.GL_INCR_WRAP
  val DECR_WRAP = GL14.GL_DECR_WRAP
  val VENDOR = GL11.GL_VENDOR
  val RENDERER = GL11.GL_RENDERER
  val VERSION = GL11.GL_VERSION
  val NEAREST = GL11.GL_NEAREST
  val LINEAR = GL11.GL_LINEAR
  val NEAREST_MIPMAP_NEAREST = GL11.GL_NEAREST_MIPMAP_NEAREST
  val LINEAR_MIPMAP_NEAREST = GL11.GL_LINEAR_MIPMAP_NEAREST
  val NEAREST_MIPMAP_LINEAR = GL11.GL_NEAREST_MIPMAP_LINEAR
  val LINEAR_MIPMAP_LINEAR = GL11.GL_LINEAR_MIPMAP_LINEAR
  val TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER
  val TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER
  val TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S
  val TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T
  val TEXTURE_2D = GL11.GL_TEXTURE_2D
  val TEXTURE = GL11.GL_TEXTURE
  val TEXTURE_CUBE_MAP = GL13.GL_TEXTURE_CUBE_MAP
  val TEXTURE_BINDING_CUBE_MAP = GL13.GL_TEXTURE_BINDING_CUBE_MAP
  val TEXTURE_CUBE_MAP_POSITIVE_X = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X
  val TEXTURE_CUBE_MAP_NEGATIVE_X = GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X
  val TEXTURE_CUBE_MAP_POSITIVE_Y = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y
  val TEXTURE_CUBE_MAP_NEGATIVE_Y = GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
  val TEXTURE_CUBE_MAP_POSITIVE_Z = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z
  val TEXTURE_CUBE_MAP_NEGATIVE_Z = GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
  val MAX_CUBE_MAP_TEXTURE_SIZE = GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE
  val TEXTURE0 = GL13.GL_TEXTURE0
  val TEXTURE1 = GL13.GL_TEXTURE1
  val TEXTURE2 = GL13.GL_TEXTURE2
  val TEXTURE3 = GL13.GL_TEXTURE3
  val TEXTURE4 = GL13.GL_TEXTURE4
  val TEXTURE5 = GL13.GL_TEXTURE5
  val TEXTURE6 = GL13.GL_TEXTURE6
  val TEXTURE7 = GL13.GL_TEXTURE7
  val TEXTURE8 = GL13.GL_TEXTURE8
  val TEXTURE9 = GL13.GL_TEXTURE9
  val TEXTURE10 = GL13.GL_TEXTURE10
  val TEXTURE11 = GL13.GL_TEXTURE11
  val TEXTURE12 = GL13.GL_TEXTURE12
  val TEXTURE13 = GL13.GL_TEXTURE13
  val TEXTURE14 = GL13.GL_TEXTURE14
  val TEXTURE15 = GL13.GL_TEXTURE15
  val TEXTURE16 = GL13.GL_TEXTURE16
  val TEXTURE17 = GL13.GL_TEXTURE17
  val TEXTURE18 = GL13.GL_TEXTURE18
  val TEXTURE19 = GL13.GL_TEXTURE19
  val TEXTURE20 = GL13.GL_TEXTURE20
  val TEXTURE21 = GL13.GL_TEXTURE21
  val TEXTURE22 = GL13.GL_TEXTURE22
  val TEXTURE23 = GL13.GL_TEXTURE23
  val TEXTURE24 = GL13.GL_TEXTURE24
  val TEXTURE25 = GL13.GL_TEXTURE25
  val TEXTURE26 = GL13.GL_TEXTURE26
  val TEXTURE27 = GL13.GL_TEXTURE27
  val TEXTURE28 = GL13.GL_TEXTURE28
  val TEXTURE29 = GL13.GL_TEXTURE29
  val TEXTURE30 = GL13.GL_TEXTURE30
  val TEXTURE31 = GL13.GL_TEXTURE31
  val ACTIVE_TEXTURE = GL13.GL_ACTIVE_TEXTURE
  val REPEAT = GL11.GL_REPEAT
  val CLAMP_TO_EDGE = GL12.GL_CLAMP_TO_EDGE
  val MIRRORED_REPEAT = GL14.GL_MIRRORED_REPEAT
  val FLOAT_VEC2 = GL20.GL_FLOAT_VEC2
  val FLOAT_VEC3 = GL20.GL_FLOAT_VEC3
  val FLOAT_VEC4 = GL20.GL_FLOAT_VEC4
  val INT_VEC2 = GL20.GL_INT_VEC2
  val INT_VEC3 = GL20.GL_INT_VEC3
  val INT_VEC4 = GL20.GL_INT_VEC4
  val BOOL = GL20.GL_BOOL
  val BOOL_VEC2 = GL20.GL_BOOL_VEC2
  val BOOL_VEC3 = GL20.GL_BOOL_VEC3
  val BOOL_VEC4 = GL20.GL_BOOL_VEC4
  val FLOAT_MAT2 = GL20.GL_FLOAT_MAT2
  val FLOAT_MAT3 = GL20.GL_FLOAT_MAT3
  val FLOAT_MAT4 = GL20.GL_FLOAT_MAT4
  val SAMPLER_2D = GL20.GL_SAMPLER_2D
  val SAMPLER_CUBE = GL20.GL_SAMPLER_CUBE
  val VERTEX_ATTRIB_ARRAY_ENABLED = GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED
  val VERTEX_ATTRIB_ARRAY_SIZE = GL20.GL_VERTEX_ATTRIB_ARRAY_SIZE
  val VERTEX_ATTRIB_ARRAY_STRIDE = GL20.GL_VERTEX_ATTRIB_ARRAY_STRIDE
  val VERTEX_ATTRIB_ARRAY_TYPE = GL20.GL_VERTEX_ATTRIB_ARRAY_TYPE
  val VERTEX_ATTRIB_ARRAY_NORMALIZED = GL20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED
  val VERTEX_ATTRIB_ARRAY_POINTER = GL20.GL_VERTEX_ATTRIB_ARRAY_POINTER
  val VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING
  val COMPILE_STATUS = GL20.GL_COMPILE_STATUS
  val LOW_FLOAT = ARBES2Compatibility.GL_LOW_FLOAT
  val MEDIUM_FLOAT = ARBES2Compatibility.GL_MEDIUM_FLOAT
  val HIGH_FLOAT = ARBES2Compatibility.GL_HIGH_FLOAT
  val LOW_INT = ARBES2Compatibility.GL_LOW_INT
  val MEDIUM_INT = ARBES2Compatibility.GL_MEDIUM_INT
  val HIGH_INT = ARBES2Compatibility.GL_HIGH_INT
  val FRAMEBUFFER = GL30.GL_FRAMEBUFFER
  val RENDERBUFFER = GL30.GL_RENDERBUFFER
  val RGBA4 = GL11.GL_RGBA4
  val RGB5_A1 = GL11.GL_RGB5_A1
  val RGB565 = ARBES2Compatibility.GL_RGB565
  val DEPTH_COMPONENT16 = GL14.GL_DEPTH_COMPONENT16
  val STENCIL_INDEX = GL11.GL_STENCIL_INDEX
  val STENCIL_INDEX8 = GL30.GL_STENCIL_INDEX8
  val DEPTH_STENCIL = GL30.GL_DEPTH_STENCIL
  val RENDERBUFFER_WIDTH = GL30.GL_RENDERBUFFER_WIDTH
  val RENDERBUFFER_HEIGHT = GL30.GL_RENDERBUFFER_HEIGHT
  val RENDERBUFFER_INTERNAL_FORMAT = GL30.GL_RENDERBUFFER_INTERNAL_FORMAT
  val RENDERBUFFER_RED_SIZE = GL30.GL_RENDERBUFFER_RED_SIZE
  val RENDERBUFFER_GREEN_SIZE = GL30.GL_RENDERBUFFER_GREEN_SIZE
  val RENDERBUFFER_BLUE_SIZE = GL30.GL_RENDERBUFFER_BLUE_SIZE
  val RENDERBUFFER_ALPHA_SIZE = GL30.GL_RENDERBUFFER_ALPHA_SIZE
  val RENDERBUFFER_DEPTH_SIZE = GL30.GL_RENDERBUFFER_DEPTH_SIZE
  val RENDERBUFFER_STENCIL_SIZE = GL30.GL_RENDERBUFFER_STENCIL_SIZE
  val FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE
  val FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME
  val FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = GL30.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL
  val FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = GL30.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE
  val COLOR_ATTACHMENT0 = GL30.GL_COLOR_ATTACHMENT0
  val DEPTH_ATTACHMENT = GL30.GL_DEPTH_ATTACHMENT
  val STENCIL_ATTACHMENT = GL30.GL_STENCIL_ATTACHMENT
  val DEPTH_STENCIL_ATTACHMENT = GL30.GL_DEPTH_STENCIL_ATTACHMENT
  val NONE = GL11.GL_NONE
  val FRAMEBUFFER_COMPLETE = GL30.GL_FRAMEBUFFER_COMPLETE
  val FRAMEBUFFER_INCOMPLETE_ATTACHMENT = GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT
  val FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT
  val FRAMEBUFFER_INCOMPLETE_DIMENSIONS = EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT // no present in standard OpenGL 3.0
  // GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER not present in WebGL & Android, moved to Macroglex
  // GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER not present in WebGL & Android, moved to Macroglex
  val FRAMEBUFFER_UNSUPPORTED = GL30.GL_FRAMEBUFFER_UNSUPPORTED
  val FRAMEBUFFER_BINDING = GL30.GL_FRAMEBUFFER_BINDING
  val RENDERBUFFER_BINDING = GL30.GL_RENDERBUFFER_BINDING
  val MAX_RENDERBUFFER_SIZE = GL30.GL_MAX_RENDERBUFFER_SIZE
  val INVALID_FRAMEBUFFER_OPERATION = GL30.GL_INVALID_FRAMEBUFFER_OPERATION

  /* public API - methods */

  final def createByteData(sz: Int): Data.Byte = {
    org.lwjgl.BufferUtils.createByteBuffer(sz)
  }

  final def createShortData(sz: Int): Data.Short = {
    org.lwjgl.BufferUtils.createShortBuffer(sz)
  }

  final def createIntData(sz: Int): Data.Int = {
    org.lwjgl.BufferUtils.createIntBuffer(sz)
  }

  final def createFloatData(sz: Int): Data.Float = {
    org.lwjgl.BufferUtils.createFloatBuffer(sz)
  }

  final def createDoubleData(sz: Int): Data.Double = {
    org.lwjgl.BufferUtils.createDoubleBuffer(sz)
  }

  /* public API - implicits */

  implicit val default = new Macrogl()

  /* implementation-specific methods */

}
