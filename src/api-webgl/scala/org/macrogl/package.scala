package org



import org.scalajs.nio



package object macrogl {

  case class FrameEvent(elapsedTime: Float)

  trait FrameListener {
    def init(): Unit
    def continue(): Boolean
    def render(fe: FrameEvent): Unit
    def close(): Unit
  }

  case class ActiveInfo(size: Int, tpe: Int, name: String)
  case class PrecisionFormat(rangeMin: Int, rangeMax: Int, precision: Int)

  case class MacroglException(msg: String) extends Exception(msg)

  type Data = org.scalajs.nio.Buffer

  object Data {
    type Byte = org.scalajs.nio.ByteBuffer
    type Int = org.scalajs.nio.IntBuffer
    type Short = org.scalajs.nio.ShortBuffer
    type Float = org.scalajs.nio.FloatBuffer
    type Double = org.scalajs.nio.DoubleBuffer
  }

}
