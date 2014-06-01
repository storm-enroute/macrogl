package org.macrogl.examples.backend.common

import org.macrogl.Utils
import org.macrogl.Macrogl
import org.macrogl.{ Macrogl => GL }

import org.macrogl.math._

class BasicFractale3D(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean, systemInit: () => Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicFractale3DListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Fractale3D: init")

      val mgl = systemInit()

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
      val attribColorLocation = mgl.getAttribLocation(program, "color")
      val uniformProjectionLocation = mgl.getUniformLocation(program, "projection")
      val uniformTransformLocation = mgl.getUniformLocation(program, "transform")

      val vertexBuffer = mgl.createBuffer
      val colorBuffer = mgl.createBuffer
      val indicesBuffer = mgl.createBuffer

      val nbTriangles = 20

      // position: nbTriangles triangles (3 vertices)
      val vertexBufferData = Macrogl.createFloatData(nbTriangles * 3 * 3)
      // color: nbTriangles triangles (3 vertices)
      val colorBufferData = Macrogl.createFloatData(nbTriangles * 3 * 3)
      val indicesBufferData = Macrogl.createShortData(nbTriangles * 3)

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

      // Fill the OpenGL buffers with the data
      mgl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, vertexBufferData, GL.STATIC_DRAW)
      mgl.vertexAttribPointer(attribPosLocation, 3, GL.FLOAT, false, 0, 0)

      mgl.bindBuffer(GL.ARRAY_BUFFER, colorBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, colorBufferData, GL.STATIC_DRAW)
      mgl.vertexAttribPointer(attribColorLocation, 3, GL.FLOAT, false, 0, 0)

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      mgl.viewport(0, 0, width, height)

      // Grey background
      mgl.clearColor(0.5f, 0.5f, 0.5f, 1)

      mgl.enableVertexAttribArray(attribPosLocation)
      mgl.enableVertexAttribArray(attribColorLocation)

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
        //print("Elapsed seconds since last frame: " + fe.elapsedTime)
        transformStack.push // Save the current transformation matrix

        // Anime the rotation using the data from the FrameEvent
        currentRotation += rotationVelocity * fe.elapsedTime

        transformStack.current = Matrix4f.translate3D(new Vector3f(0, 0, -3)) *
          Matrix4f.rotation3D(currentRotation, new Vector3f(0, 1, 0))

        // Send the current transformation to the shader
        mgl.uniformMatrix4f(uniformTransformLocation, transformStack.current)

        // Send the projection to the shader
        mgl.uniformMatrix4f(uniformProjectionLocation, projection)

        mgl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT)
        mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

        transformStack.pop // Restore the transformation matrix 
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Fractale3D: closing")

        mgl.disableVertexAttribArray(attribColorLocation)
        mgl.disableVertexAttribArray(attribPosLocation)

        mgl.deleteBuffer(indicesBuffer)
        mgl.deleteBuffer(colorBuffer)
        mgl.deleteBuffer(vertexBuffer)

        mgl.deleteShader(vertex)
        mgl.deleteShader(fragment)

        mgl.deleteProgram(program)

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