package org.macrogl



import scala.collection._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL14._
import org.lwjgl.opengl.GL20._



final class Texture(val target: Int)(val init: Texture => Unit) extends Handle {
  private var tindex = -1

  def index = tindex

  def binding = target match {
    case GL_TEXTURE_2D => GL_TEXTURE_BINDING_2D
    case _ => throw new UnsupportedOperationException
  }

  def acquire() {
    release()
    tindex = glGenTextures()
    init(this)
  }

  def update(name: Int, v: Float) {
    glBindTexture(GL_TEXTURE_2D, tindex)
    glTexParameterf(target, name, v)
  }

  def update(name: Int, v: Int) {
    glBindTexture(GL_TEXTURE_2D, tindex)
    glTexParameteri(target, name, v)
  }

  def apply(name: Int): Int = {
    glGetTexParameteri(target, name)
  }

  def minFilter = this(GL_TEXTURE_MIN_FILTER)

  def minFilter_=(v: Int) = this(GL_TEXTURE_MIN_FILTER) = v

  def magFilter = this(GL_TEXTURE_MAG_FILTER)

  def magFilter_=(v: Int) = this(GL_TEXTURE_MAG_FILTER) = v

  def wrapS = this(GL_TEXTURE_WRAP_S)

  def wrapS_=(v: Int) = this(GL_TEXTURE_WRAP_S) = v

  def wrapT = this(GL_TEXTURE_WRAP_T)

  def wrapT_=(v: Int) = this(GL_TEXTURE_WRAP_T) = v

  def compareMode = this(GL_TEXTURE_COMPARE_MODE)

  def compareMode_=(v: Int) = this(GL_TEXTURE_COMPARE_MODE) = v

  def compareFunc = this(GL_TEXTURE_COMPARE_FUNC)

  def compareFunc_=(v: Int) = this(GL_TEXTURE_COMPARE_FUNC) = v

  def depthTextureMode = this(GL_DEPTH_TEXTURE_MODE)

  def depthTextureMode_=(v: Int) = this(GL_DEPTH_TEXTURE_MODE) = v

  def allocateImage1D(level: Int, internalFormat: Int, wdt: Int, border: Int, format: Int, dataType: Int) {
    target match {
      case GL_TEXTURE_1D => glTexImage1D(target, level, internalFormat, wdt, border, format, dataType, null: java.nio.IntBuffer)
      case _ => throw new UnsupportedOperationException
    }
  }

  def allocateImage2D(level: Int, internalFormat: Int, wdt: Int, hgt: Int, border: Int, format: Int, dataType: Int) {
    target match {
      case GL_TEXTURE_2D => glTexImage2D(target, level, internalFormat, wdt, hgt, border, format, dataType, null: java.nio.IntBuffer)
      case _ => throw new UnsupportedOperationException
    }
  }

  def release() {
    if (tindex != -1) {
      glDeleteTextures(tindex)
      tindex = -1
    }
  }

}


object Texture {

  def apply(target: Int)(init: Texture => Unit): Texture = new Texture(target)(init)

}
