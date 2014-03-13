package org.macrogl



import scala.collection._



final class Texture(val target: Int)(val init: Texture => Unit)(implicit gl: Macrogl) extends Handle {
  private var ttoken = Token.Texture.invalid

  def token = ttoken

  def binding = target match {
    case Macrogl.GL_TEXTURE_2D => Macrogl.GL_TEXTURE_BINDING_2D
    case Macrogl.GL_TEXTURE_1D => Macrogl.GL_TEXTURE_BINDING_1D
    case _ => throw new UnsupportedOperationException
  }

  def acquire() {
    release()
    ttoken = gl.genTextures()
    init(this)
  }

  object param {

    def update(name: Int, v: Float) {
      gl.bindTexture(Macrogl.GL_TEXTURE_2D, ttoken)
      gl.texParameterf(target, name, v)
    }
  
    def update(name: Int, v: Int) {
      gl.bindTexture(Macrogl.GL_TEXTURE_2D, ttoken)
      gl.texParameteri(target, name, v)
    }
  
    def int(name: Int): Int = {
      gl.getTexParameteri(target, name)
    }
  }

  def minFilter = param.int(Macrogl.GL_TEXTURE_MIN_FILTER)

  def minFilter_=(v: Int) = param(Macrogl.GL_TEXTURE_MIN_FILTER) = v

  def magFilter = param.int(Macrogl.GL_TEXTURE_MAG_FILTER)

  def magFilter_=(v: Int) = param(Macrogl.GL_TEXTURE_MAG_FILTER) = v

  def wrapS = param.int(Macrogl.GL_TEXTURE_WRAP_S)

  def wrapS_=(v: Int) = param(Macrogl.GL_TEXTURE_WRAP_S) = v

  def wrapT = param.int(Macrogl.GL_TEXTURE_WRAP_T)

  def wrapT_=(v: Int) = param(Macrogl.GL_TEXTURE_WRAP_T) = v

  def compareMode = param.int(Macrogl.GL_TEXTURE_COMPARE_MODE)

  def compareMode_=(v: Int) = param(Macrogl.GL_TEXTURE_COMPARE_MODE) = v

  def compareFunc = param.int(Macrogl.GL_TEXTURE_COMPARE_FUNC)

  def compareFunc_=(v: Int) = param(Macrogl.GL_TEXTURE_COMPARE_FUNC) = v

  def depthTextureMode = param.int(Macrogl.GL_DEPTH_TEXTURE_MODE)

  def depthTextureMode_=(v: Int) = param(Macrogl.GL_DEPTH_TEXTURE_MODE) = v

  def allocateImage1D(level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int) {
    target match {
      case Macrogl.GL_TEXTURE_1D => gl.texImage1D(target, level, internalFormat, wdt, border, format, dataType, null: Buffer.Int)
      case _ => throw new UnsupportedOperationException
    }
  }

  def allocateImage2D(level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int) {
    target match {
      case Macrogl.GL_TEXTURE_2D => gl.texImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, null: Buffer.Int)
      case _ => throw new UnsupportedOperationException
    }
  }

  def release() {
    if (ttoken != -1) {
      gl.deleteTextures(ttoken)
      ttoken = -1
    }
  }

}


object Texture {

  def apply(target: Int)(init: Texture => Unit): Texture = new Texture(target)(init)

}
