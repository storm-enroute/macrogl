package org.macrogl.examples.backend.common

import org.macrogl._
import org.macrogl.algebra._
import scala.collection.mutable.ArrayBuffer

/** Basic example with a static triangle.
 */
class BasicTriangle(
  width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => Macrogl, systemClose: () => Unit
) extends DemoRenderable {

  class BasicTriangleListener extends FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Triangle: init")

      implicit val mgl = systemInit()

      // Prepare Data

      val vertexSource = """
        attribute vec3 position;
  
        void main(void) {
          gl_Position = vec4(position, 1.0);
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
      vertexBufferData.rewind

      val indicesBufferData = Macrogl.createShortData(3 * 1)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.rewind

      var color = 0.0
      val triangleColor = new Vector3f(0, 0, 0.5f)

      // General OpenGL
      mgl.viewport(0, 0, width, height)
      mgl.clearColor(1, 0, 0, 1) // red background

      // Setup
      val pp = new Program("BasicTriangle")(
        Program.Shader.Vertex(vertexSource),
        Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val vertexBuffer = new AttributeBuffer(Macrogl.STATIC_DRAW,
        vertexBufferData.remaining() / 3, 3, Array((0, 3)))
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: FrameEvent): Unit = {
        mgl.clear(Macrogl.COLOR_BUFFER_BIT)

        for {
          _ <- using.program(pp)
          b <- using.vertexbuffer(vertexBuffer)
        } {
          pp.uniform.color = triangleColor
          b.render(Macrogl.TRIANGLES)
        }

        color += 0.01
        triangleColor(2) = math.abs(color % 2.0 - 1.0).toFloat
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Triangle: closing")
        vertexBuffer.release()
        pp.release()

        systemClose()

        print("Basic Triangle: closed")
      }

      funcs = Some(continue, render, close)
    }

    val errMsg = "Basic Triangle: not ready"

    def continue(): Boolean = {
      funcs match {
        case Some((continueFunc, _, _)) => continueFunc()
        case None => throw new RuntimeException(errMsg)
      }
    }
    def render(fe: FrameEvent): Unit = {
      funcs match {
        case Some((_, renderFunc, _)) => renderFunc(fe)
        case None => throw new RuntimeException(errMsg)
      }
    }
    def close(): Unit = {
      funcs match {
        case Some((_, _, closeFunc)) => closeFunc()
        case None => throw new RuntimeException(errMsg)
      }

      funcs = None
    }
  }

  def start(): Unit = {
    Utils.startFrameListener(new BasicTriangleListener)
  }
}