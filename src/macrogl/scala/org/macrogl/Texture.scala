package org.macrogl



import scala.collection._



final class Texture(val target: Int)(val init: Texture => Unit)(implicit gl: Macroglex) extends Handle {
  private var ttoken = Token.Texture.invalid

  def token = ttoken

  def binding = target match {
    case Macrogl.TEXTURE_2D => Macrogl.TEXTURE_BINDING_2D
    case Macroglex.TEXTURE_1D => Macroglex.TEXTURE_BINDING_1D
    case _ => throw new UnsupportedOperationException
  }

  def acquire() {
    release()
    ttoken = gl.createTexture()
    gl.bindTexture(target, ttoken)
    init(this)
    gl.bindTexture(target, Token.Texture.none)
  }

  object param {

    def update(name: Int, v: Float) {
      gl.texParameterf(target, name, v)
    }
  
    def update(name: Int, v: Int) {
      gl.texParameteri(target, name, v)
    }
  
    def int(name: Int): Int = {
      gl.getTexParameteri(target, name)
    }
  }

  def minFilter = param.int(Macrogl.TEXTURE_MIN_FILTER)

  def minFilter_=(v: Int) = param(Macrogl.TEXTURE_MIN_FILTER) = v

  def magFilter = param.int(Macrogl.TEXTURE_MAG_FILTER)

  def magFilter_=(v: Int) = param(Macrogl.TEXTURE_MAG_FILTER) = v

  def wrapS = param.int(Macrogl.TEXTURE_WRAP_S)

  def wrapS_=(v: Int) = param(Macrogl.TEXTURE_WRAP_S) = v

  def wrapT = param.int(Macrogl.TEXTURE_WRAP_T)

  def wrapT_=(v: Int) = param(Macrogl.TEXTURE_WRAP_T) = v

  def compareMode = param.int(Macroglex.TEXTURE_COMPARE_MODE)

  def compareMode_=(v: Int) = param(Macroglex.TEXTURE_COMPARE_MODE) = v

  def compareFunc = param.int(Macroglex.TEXTURE_COMPARE_FUNC)

  def compareFunc_=(v: Int) = param(Macroglex.TEXTURE_COMPARE_FUNC) = v

  def depthTextureMode = param.int(Macroglex.DEPTH_TEXTURE_MODE)

  def depthTextureMode_=(v: Int) = param(Macroglex.DEPTH_TEXTURE_MODE) = v

  def allocateImage1D(level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int, data: Data = null) {
    target match {
      case Macroglex.TEXTURE_1D => data match {
        case null           => gl.texImage1D(target, level, internalFormat, wdt, border, format, dataType, null: Data.Int)
        case data: Data.Int => gl.texImage1D(target, level, internalFormat, wdt, border, format, dataType, data)
        case _ => throw new UnsupportedOperationException(s"Unknown data format: ${data.getClass}")
      }
      case _ => throw new UnsupportedOperationException("Texture is not 1D.")
    }
  }

  def allocateImage2D(level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int, data: Data = null) {
    target match {
      case Macrogl.TEXTURE_2D => data match {
        case null            => gl.texImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, null: Data.Int)
        case data: Data.Int  => gl.texImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, data)
        case data: Data.Byte => gl.texImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, data)
        case _ => throw new UnsupportedOperationException(s"Unknown data format: ${data.getClass}")
      }
      case _ => throw new UnsupportedOperationException("Texture is not 2D.")
    }
  }

  def release() {
    if (ttoken != -1) {
      gl.deleteTexture(ttoken)
      ttoken = -1
    }
  }

}


object Texture {

  def apply(target: Int)(init: Texture => Unit): Texture = new Texture(target)(init)

}
