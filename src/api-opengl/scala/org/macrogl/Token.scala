package org.macrogl






object Token {

  type Buffer = Int

  object Buffer {
    val invalid: Buffer = -1
    val none: Buffer = 0
  }

  type VertexArray = Int

  object VertexArray {
    val none: VertexArray = 0
  }

  type Program = Int

  object Program {
    val invalid: Program = 0
  }

  type Shader = Int

  object Shader {
    val invalid: Shader = 0
  }

  type UniformLocation = Int

  object UniformLocation {
    val invalid: UniformLocation = -1
  }

  type FrameBuffer = Int

  object FrameBuffer {
    val invalid: FrameBuffer = -1
    val none: FrameBuffer = 0
  }

  type RenderBuffer = Int

  object RenderBuffer {
    val invalid: RenderBuffer = -1
    val none: RenderBuffer = 0
  }

  type Texture = Int

  object Texture {
    val invalid: Texture = -1
    val none: Texture = 0
  }

}
