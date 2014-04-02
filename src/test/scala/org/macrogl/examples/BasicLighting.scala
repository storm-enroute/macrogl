package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.input.{Keyboard, Mouse}
import org.lwjgl.BufferUtils
import org.macrogl._

object BasicLighting {
  def main(args: Array[String]): Unit = for (storage <- HandleStorage) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val dummyDisplayHandle = storage.manage(new Handle {
      def acquire {}
      def release { Display.destroy() }
      override def toString = "dummy Display handle"
    })

    val ibb = BufferUtils.createByteBuffer(Cube.indices.length)
    ibb.put(Cube.indices)
    ibb.flip()

    val indexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibb, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

    val dummyIndexBufferHandle = storage.manage(new Handle {
      def acquire {}
      def release { GL15.glDeleteBuffers(indexBuffer) }
      override def toString = "dummy indexBuffer handle"
    })

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val dummyVaoHandle = storage.manage(new Handle {
      def acquire {}
      def release { GL15.glDeleteBuffers(indexBuffer) }
      override def toString = "dummy vao handle"
    })

    val cfb = BufferUtils.createFloatBuffer(Cube.vertices.length)
    cfb.put(Cube.vertices)
    cfb.flip()

    val vertexBuffer = storage.manage(new AttributeBuffer(GL15.GL_STATIC_DRAW, Cube.vertices.length / Cube.components, Cube.components))
    vertexBuffer.send(0, cfb)
    val attrsCfg = Array((0, 3), (3, 3), (6, 3))

    for (_ <- using.attributebuffer(vertexBuffer)) vertexBuffer.setAttributePointers(attrsCfg)

    GL30.glBindVertexArray(0)

    val pp = storage.manage(new Program("test")(
      Program.Shader.Vertex  (Utils.readResource("/org/macrogl/examples/BasicLighting.vert")),
      Program.Shader.Fragment(Utils.readResource("/org/macrogl/examples/BasicLighting.frag"))
    ))

    val projectionTransform = Utils.perspectiveProjection(50, 800.0 / 600.0, 0.1, 100.0)

    val camera = new Utils.Camera(0, 0, 4)
    val cameraSpeed = 5.0

    var dtSeconds = 0.0
    var prevTime = System.currentTimeMillis

    Mouse.setGrabbed(true)

    val leftTransform = new Matrix.Plain(
      Array[Double](
         1, 0,  0, 0,
         0, 1,  0, 0,
         0, 0,  1, 0,
        -3, 0, -5, 1
      )
    )

    val rightTransform = new Matrix.Plain(
      Array[Double](
        1, 0,  0, 0,
        0, 1,  0, 0,
        0, 0,  1, 0,
        3, 0, -5, 1
      )
    )

    for (_ <- using.program(pp)) {
      def normalize(x: Float, y: Float, z: Float) = {
        val len = x * x + y * y + z * z
        (x / len, y / len, z / len)
      }

      pp.uniform.projection = projectionTransform
      pp.uniform.lightColor = (1.0f, 1.0f, 1.0f)
      pp.uniform.lightDirection = normalize(0.0f, -1.0f, -1.0f)
      pp.uniform.ambient = 0.05f
      pp.uniform.diffuse = 0.95f
    }

    GL11.glEnable(GL11.GL_CULL_FACE)
    GL11.glEnable(GL11.GL_DEPTH_TEST)

    while (!Display.isCloseRequested && !closeRequested) {
      val time = System.currentTimeMillis
      dtSeconds = (time - prevTime) / 1000.0
      prevTime = time

      val stateChanged = processInput()
      if (stateChanged) {
        if (cameraResetRequested) {
          camera.position(0) = 0
          camera.position(1) = 0
          camera.position(2) = 4

          camera.horizontalAngle = 0
          camera.verticalAngle   = 0

          cameraResetRequested = false
        }
      }

      // update camera
      if (movingForward)  camera.moveForward(cameraSpeed * dtSeconds)
      if (movingBackward) camera.moveBackward(cameraSpeed * dtSeconds)
      if (movingLeft)     camera.moveLeft(cameraSpeed * dtSeconds)
      if (movingRight)    camera.moveRight(cameraSpeed * dtSeconds)

      val xOffset = 400 - Mouse.getX
      val yOffset = 300 - Mouse.getY
      camera.offsetOrientation(xOffset * 0.1, yOffset * 0.1)
      Mouse.setCursorPosition(400, 300)

      // update animations
      val angle = time / 1000.0

      import scala.math.{sin, cos}
      val c = cos(angle)
      val s = sin(angle)

      leftTransform.array(0)  =  c
      leftTransform.array(2)  =  s
      leftTransform.array(8)  = -s
      leftTransform.array(10) =  c

      rightTransform.array(5)  =  c
      rightTransform.array(6)  =  s
      rightTransform.array(9)  = -s
      rightTransform.array(10) =  c

      // draw
      for (_ <- using.program(pp)) {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)

        pp.uniform.viewTransform = camera.transform

        GL30.glBindVertexArray(vao)

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
        vertexBuffer.enableAttributeArrays(attrsCfg)

        pp.uniform.worldTransform = leftTransform
        GL11.glDrawElements(GL11.GL_TRIANGLES, Cube.indices.length, GL11.GL_UNSIGNED_BYTE, 0)

        pp.uniform.worldTransform = rightTransform
        GL11.glDrawElements(GL11.GL_TRIANGLES, Cube.indices.length, GL11.GL_UNSIGNED_BYTE, 0)
        
        vertexBuffer.disableAttributeArrays(attrsCfg)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

        GL30.glBindVertexArray(0)
      }

      Display.update()
    }
  }

  var movingForward  = false
  var movingBackward = false
  var movingLeft  = false
  var movingRight = false

  var closeRequested = false
  var cameraResetRequested = false

  def processInput(): Boolean = {
    var stateChanged = false

    while (Keyboard.next()) {
      movingForward  = Keyboard.isKeyDown(Keyboard.KEY_W)
      movingBackward = !movingForward && Keyboard.isKeyDown(Keyboard.KEY_S)

      movingLeft  = Keyboard.isKeyDown(Keyboard.KEY_A)
      movingRight = !movingLeft && Keyboard.isKeyDown(Keyboard.KEY_D)

      if (Keyboard.getEventKeyState()) {
        stateChanged ||= {
          Keyboard.getEventKey() match {
            case Keyboard.KEY_ESCAPE => closeRequested = true; false

            case Keyboard.KEY_F5 => cameraResetRequested = true; true

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
