package org.macrogl.examples.backend.common

import org.macrogl
import org.macrogl.{ Macrogl => GL }
import org.macrogl.using

import org.macrogl.math._

/**
 * Basic example to try depth-testing
 */
class BasicFractale3D(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => macrogl.Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicFractale3DListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Fractale3D: init")

      implicit val mgl = systemInit()

      val vertexSource = """
        uniform mat4 projection;
        uniform mat4 transform;
        
        attribute vec3 position;
        attribute vec3 color;
  
        varying vec3 vColor;
        
        void main(void) {
          gl_Position = projection * transform * vec4(position, 1.0);
          vColor = color;
        }
        """

      val fragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        varying vec3 vColor;
  
        void main(void) {
          gl_FragColor = vec4(vColor, 1.0);
        }
        """

      val pp = new macrogl.Program("BasicTriangle")(
        macrogl.Program.Shader.Vertex(vertexSource),
        macrogl.Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val indicesBuffer = mgl.createBuffer

      val nbTriangles = 20

      // position: nbTriangles triangles (3 vertices)
      val vertexBufferData = macrogl.Macrogl.createFloatData(nbTriangles * 3 * 3)
      // color: nbTriangles triangles (3 vertices)
      val colorBufferData = macrogl.Macrogl.createFloatData(nbTriangles * 3 * 3)
      val indicesBufferData = macrogl.Macrogl.createShortData(nbTriangles * 3)

      val cos120 = -0.5f
      val sin120 = 0.866025403784438646763723170752936183471402626905190314027903f
      val cos240 = -0.5f
      val sin240 = -0.866025403784438646763723170752936183471402626905190314027903f
      def drawTriangle(cX: Float, cY: Float, cZ: Float, r: Float): Unit = {
        val top = cY + r
        vertexBufferData.put(cX).put(top).put(cZ)
        vertexBufferData.put(cX * cos120 - top * sin120).put(cX * sin120 + top * cos120).put(cZ)
        vertexBufferData.put(cX * cos240 - top * sin240).put(cX * sin240 + top * cos240).put(cZ)
        colorBufferData.put(0f).put(0f).put(0f)
        colorBufferData.put(0f).put(0f).put(0f)
        colorBufferData.put(0f).put(0f).put(1f / cZ)
      }

      def recursiveTriangles(n: Integer): Unit = {
        drawTriangle(0f, 0f, 2f / n.toFloat, 3f / n.toFloat)
        indicesBufferData.put((3 * n - 3).toShort).put((3 * n - 2).toShort).put((3 * n - 1).toShort)
        if (n > 2)
          recursiveTriangles(n - 1)
      }

      recursiveTriangles(nbTriangles)

      vertexBufferData.rewind
      colorBufferData.rewind
      indicesBufferData.rewind

      val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)
      for (_ <- using attributebuffer (vertexBuffer)) {
        val vertexAttrsLocs = Array(mgl.getAttribLocation(pp.token, "position"))
        val vertexAttrsCfg = Array((0, 3))

        vertexBuffer.setLocations(vertexAttrsLocs)
        vertexBuffer.setAttribsCfg(vertexAttrsCfg)

        vertexBuffer.setAttributePointers()
      }

      val colorBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, colorBufferData.remaining() / 3, 3)
      colorBuffer.acquire()
      colorBuffer.send(0, colorBufferData)
      for (_ <- using attributebuffer (colorBuffer)) {
        val colorAttrsLocs = Array(mgl.getAttribLocation(pp.token, "color"))
        val colorAttrsCfg = Array((0, 3))

        colorBuffer.setLocations(colorAttrsLocs)
        colorBuffer.setAttribsCfg(colorAttrsCfg)

        colorBuffer.setAttributePointers()
      }

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      mgl.viewport(0, 0, width, height)
      mgl.clearColor(0.5f, 0.5f, 0.5f, 1)

      // Setup matrices
      val projection = Matrix4f.perspective3D(70f, 1280f / 720f, 0.01f, 10f)
      val transformStack = new MatrixStack(new Matrix4f)

      // Enable depth test
      mgl.enable(GL.DEPTH_TEST)
      mgl.depthFunc(GL.LESS)

      print("Basic Fractale3D: ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      var currentRotation: Float = 0f
      val rotationVelocity: Float = 90f

      def render(fe: org.macrogl.FrameEvent): Unit = {
        transformStack.push // Save the current transformation matrix

        // Anime the rotation using the data from the FrameEvent
        currentRotation += rotationVelocity * fe.elapsedTime

        transformStack.current = Matrix4f.translate3D(new Vector3f(0, 0, -3)) *
          Matrix4f.rotation3D(currentRotation, new Vector3f(0, 1, 0))

        for {
          _ <- using program (pp)
        } {
          mgl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT)

          vertexBuffer.enableAttributeArrays()
          colorBuffer.enableAttributeArrays()

          pp.uniform.projection = projection
          pp.uniform.transform = transformStack.current

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

          colorBuffer.disableAttributeArrays()
          vertexBuffer.disableAttributeArrays()
        }

        transformStack.pop // Restore the transformation matrix 
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Fractale3D: closing")

        colorBuffer.release()
        vertexBuffer.release()

        pp.release()

        systemClose()

        print("Basic Fractale3D: closed")
      }

      funcs = Some(continue, render, close)
    }

    val errMsg = "Basic Fractale3D: not ready"

    def continue(): Boolean = {
      funcs match {
        case Some((continueFunc, _, _)) => continueFunc()
        case None => throw new RuntimeException(errMsg)
      }
    }
    def render(fe: org.macrogl.FrameEvent): Unit = {
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
    org.macrogl.Utils.startFrameListener(new BasicFractale3DListener)
  }
}