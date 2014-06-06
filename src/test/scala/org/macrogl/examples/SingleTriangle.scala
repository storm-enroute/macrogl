package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.BufferUtils
import org.macrogl._

object SingleTriangle {
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vertices = Array(
      -0.5f, 0.5f, 0.0f, 1, 0, 0,
      0.0f, -0.5f, 0.0f, 0, 1, 0,
      0.5f, 0.5f, 0.0f, 0, 0, 1)

    val fb = BufferUtils.createFloatBuffer(vertices.length)
    fb.put(vertices)
    fb.flip()

    val mb = new AttributeBuffer(GL15.GL_STATIC_DRAW, 3, 6)
    mb.acquire()
    mb.send(0, fb)

    def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

    val pp = new Program("test")(
      Program.Shader.Vertex(readResource("/org/macrogl/examples/SingleTriangle.vert")),
      Program.Shader.Fragment(readResource("/org/macrogl/examples/SingleTriangle.frag")))

    pp.acquire()

    val attr = Array((0, 3), (3, 3))

    while (!Display.isCloseRequested) {
      for {
        _ <- using.program(pp)
        acc <- using.attributebuffer(mb)
      } {
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        acc.render(GL11.GL_TRIANGLES, attr)
      }

      Display.update()
    }

    pp.release()
    mb.release()
    Display.destroy()
  }
}
