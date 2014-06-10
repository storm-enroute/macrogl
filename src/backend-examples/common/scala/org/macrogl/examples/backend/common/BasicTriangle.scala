package org.macrogl.examples.backend.common

import org.macrogl.Utils
import org.macrogl.Macrogl
import org.macrogl.{ Macrogl => GL }

/**
 * Basic example with a static triangle
 */
class BasicTriangle(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicTriangleListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Triangle: init")

      val mgl = systemInit()

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

      val program = mgl.createProgram()
      val vertex = mgl.createShader(GL.VERTEX_SHADER)
      val fragment = mgl.createShader(GL.FRAGMENT_SHADER)

      mgl.shaderSource(vertex, vertexSource)
      mgl.shaderSource(fragment, fragmentSource)

      mgl.compileShader(vertex)
      mgl.compileShader(fragment)

      if (mgl.getShaderParameterb(vertex, GL.COMPILE_STATUS) == false)
        print("Vertex compilation error: " + mgl.getShaderInfoLog(vertex))
      if (mgl.getShaderParameterb(fragment, GL.COMPILE_STATUS) == false)
        print("Fragment compilation error: " + mgl.getShaderInfoLog(fragment))

      mgl.attachShader(program, vertex)
      mgl.attachShader(program, fragment)

      mgl.linkProgram(program)

      if (mgl.getProgramParameterb(program, GL.LINK_STATUS) == false)
        print("Program linking error: " + mgl.getProgramInfoLog(program))

      mgl.validateProgram(program)

      if (mgl.getProgramParameterb(program, GL.VALIDATE_STATUS) == false)
        print("Program validation error: " + mgl.getProgramInfoLog(program))

      mgl.useProgram(program)

      val attribPosLocation = mgl.getAttribLocation(program, "position")
      val uniformColorLocation = mgl.getUniformLocation(program, "color")

      val vertexBuffer = mgl.createBuffer
      val indicesBuffer = mgl.createBuffer

      val vertexBufferData = Macrogl.createFloatData(3 * 3)
      vertexBufferData.put(-0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0.2f).put(-0.2f).put(0)
      vertexBufferData.put(0).put(0.2f).put(0)
      vertexBufferData.rewind

      val indicesBufferData = Macrogl.createShortData(3 * 1)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.rewind

      val color = new org.macrogl.math.Vector3f(0, 0, 1)

      mgl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, vertexBufferData, GL.STATIC_DRAW)
      mgl.vertexAttribPointer(attribPosLocation, 3, GL.FLOAT, false, 0, 0)

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      mgl.viewport(0, 0, width, height)

      mgl.clearColor(1, 0, 0, 1)

      /*org.macrogl.Utils.getTextFileFromResources("/org/macrogl/examples/backend/common/test.txt") { lines =>
        print("Basic Triangle: Text file received, lines = " + lines.length + "\n--- Start of file ---")
        lines.foreach { line =>
          print(line)
        }
        print("--- End of file ---")
      }
      
      org.macrogl.Utils.getBinaryFileFromResources("/org/macrogl/examples/backend/common/macrogl.png") { buffer =>
        print("Basic Triangle: Binary file received, size = " + buffer.remaining() + " bytes, first byte = " + buffer.get(0))
      }*/
      
      mgl.enableVertexAttribArray(attribPosLocation)

      print("Basic Triangle: ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: org.macrogl.FrameEvent): Unit = {
        //print("Elapsed seconds since last frame: " + fe.elapsedTime)

        mgl.clear(GL.COLOR_BUFFER_BIT)
        mgl.uniform3f(uniformColorLocation, color)

        mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Triangle: closing")

        mgl.disableVertexAttribArray(attribPosLocation)

        mgl.deleteBuffer(indicesBuffer)
        mgl.deleteBuffer(vertexBuffer)

        mgl.deleteShader(vertex)
        mgl.deleteShader(fragment)

        mgl.deleteProgram(program)

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