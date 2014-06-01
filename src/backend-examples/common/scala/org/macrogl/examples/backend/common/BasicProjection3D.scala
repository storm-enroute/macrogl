package org.macrogl.examples.backend.common

import org.macrogl.Utils
import org.macrogl.Macrogl
import org.macrogl.{ Macrogl => GL }

import org.macrogl.math._

class BasicProjection3D(print: String => Unit, systemUpdate: () => Boolean, systemInit: () => Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicProjection3DListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Projection3D: init")

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

      // position: 1 face (4 vertices)
      val vertexBufferData = Macrogl.createFloatData(4 * 3)
      vertexBufferData.put(-1f).put(-0.5f).put(0f)
      vertexBufferData.put(1f).put(-0.5f).put(0f)
      vertexBufferData.put(1f).put(0.5f).put(0f)
      vertexBufferData.put(-1f).put(0.5f).put(0f)
      vertexBufferData.rewind

      // color: 1 face (4 vertices)
      val colorBufferData = Macrogl.createFloatData(4 * 3)
      colorBufferData.put(1f).put(0f).put(0f)
      colorBufferData.put(0f).put(1f).put(0f)
      colorBufferData.put(1f).put(1f).put(0f)
      colorBufferData.put(0f).put(0f).put(1f)
      colorBufferData.rewind

      // 1 faces = 2 triangles
      val indicesBufferData = Macrogl.createShortData(2 * 3)
      indicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
      indicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
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

      // Grey background
      mgl.clearColor(0.5f, 0.5f, 0.5f, 1)

      mgl.enableVertexAttribArray(attribPosLocation)
      mgl.enableVertexAttribArray(attribColorLocation)

      // Setup matrices
      val projection = Matrix4f.perspective3D(70f, 1280f / 720f, 0.01f, 10f)
      val transformStack = new MatrixStack(new Matrix4f)

      print("Basic Projection3D: ready")

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

        mgl.clear(GL.COLOR_BUFFER_BIT)
        mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

        transformStack.pop // Restore the transformation matrix 
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Projection3D: closing")

        mgl.disableVertexAttribArray(attribColorLocation)
        mgl.disableVertexAttribArray(attribPosLocation)

        mgl.deleteBuffer(indicesBuffer)
        mgl.deleteBuffer(colorBuffer)
        mgl.deleteBuffer(vertexBuffer)

        mgl.deleteShader(vertex)
        mgl.deleteShader(fragment)

        mgl.deleteProgram(program)

        systemClose()

        print("Basic Projection3D: closed")
      }

      funcs = Some(continue, render, close)
    }
    
    val errMsg = "Basic Projection3D: not ready"

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
    org.macrogl.Utils.startFrameListener(new BasicProjection3DListener)
  }
}