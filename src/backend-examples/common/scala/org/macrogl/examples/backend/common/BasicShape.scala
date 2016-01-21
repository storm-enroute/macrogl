package org.macrogl.examples.backend.common

import org.macrogl._
import org.macrogl.algebra._
import scala.collection.mutable.ArrayBuffer

/** Basic example of creating a shape with combination of static triangles.
 */
class BasicShape(
  width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => Macrogl, systemClose: () => Unit
) extends DemoRenderable {

  class BasicShapeListener extends FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Shape: init")

      implicit val mgl = systemInit()

      // Prepare Data

      val vertexSource = """
        attribute vec3 position;

        uniform mat4 projection;
        uniform mat4 view;
  
        void main(void) {
          gl_Position = view * projection * vec4(position, 1.0);
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

      val vertexBufferData = Macrogl.createFloatData(12 * 3)
      vertexBufferData.put(0.2f).put(0).put(-2)
      vertexBufferData.put(0).put(0.2f).put(-2)
      vertexBufferData.put(0).put(0).put(-2)
      vertexBufferData.put(0).put(-0.2f).put(-2)
      vertexBufferData.put(0.2f).put(0).put(-2)
      vertexBufferData.put(0).put(0).put(-2)
      vertexBufferData.put(0).put(0).put(-2)
      vertexBufferData.put(-0.2f).put(0).put(-2)
      vertexBufferData.put(0).put(-0.2f).put(-2)
      vertexBufferData.put(0).put(0.2f).put(-2)
      vertexBufferData.put(-0.2f).put(0).put(-2)
      vertexBufferData.put(0).put(0).put(-2)      
      vertexBufferData.rewind

      val indicesBufferData = Macrogl.createShortData(3 * 1)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.rewind

      var color = 0.0
      val shapeColor = new Vector3f(0, 0.5f, 0.5f)
      
      val projectionTranform = Matrix.perspectiveProjection(50, width.toDouble / height, 0.1, 100.0)
      
      // General OpenGL
      mgl.viewport(0, 0, width, height)
      mgl.clearColor(1, 0, 0, 1) // red background

      // Setup
      val pp = new Program("BasicShape")(
        Program.Shader.Vertex(vertexSource),
        Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val vertexBuffer = new AttributeBuffer(Macrogl.STATIC_DRAW,
        vertexBufferData.remaining() / 3, 3, Array((0, 3)))
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)
      
      val camera = new Matrix.Camera(-0.25f, 0, 0);
      
      var continueCondition: Boolean = true
      var direction: Int = 0
      
      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: FrameEvent): Unit = {
        mgl.clear(Macrogl.COLOR_BUFFER_BIT)

        for {
          _ <- using.program(pp)
          b <- using.vertexbuffer(vertexBuffer)
        } {
          pp.uniform.color = shapeColor
          pp.uniform.projection = projectionTranform
          pp.uniform.view = camera.transform
          b.render(Macrogl.TRIANGLES)        
        }

        color += 0.01
        shapeColor(2) = math.abs(color % 2.0 - 1.0).toFloat
        // println(camera.position(0) , " " , camera.position(1) , " " , camera.position(2))
        
        if (direction == 0) {
          camera.moveRight(0.0003f)
        } else {
          camera.moveLeft(0.0003f)
        }

        if (camera.position(0) >= 0.5f) {
          direction = 1
        }
        if (camera.position(0) <= -0.5f) {
          direction = 0
        }
        
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Shape: closing")
        vertexBuffer.release()
        pp.release()

        systemClose()

        print("Basic Shape: closed")
      }

      funcs = Some(continue, render, close)
    }

    val errMsg = "Basic Shape: not ready"

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
    Utils.startFrameListener(new BasicShapeListener)
  }
}
