package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.input.Keyboard
import org.lwjgl.BufferUtils
import org.macrogl._

object FeedbackTransform {
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val drawParticles = new Program("draw")(
      Program.Shader.Vertex  (Utils.readResource("/org/macrogl/examples/SingleTriangle.vert")),
      Program.Shader.Fragment(Utils.readResource("/org/macrogl/examples/SingleTriangle.frag"))
    )
    drawParticles.acquire()

    val updateParticles = new Program("update")(
      Program.Shader.Vertex(Utils.readResource("/org/macrogl/examples/TransformFeedback.vert"))
    )
    updateParticles.acquire()

    GL30.glTransformFeedbackVaryings(updateParticles.token, Array("newPosition", "newColor", "newVelocity"), GL30.GL_INTERLEAVED_ATTRIBS)

    GL20.glLinkProgram(updateParticles.token)
    GL20.glValidateProgram(updateParticles.token)
    Macrogl.default.checkError()

    val query = GL15.glGenQueries()

    val fb = BufferUtils.createFloatBuffer(Particles.vertices.length)
    fb.put(Particles.vertices)
    fb.flip()

    val evenBuffer = new AttributeBuffer(GL15.GL_STATIC_DRAW, Particles.count, Particles.components)
    evenBuffer.acquire()
    evenBuffer.send(0, fb)

    val oddBuffer = new AttributeBuffer(GL15.GL_STREAM_DRAW, Particles.count, Particles.components)
    oddBuffer.acquire()

    val updateAttr = Array((0, 3), (3, 3), (6, 3))
    val drawAttr   = Array((0, 3), (3, 3))

    var prevTime = System.currentTimeMillis
    var frame = 0

    while (!Display.isCloseRequested && !closeRequested) {
      val time = System.currentTimeMillis
      val dtSeconds = (time - prevTime) / 1000.0
      prevTime = time

      val stateChanged = processInput()
      if (stateChanged) {}

      val (input, output)= if (frame % 2 == 0) (evenBuffer, oddBuffer) else (oddBuffer, evenBuffer)
      frame += 1

      for {
        _   <- using.program(updateParticles)
        acc <- using.attributebuffer(input)
        _   <- enabling(GL30.GL_RASTERIZER_DISCARD)
      } {
        updateParticles.uniform.dtSeconds = dtSeconds

        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, output.token)
        GL30.glBeginTransformFeedback(GL11.GL_POINTS)
        GL15.glBeginQuery(GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN, query)
        acc.render(GL11.GL_POINTS, updateAttr)
        GL30.glEndTransformFeedback()
        GL15.glBindBuffer(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0)
        GL15.glEndQuery(GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN)

        val written = GL15.glGetQueryObjectui(query, GL15.GL_QUERY_RESULT)
      }

      for {
        _   <- using.program(drawParticles)
        acc <- using.attributebuffer(output)
      } {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        acc.render(GL11.GL_POINTS, drawAttr)
      }

      Display.update()
    }

    oddBuffer.release()
    evenBuffer.release()
    Display.destroy()
  }

  var closeRequested = false

  def processInput(): Boolean = {
    var stateChanged = false

    while (Keyboard.next()) {
      if (Keyboard.getEventKeyState()) {
        stateChanged ||= {
          Keyboard.getEventKey() match {
            case Keyboard.KEY_ESCAPE => closeRequested = true; false

            case _ => false
          }
        }
      }
    }
    stateChanged
  }

  object Particles {
    val count = 3000
    val vertices = genParticles(count)
    val components = vertices.length / count

    def genParticle() = {
      import util.Random.{nextFloat => nf}
      Array[Float](
        0, 0, 0,
        nf, nf, nf,
        nf - 0.5f, nf - 0.5f, nf - 0.5f
      )
    }

    def genParticles(count: Int) = {
      val ab = new collection.mutable.ArrayBuilder.ofFloat
      for (i <- 1 to count) ab ++= genParticle()
      ab.result()
    }

  }
}
