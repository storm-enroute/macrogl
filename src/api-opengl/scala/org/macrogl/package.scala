package org






package macrogl {

  case class MacroglException(msg: String) extends Exception(msg)

  object Buffer {
    type Byte = java.nio.ByteBuffer
    type Int = java.nio.IntBuffer
    type Float = java.nio.FloatBuffer
    type Double = java.nio.DoubleBuffer
  }

}
