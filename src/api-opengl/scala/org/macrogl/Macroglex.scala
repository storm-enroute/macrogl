package org.macrogl

import org.lwjgl.opengl._
import org.lwjgl.util.glu._

class Macroglex private[macrogl] () extends Macrogl() {

  final def bindShaderStorageBuffer(target: Int, layoutIndex: Int, buffer: Token.Buffer) = {
    import org.lwjgl.opengl.GL43._
    GL30.glBindBufferBase(target, layoutIndex, buffer)
  }

  final def dispatchCompute(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) {
    GL43.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
  }

  // Moved from Macrogl

  final def bufferData(target: Int, totalBytes: Long, usage: Int) {
    GL15.glBufferData(target, totalBytes, usage)
  }

  final def getBufferSubData(target: Int, offset: Long, data: Data.Byte) {
    GL15.glGetBufferSubData(target, offset, data)
  }
  final def getBufferSubData(target: Int, offset: Long, data: Data.Short) {
    GL15.glGetBufferSubData(target, offset, data)
  }
  final def getBufferSubData(target: Int, offset: Long, data: Data.Int) {
    GL15.glGetBufferSubData(target, offset, data)
  }
  final def getBufferSubData(target: Int, offset: Long, data: Data.Float) {
    GL15.glGetBufferSubData(target, offset, data)
  }
  final def getBufferSubData(target: Int, offset: Long, data: Data.Double) {
    GL15.glGetBufferSubData(target, offset, data)
  }
  
  final def getParameterd(pname: Int): Double = {
    GL11.glGetDouble(pname)
  }
  
  final def getParameterdv(pname: Int, outputs: Data.Double) = {
    GL11.glGetDouble(pname, outputs)
  }
  
  final def texImage1D(target: Int, level: Int, internalformat: Int, width: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Byte) = {
    GL11.glTexImage1D(target, level, internalformat, width, border, format, `type`, pixels)
  }
  final def texImage1D(target: Int, level: Int, internalformat: Int, width: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Short) = {
    GL11.glTexImage1D(target, level, internalformat, width, border, format, `type`, pixels)
  }
  final def texImage1D(target: Int, level: Int, internalformat: Int, width: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Int) = {
    GL11.glTexImage1D(target, level, internalformat, width, border, format, `type`, pixels)
  }
  final def texImage1D(target: Int, level: Int, internalformat: Int, width: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Float) = {
    GL11.glTexImage1D(target, level, internalformat, width, border, format, `type`, pixels)
  }
  final def texImage1D(target: Int, level: Int, internalformat: Int, width: Int, border: Int,
    format: Int, `type`: Int, pixels: Data.Double) = {
    GL11.glTexImage1D(target, level, internalformat, width, border, format, `type`, pixels)
  }
  
  final def color4f(r: Float, g: Float, b: Float, a: Float) {
    GL11.glColor4f(r, g, b, a)
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
}

object Macroglex {

  /* public API - constants */

  val GL_GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER

  val GL_COMPUTE_SHADER = GL43.GL_COMPUTE_SHADER

  val GL_SHADER_STORAGE_BUFFER = GL43.GL_SHADER_STORAGE_BUFFER

  /* public API - implicits */

  implicit def default = new Macroglex()

}


