package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.input.Keyboard
import org.lwjgl.BufferUtils
import org.{ macrogl => gl }
import org.macrogl._
import org.macrogl.{ ex => glex }

object TransformFeedback {
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val drawTriangle = new glex.Program("triangle")(
      gl.Program.Shader.Vertex(Utils.readResource("/org/macrogl/examples/TransformFeedbackTriangle.vert")),
      gl.Program.Shader.Fragment(Utils.readResource("/org/macrogl/examples/SingleTriangle.frag")))
    drawTriangle.acquire()

    GL30.glTransformFeedbackVaryings(drawTriangle.token, Array("worldPosition"), GL30.GL_INTERLEAVED_ATTRIBS)

    GL20.glLinkProgram(drawTriangle.token)
    GL20.glValidateProgram(drawTriangle.token)
    Macrogl.default.checkError()

    val drawParticles = new glex.Program("draw-particles")(
      gl.Program.Shader.Vertex(Utils.readResource("/org/macrogl/examples/TransformFeedbackTriangle.vert")),
      gl.Program.Shader.Fragment(Utils.readResource("/org/macrogl/examples/SingleTriangle.frag")))
    drawParticles.acquire()

    val updateParticles = new glex.Program("update-particles")(
      gl.Program.Shader.Vertex(Utils.readResource("/org/macrogl/examples/TransformFeedback.vert")))
    updateParticles.acquire()

    GL30.glTransformFeedbackVaryings(updateParticles.token, Array("newPosition", "newColor", "newVelocity"), GL30.GL_INTERLEAVED_ATTRIBS)

    GL20.glLinkProgram(updateParticles.token)
    GL20.glValidateProgram(updateParticles.token)
    Macrogl.default.checkError()

    val query = GL15.glGenQueries()

    val fb = BufferUtils.createFloatBuffer(Particles.vertices.length)
    fb.put(Particles.vertices)
    fb.flip()

    val evenBuffer = new AttributeBuffer(GL15.GL_STREAM_DRAW, Particles.count, Particles.components)
    evenBuffer.acquire()
    evenBuffer.send(0, fb)

    val oddBuffer = new AttributeBuffer(GL15.GL_STREAM_DRAW, Particles.count, Particles.components)
    oddBuffer.acquire()

    val triangleFeedback = new AttributeBuffer(GL15.GL_STREAM_DRAW, Triangle.count, Triangle.components + 1)
    triangleFeedback.acquire()

