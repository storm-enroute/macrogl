package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.BufferUtils
import org.macrogl._

object SingleTriangle {
  def main(args: Array[String]) {
    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create()

    val vertices = Array(
      -0.5f,  0.5f, 0.0f,  1, 0, 0,  0, 0,
       0.0f, -0.5f, 0.0f,  0, 1 ,0,  0, 0,
       0.5f,  0.5f, 0.0f,  0, 0, 1,  0, 0
    )

    val fb = BufferUtils.createFloatBuffer(vertices.length)
    fb.put(vertices)
    fb.flip()

    val mb = new MeshBuffer(GL15.GL_STATIC_DRAW, 3)
    mb.acquire()
    mb.send(0, fb)
    def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

    val pp = new Program("test")(
      Program.Shader.Vertex(readResource("/vertex.glsl")),
      Program.Shader.Fragment(readResource("/fragment.glsl"))
    )

    pp.acquire()

    while (!Display.isCloseRequested) {
      for {
        _   <- using.program(pp)
        acc <- using.meshbuffer(mb)
      } {
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        acc.render(GL11.GL_TRIANGLES)
      }

      Display.update()
    }

    pp.release()
    mb.release()
    Display.destroy()
  }
}
