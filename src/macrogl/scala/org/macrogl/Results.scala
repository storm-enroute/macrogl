package org.macrogl



import java.nio.IntBuffer
import java.nio.FloatBuffer
import java.nio.DoubleBuffer
import org.lwjgl.BufferUtils._



object Results {
  private val intResultT = new ThreadLocal[IntBuffer] {
    override def initialValue = createIntBuffer(16)
  }
  
  def intResult = intResultT.get

  private val floatResultT = new ThreadLocal[FloatBuffer] {
    override def initialValue = createFloatBuffer(16)
  }

  def floatResult = floatResultT.get

  private val doubleResultT = new ThreadLocal[DoubleBuffer] {
    override def initialValue = createDoubleBuffer(16)
  }
  
  def doubleResult = doubleResultT.get
}