    GL13.glActiveTexture(GL13.GL_TEXTURE0)
    GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, triangleFeedback.token)

    val triangleTexture = GL11.glGenTextures()
    GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, triangleTexture)
    GL31.glTexBuffer(GL31.GL_TEXTURE_BUFFER, GL30.GL_RGBA32F, triangleFeedback.token)

    val tb = BufferUtils.createFloatBuffer(Triangle.vertices.length)
    tb.put(Triangle.vertices)
    tb.flip()

    val triangleBuffer = new AttributeBuffer(GL15.GL_STATIC_DRAW, Triangle.count, Triangle.components)
    triangleBuffer.acquire()
    triangleBuffer.send(0, tb)

    val updateAttr = Array((0, 3), (3, 3), (6, 3))
    val drawAttr = Array((0, 3), (3, 3))

    var prevTime = System.currentTimeMillis
    var frame = 0

    val triangleTransform = gl.Matrix.identity[glex.Matrix.Plain]

    val rotationSpeed = 2.0f

    GL11.glPointSize(2.0f)

    val aspectRatio = 800.0 / 600.0
    val projectionTransform = Utils.orthoProjection(-aspectRatio, aspectRatio, -1, 1, -1, 1)

    for (_ <- using.program(drawParticles)) {
      drawParticles.uniform.transform = gl.Matrix.identity[glex.Matrix.Plain]
      drawParticles.uniform.projection = projectionTransform
    }

    while (!Display.isCloseRequested && !closeRequested) {
      val time = System.currentTimeMillis
      val dtSeconds = (time - prevTime) / 1000.0
      prevTime = time

      val stateChanged = processInput()
      if (stateChanged) {}

      if (left || right) {
        angle += (if (left) rotationSpeed * dtSeconds else -rotationSpeed * dtSeconds).toFloat

        import scala.math.{ sin, cos }
        val c = cos(angle)
        val s = sin(angle)

        triangleTransform.array(0) = c
        triangleTransform.array(1) = s
        triangleTransform.array(4) = -s
        triangleTransform.array(5) = c
      }

      for {
        _ <- using.program(drawTriangle)
        acc <- using.attributebuffer(triangleBuffer)
      } {
        drawTriangle.uniform.transform = triangleTransform
        drawTriangle.uniform.projection = projectionTransform

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, triangleFeedback.token)
        GL30.glBeginTransformFeedback(GL11.GL_TRIANGLES)

        acc.render(GL11.GL_TRIANGLES, drawAttr)

        GL30.glEndTransformFeedback()
        GL15.glBindBuffer(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0)
      }

      val (input, output) = if (frame % 2 == 0) (evenBuffer, oddBuffer) else (oddBuffer, evenBuffer)
      frame += 1

      for {
        _ <- using.program(updateParticles)
        acc <- using.attributebuffer(input)
        _ <- enabling(GL30.GL_RASTERIZER_DISCARD)
      } {
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, triangleFeedback.token)

        updateParticles.uniform.dtSeconds = dtSeconds
        updateParticles.uniform.triangleVertices = 0

        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, output.token)

        GL15.glBeginQuery(GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN, query)
        GL30.glBeginTransformFeedback(GL11.GL_POINTS)

        acc.render(GL11.GL_POINTS, updateAttr)

        GL30.glEndTransformFeedback()
        GL15.glEndQuery(GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN)

        GL15.glBindBuffer(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0)

        val written = GL15.glGetQueryObjectui(query, GL15.GL_QUERY_RESULT)

        GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, 0)
      }

      for {
        _ <- using.program(drawParticles)
        acc <- using.attributebuffer(output)
      } acc.render(GL11.GL_POINTS, drawAttr)

      Display.update()
    }

    drawTriangle.release()
    updateParticles.release()
    drawParticles.release()

    oddBuffer.release()
    evenBuffer.release()
    triangleBuffer.release()
    triangleFeedback.release()

    Display.destroy()
  }

  var closeRequested = false
  var angle = 0.0f
  var left = false
  var right = false

  def processInput(): Boolean = {
    var stateChanged = false

    while (Keyboard.next()) {
      left = Keyboard.isKeyDown(Keyboard.KEY_A)
      right = !left && Keyboard.isKeyDown(Keyboard.KEY_D)

      if (Keyboard.getEventKeyState()) {
        stateChanged ||= {
          Keyboard.getEventKey() match {
            case Keyboard.KEY_ESCAPE =>
              closeRequested = true; false

            case _ => false
          }
        }
      }
    }
    stateChanged
  }

  object Particles {
    val count = 10000
    val vertices = genParticles(count)
    val components = vertices.length / count

    def genParticle() = {
      import scala.util.Random.{ nextFloat => nf }
      Array[Float](
        0, 0.75f, 0,
        nf, nf, nf,
        nf - 0.5f, nf - 0.5f, nf - 0.5f)
    }

    def genParticles(count: Int) = {
      val ab = new collection.mutable.ArrayBuilder.ofFloat
      for (i <- 1 to count) ab ++= genParticle()
      ab.result()
    }
  }

  object Triangle {
    val count = 3
    val components = 6
    val vertices = Array[Float](
      -0.433f, 0.25f, 0, 1, 0, 0,
      0, -0.5f, 0, 0, 1, 0,
      0.433f, 0.25f, 0, 0, 0, 1)
  }
}
