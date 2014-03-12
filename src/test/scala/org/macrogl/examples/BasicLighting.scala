package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.input.Keyboard
import org.lwjgl.BufferUtils
import org.macrogl._

object BasicLighting {
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val ibb = BufferUtils.createByteBuffer(Cube.indices.length)
    ibb.put(Cube.indices)
    ibb.flip()

    val indexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibb, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val cfb = BufferUtils.createFloatBuffer(Cube.vertices.length)
    cfb.put(Cube.vertices)
    cfb.flip()

    val vertexBuffer = new AttributeBuffer(GL15.GL_STATIC_DRAW, Cube.vertices.length / Cube.components, Cube.components)
    vertexBuffer.acquire()
    vertexBuffer.send(0, cfb)
    val attrsCfg = Array((0, 3), (3, 3), (6, 3))
    for (_ <- using.attributebuffer(vertexBuffer)) vertexBuffer.setAttributePointers(attrsCfg)

    GL30.glBindVertexArray(0)

    val pp = new Program("test")(
      Program.Shader.Vertex  (Utils.readResource("/org/macrogl/examples/BasicLighting.vert")),
      Program.Shader.Fragment(Utils.readResource("/org/macrogl/examples/BasicLighting.frag"))
    )

    pp.acquire()

    val projectionTransform = Utils.perspectiveProjection(50, 800.0 / 600.0, 0.1, 100.0)

    while (!Display.isCloseRequested) {
      val stateChanged = processInput()
      if (stateChanged) {}

      for {
        _   <- enabling(GL11.GL_CULL_FACE)
        _   <- using.program(pp)
      } {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        pp.uniform.projection = projectionTransform

        GL30.glBindVertexArray(vao)

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
        vertexBuffer.enableAttributeArrays(attrsCfg)

        GL11.glDrawElements(GL11.GL_TRIANGLES, Cube.indices.length, GL11.GL_UNSIGNED_BYTE, 0)
        
        vertexBuffer.disableAttributeArrays(attrsCfg)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

        GL30.glBindVertexArray(0)
      }

      Display.update()
    }

    pp.release()
    vertexBuffer.release()
    GL30.glDeleteVertexArrays(vao)
    GL15.glDeleteBuffers(indexBuffer)
    Display.destroy()
  }

  def processInput(): Boolean = {
    var stateChanged = false

    while (Keyboard.next()) {
      if (!Keyboard.getEventKeyState()) {
        stateChanged ||= {
          Keyboard.getEventKey() match {
            case _ => false
          }
        }
      }
    }
    stateChanged
  }

  // data
  object Cube {
    val components = 9
    val attributes = 3

    // position, normal, color
    val vertices = Array[Float](
      // bottom
      -1.0f, -1.0f, -1.0f,   0, -1, 0,   1, 0, 0,
       1.0f, -1.0f, -1.0f,   0, -1, 0,   1, 0, 0,
      -1.0f, -1.0f,  1.0f,   0, -1, 0,   1, 0, 0,
       1.0f, -1.0f,  1.0f,   0, -1, 0,   1, 0, 0,
      // top
      -1.0f,  1.0f, -1.0f,   0, 1, 0,   0, 1, 0,
      -1.0f,  1.0f,  1.0f,   0, 1, 0,   0, 1, 0,
       1.0f,  1.0f, -1.0f,   0, 1, 0,   0, 1, 0,
       1.0f,  1.0f,  1.0f,   0, 1, 0,   0, 1, 0,
      // front
      -1.0f,  1.0f,  1.0f,   0, 0, 1,   0, 0, 1,
      -1.0f, -1.0f,  1.0f,   0, 0, 1,   0, 0, 1,
       1.0f,  1.0f,  1.0f,   0, 0, 1,   0, 0, 1,
       1.0f, -1.0f,  1.0f,   0, 0, 1,   0, 0, 1,
      // back
       1.0f,  1.0f, -1.0f,   0, 0, -1,   1, 1, 0,
       1.0f, -1.0f, -1.0f,   0, 0, -1,   1, 1, 0,
      -1.0f,  1.0f, -1.0f,   0, 0, -1,   1, 1, 0,
      -1.0f, -1.0f, -1.0f,   0, 0, -1,   1, 1, 0,
      // left
      -1.0f,  1.0f,  1.0f,   -1, 0, 0,   1, 0, 1,
      -1.0f,  1.0f, -1.0f,   -1, 0, 0,   1, 0, 1,
      -1.0f, -1.0f,  1.0f,   -1, 0, 0,   1, 0, 1,
      -1.0f, -1.0f, -1.0f,   -1, 0, 0,   1, 0, 1,
      // right
       1.0f,  1.0f, -1.0f,   1, 0, 0,   0, 1, 1,
       1.0f,  1.0f,  1.0f,   1, 0, 0,   0, 1, 1,
       1.0f, -1.0f, -1.0f,   1, 0, 0,   0, 1, 1,
       1.0f, -1.0f,  1.0f,   1, 0, 0,   0, 1, 1
    )
    
    val indices = Array[Byte](
      // bottom
      0, 1, 2,  1, 3, 2,
      // top
      4, 5, 6,  6, 5, 7,
      // front
      8, 9, 10,  9, 11, 10,
      // back
      12, 13, 14,  13, 15, 14,
      // left
      16, 17, 18,  17, 19, 18,
      // right
      20, 21, 22,  21, 23, 22
    )
  }
}
