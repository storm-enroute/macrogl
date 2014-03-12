package org






package macrogl {

  case class MacroglException(msg: String) extends Exception(msg)

  object Buffer {
    type Float = java.nio.FloatBuffer
  }

}
