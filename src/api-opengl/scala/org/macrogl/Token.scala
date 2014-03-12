package org.macrogl






abstract class Token {

  def valid: Boolean

  def index: Int

  def webGLObject: Any = throw new UnsupportedOperationException("Not a WebGL implementation.")

}


object Token {

  case class Buffer private[macrogl] (val index: Int) extends Token {
    final def valid = index > 0
  }

  object Buffer {
    val invalid: Buffer = Buffer(-1)
    val none: Buffer = Buffer(0)
  }

}

