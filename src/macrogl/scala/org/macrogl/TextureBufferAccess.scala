package org.macrogl

trait TextureBufferAccess extends BufferAccess {
  this: Buffer =>

  object textureBufferAccess extends TextureBufferAccessInner

  trait TextureBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.TEXTURE_BUFFER
  }
}

