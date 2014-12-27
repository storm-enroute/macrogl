package org.macrogl



import org.scalajs.dom
// See scalajs-dom for the package org.scalajs.dom
// https://github.com/scala-js/scala-js-dom



object Token {

  type Buffer = org.scalajs.dom.WebGLBuffer

  object Buffer {
    val invalid: Buffer = null
    val none: Buffer = null
  }

  type Program = org.scalajs.dom.WebGLProgram

  object Program {
    val invalid: Program = null
  }

  type Shader = org.scalajs.dom.WebGLShader

  object Shader {
    val invalid: Shader = null
  }

  type UniformLocation = org.scalajs.dom.WebGLUniformLocation

  object UniformLocation {
    val invalid: UniformLocation = null
  }

  type FrameBuffer = org.scalajs.dom.WebGLFramebuffer

  object FrameBuffer {
    val invalid: FrameBuffer = null
    val none: FrameBuffer = null
  }

  type RenderBuffer = org.scalajs.dom.WebGLRenderbuffer

  object RenderBuffer {
    val invalid: RenderBuffer = null
    val none: RenderBuffer = null
  }

  type Texture = org.scalajs.dom.WebGLTexture

  object Texture {
    val invalid: Texture = null
    val none: Texture = null
  }

}
