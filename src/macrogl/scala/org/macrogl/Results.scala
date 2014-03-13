package org.macrogl






object Results {
  private val intResultT = new ThreadLocal[Buffer.Int] {
    override def initialValue = Macrogl.createIntBuffer(16)
  }
  
  def intResult = intResultT.get

  private val floatResultT = new ThreadLocal[Buffer.Float] {
    override def initialValue = Macrogl.createFloatBuffer(16)
  }

  def floatResult = floatResultT.get

  private val doubleResultT = new ThreadLocal[Buffer.Double] {
    override def initialValue = Macrogl.createDoubleBuffer(16)
  }
  
  def doubleResult = doubleResultT.get
}

