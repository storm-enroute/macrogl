package org.macrogl.examples.backend.common

import org.macrogl._
import org.macrogl.algebra._
import scala.collection.mutable.ArrayBuffer

/** Main banner for the MacroGL homepage.
 */
class MainBanner(
  width: Int, height: Int, systemInit: () => Macrogl
) extends DemoRenderable {

  class MainBannerListener extends FrameListener {
    var funcs: Option[(() => Boolean, FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      implicit val mgl = systemInit()

      val vertexSource = """
        attribute vec3 position;

        uniform mat4 projection;
        uniform mat4 view;

        void main(void) {
          gl_Position = projection * view * vec4(position, 1.0);
        }
        """

      val fragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif

        uniform vec3 color;

        void main(void) {
          gl_FragColor = vec4(color, 1.0);
        }
        """

      val vertexBufferData = Macrogl.createFloatData(3 * 3)
      vertexBufferData.put(-0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0).put(0.2f).put(0)
      vertexBufferData.rewind()

      val projectionTransform =
        Matrix.perspectiveProjection(50, width.toDouble / height, 0.1, 100.0)

      val triangleColor = new Vector3f(0.5f, 0.5f, 0.5f)

      val camera = new Matrix.Camera(0, 0, 8)
      var xAngle = 0.0
      var yAngle = 0.0

      // General OpenGL
      mgl.viewport(0, 0, width, height)
      mgl.clearColor(1, 1, 1, 1)

      // Setup
      val pp = new Program("MainBanner")(
        Program.Shader.Vertex(vertexSource),
        Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val vertexBuffer = new AttributeBuffer(Macrogl.STATIC_DRAW,
        vertexBufferData.remaining() / 3, 3, Array((0, 3)))
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)

      def continue(): Boolean = true

      def render(fe: FrameEvent): Unit = {
        mgl.clear(Macrogl.COLOR_BUFFER_BIT)

        for {
          _ <- using.program(pp)
          b <- using.vertexbuffer(vertexBuffer)
        } {
          pp.uniform.projection = projectionTransform
          pp.uniform.view = camera.transform
          pp.uniform.color = triangleColor
          b.render(Macrogl.TRIANGLES)
        }

        xAngle = (xAngle + 0.004) % (2 * math.Pi)
        yAngle = (yAngle + 0.002) % (2 * math.Pi)
        camera.setOrientation(xAngle, yAngle)
      }

      def close(): Unit = {
        vertexBuffer.release()
        pp.release()
      }

      funcs = Some(continue, render, close)
    }

    def continue(): Boolean = {
      funcs match {
        case Some((continueFunc, _, _)) => continueFunc()
        case None => sys.error("not initialized")
      }
    }
    def render(fe: FrameEvent): Unit = {
      funcs match {
        case Some((_, renderFunc, _)) => renderFunc(fe)
        case None => sys.error("not initialized")
      }
    }
    def close(): Unit = {
      funcs match {
        case Some((_, _, closeFunc)) => closeFunc()
        case None => sys.error("not initialized")
      }

      funcs = None
    }
  }

  def start(): Unit = {
    Utils.startFrameListener(new MainBannerListener)
  }
}
