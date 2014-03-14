package org






package object macrogl {

  case class MacroglException(msg: String) extends Exception(msg)

  type Data = java.nio.Buffer

  object Data {
    type Byte = java.nio.ByteBuffer
    type Int = java.nio.IntBuffer
    type Float = java.nio.FloatBuffer
    type Double = java.nio.DoubleBuffer
  }

}
