package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.BufferUtils
import org.macrogl._

object Texture2D {
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vertices = Array(
      -0.5f,  0.5f, 0.0f,  0.0f, 0.0f,
      -0.5f, -0.5f, 0.0f,  0.0f, 1.0f,
       0.5f,  0.5f, 0.0f,  1.0f, 0.0f
    )

    val fb = BufferUtils.createFloatBuffer(vertices.length)
    fb.put(vertices)
    fb.flip()

    val mb = new AttributeBuffer(GL15.GL_STATIC_DRAW, 3, 5)
    mb.acquire()
    mb.send(0, fb)

    val textureData = new Array[Byte](64 * 64 * 3)
    scala.util.Random.nextBytes(textureData)

    val textureBuffer = BufferUtils.createByteBuffer(textureData.length)
    textureBuffer.put(textureData)
    textureBuffer.flip()

    val texture = Texture(GL11.GL_TEXTURE_2D) { tex =>
      for (_ <- using.texture(GL13.GL_TEXTURE0, tex)) {
        GL11.glTexImage2D(tex.target, 0, GL11.GL_RGB, 64, 64, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffer)

        tex.magFilter = GL11.GL_NEAREST
        tex.minFilter = GL11.GL_NEAREST
      }
    }

    texture.acquire()
    
    def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

    val pp = new Program("test")(
      Program.Shader.Vertex  (readResource("/org/macrogl/examples/Texture2D.vert")),
      Program.Shader.Fragment(readResource("/org/macrogl/examples/Texture2D.frag"))
    )

    pp.acquire()

    val attr = Array((0, 3), (3, 2))

    while (!Display.isCloseRequested) {
      for {
        _   <- using.program(pp)
        _   <- using.texture(GL13.GL_TEXTURE0, texture)
        acc <- using.attributebuffer(mb)
      } {
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        pp.uniform.testTexture = 0

        acc.render(GL11.GL_TRIANGLES, attr)
      }

      Display.update()
    }

    pp.release()
    texture.release()
    mb.release()
    Display.destroy()
  }
}
