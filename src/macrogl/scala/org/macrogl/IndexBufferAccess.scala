package org.macrogl

trait IndexBufferAccess extends BufferAccess {
  this: Buffer =>

  val elementCount: Int
  val elementType:  Int

  object indexBufferAccess extends IndexBufferAccessInner

  trait IndexBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.ELEMENT_ARRAY_BUFFER

    def draw(mode: Int, offset: Int): Unit = {
      gl.drawElements(mode, elementCount, elementType, offset)
    }
  }
}
