package org


package object macrogl {
  
  case class ActiveInfo(size: Int, tpe: Int, name: String)
  case class PrecisionFormat(rangeMin: Int, rangeMax: Int, precision: Int)

  case class MacroglException(msg: String) extends Exception(msg)

  type Data = java.nio.Buffer

  object Data {
    type Byte = java.nio.ByteBuffer
    type Short = java.nio.ShortBuffer
    type Int = java.nio.IntBuffer
    type Float = java.nio.FloatBuffer
    type Double = java.nio.DoubleBuffer
  }

}
