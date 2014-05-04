package org.macrogl
package ex

class Texture(target: Int)(init: org.macrogl.Texture => Unit)(implicit glex: Macroglex) extends org.macrogl.Texture(target)(init)(glex) {
  override def binding = target match {
    case Macroglex.TEXTURE_1D => Macroglex.TEXTURE_BINDING_1D
    case _ => super.binding
  }

  def compareMode = param.int(Macroglex.TEXTURE_COMPARE_MODE)

  def compareMode_=(v: Int) = param(Macroglex.TEXTURE_COMPARE_MODE) = v

  def compareFunc = param.int(Macroglex.TEXTURE_COMPARE_FUNC)

  def compareFunc_=(v: Int) = param(Macroglex.TEXTURE_COMPARE_FUNC) = v

  def depthTextureMode = param.int(Macroglex.DEPTH_TEXTURE_MODE)

  def depthTextureMode_=(v: Int) = param(Macroglex.DEPTH_TEXTURE_MODE) = v

  def allocateImage1D(level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int, data: Data = null) {
    target match {
      case Macroglex.TEXTURE_1D => data match {
        case null => glex.texImage1D(target, level, internalFormat, wdt, border, format, dataType, null: Data.Int)
        case data: Data.Int => glex.texImage1D(target, level, internalFormat, wdt, border, format, dataType, data)
        case _ => throw new UnsupportedOperationException(s"Unknown data format: ${data.getClass}")
      }
      case _ => throw new UnsupportedOperationException("Texture is not 1D.")
    }
  }
}