package org.macrogl.examples.backend.common

import org.macrogl.Utils
import org.macrogl.Macrogl
import org.macrogl.{ Macrogl => GL }

class BasicTexture(print: String => Unit, systemUpdate: => Boolean, systemInit: => Macrogl, systemClose: => Unit) extends DemoRenderable {

  class BasicTextureListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Init example")

      val mgl = systemInit

      val vertexSource = """
        attribute vec3 position;
        attribute vec2 texCoord;
        
        varying vec2 vTexCoord;
  
        void main(void) {
          gl_Position = vec4(position, 1.0);
          vTexCoord = texCoord;
        }
        """

      val fragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        varying vec2 vTexCoord;
        
        uniform sampler2D texSampler;
  
        void main(void) {
          gl_FragColor = texture2D(texSampler, vTexCoord);
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
      val attribCoordLocation = mgl.getAttribLocation(program, "texCoord")
      val uniformTexSamplerLocation = mgl.getUniformLocation(program, "texSampler")

      val vertexBuffer = mgl.createBuffer
      val indicesBuffer = mgl.createBuffer
      val textureCoordBuffer = mgl.createBuffer

      val vertexBufferData = Macrogl.createFloatData(4 * 3) // 4 vertices (3 components each)
      vertexBufferData.put(-0.2f).put(-0.35f).put(0)
      vertexBufferData.put(0.2f).put(-0.35f).put(0)
      vertexBufferData.put(0.2f).put(0.35f).put(0)
      vertexBufferData.put(-0.2f).put(0.35f).put(0)
      vertexBufferData.rewind()

      val indicesBufferData = Macrogl.createShortData(2 * 3) // 2 triangles (3 vertices each)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.put(0.toShort).put(2.toShort).put(3.toShort)
      indicesBufferData.rewind()

      val textureCoordBufferData = Macrogl.createFloatData(4 * 2) // 4 vertices (2 components each)
      textureCoordBufferData.put(0).put(0)
      textureCoordBufferData.put(1).put(0)
      textureCoordBufferData.put(1).put(1)
      textureCoordBufferData.put(0).put(1)
      textureCoordBufferData.rewind()

      mgl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, vertexBufferData, GL.STATIC_DRAW)
      mgl.vertexAttribPointer(attribPosLocation, 3, GL.FLOAT, false, 0, 0)

      mgl.bindBuffer(GL.ARRAY_BUFFER, textureCoordBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, textureCoordBufferData, GL.STATIC_DRAW)
      mgl.vertexAttribPointer(attribCoordLocation, 2, GL.FLOAT, false, 0, 0)

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      val texture = mgl.createTexture()
      mgl.activeTexture(GL.TEXTURE0)
      mgl.bindTexture(GL.TEXTURE_2D, texture)
      mgl.uniform1i(uniformTexSamplerLocation, 0)

      // Be careful about WebGL and textures: http://www.khronos.org/webgl/wiki/WebGL_and_OpenGL_Differences
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR)

      // Not mandatory, but good to have
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE)
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE)

      var textureReady = false
      org.macrogl.Utils.loadTexture2DFromResources("/org/macrogl/examples/backend/common/testTexture.jpg", texture, mgl, { textureReady = true; print("Texture ready"); true })

      mgl.clearColor(1, 0, 0, 1)

      mgl.enableVertexAttribArray(attribPosLocation)
      mgl.enableVertexAttribArray(attribCoordLocation)

      print("Example ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: org.macrogl.FrameEvent): Unit = {
        //print("Elapsed seconds since last frame: " + fe.elapsedTime)

        mgl.clear(GL.COLOR_BUFFER_BIT)
        //if (textureReady) {
        mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)
        //}

        continueCondition = systemUpdate
      }

      def close(): Unit = {
        print("Closing example")

        mgl.disableVertexAttribArray(attribCoordLocation)
        mgl.disableVertexAttribArray(attribPosLocation)

        mgl.deleteBuffer(textureCoordBuffer)
        mgl.deleteBuffer(indicesBuffer)
        mgl.deleteBuffer(vertexBuffer)

        mgl.deleteShader(vertex)
        mgl.deleteShader(fragment)

        mgl.deleteProgram(program)

        systemClose

        print("Example closed")
      }

      funcs = Some(continue, render, close)
    }

    def continue(): Boolean = {
      funcs match {
        case Some((continueFunc, _, _)) => continueFunc()
        case None => throw new RuntimeException("Not ready")
      }
    }
    def render(fe: org.macrogl.FrameEvent): Unit = {
      funcs match {
        case Some((_, renderFunc, _)) => renderFunc(fe)
        case None => throw new RuntimeException("Not ready")
      }
    }
    def close(): Unit = {
      funcs match {
        case Some((_, _, closeFunc)) => closeFunc()
        case None => throw new RuntimeException("Not ready")
      }

      funcs = None
    }
  }

  def start(): Unit = {
    org.macrogl.Utils.startFrameListener(new BasicTextureListener)
  }
}