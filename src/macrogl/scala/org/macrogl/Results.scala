package org.macrogl

object Results {
  private val intResultT = new ThreadLocal[Data.Int] {
    override def initialValue = Macrogl.createIntData(16)
  }

  def intResult = intResultT.get

  private val floatResultT = new ThreadLocal[Data.Float] {
    override def initialValue = Macrogl.createFloatData(16)
  }

  def floatResult = floatResultT.get

  private val doubleResultT = new ThreadLocal[Data.Double] {
    override def initialValue = Macrogl.createDoubleData(16)
  }

  def doubleResult = doubleResultT.get
}

