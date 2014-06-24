package org.macrogl.examples.backend.common

import org.macrogl
import org.macrogl.{ Macrogl => GL }

import org.macrogl.using

import scala.collection.mutable.ArrayBuffer

/**
 * Basic example with a static triangle
 */
class BasicTriangle(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => macrogl.Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicTriangleListener extends org.macrogl.FrameListener {

    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

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

      val vertexBufferData = macrogl.Macrogl.createFloatData(3 * 3)
      vertexBufferData.put(-0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0).put(0.2f).put(0)
      vertexBufferData.rewind

      val indicesBufferData = macrogl.Macrogl.createShortData(3 * 1)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.rewind

      val triangleColor = new org.macrogl.math.Vector3f(0, 0, 1)

      // General OpenGL
      mgl.viewport(0, 0, width, height)
      mgl.clearColor(1, 0, 0, 1) // red background

      // Setup
      val pp = new macrogl.Program("BasicTriangle")(
        macrogl.Program.Shader.Vertex(vertexSource),
        macrogl.Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)

      val indicesBuffer = mgl.createBuffer
      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      val attrsCfg = Array((0, 3))
      val attrsLocs = Array(mgl.getAttribLocation(pp.token, "position"))

      for (_ <- using attributebuffer (vertexBuffer)) {
        vertexBuffer.locations = attrsLocs
        
        vertexBuffer.setAttributePointers(attrsCfg)
      }

      print("Basic Triangle: ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: org.macrogl.FrameEvent): Unit = {
        mgl.clear(GL.COLOR_BUFFER_BIT)

        for {
          _ <- using program (pp)
          _ <- using attributebuffer (vertexBuffer)
        } {
          vertexBuffer.enableAttributeArrays(attrsCfg)

          pp.uniform.color = triangleColor
          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

          vertexBuffer.disableAttributeArrays(attrsCfg)
        }

        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Triangle: closing")

        mgl.deleteBuffer(indicesBuffer)
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
    org.macrogl.Utils.startFrameListener(new BasicTriangleListener)
  }
}