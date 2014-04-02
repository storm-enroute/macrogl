package org

import org.scalajs.nio

package object macrogl {

  case class MacroglException(msg: String) extends Exception(msg)

  type Data = org.scalajs.nio.Buffer

  object Data {
    type Byte = org.scalajs.nio.ByteBuffer
    type Int = org.scalajs.nio.IntBuffer
    type Float = org.scalajs.nio.FloatBuffer
    type Double = org.scalajs.nio.DoubleBuffer
  }

}
