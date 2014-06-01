package org.macrogl.examples.backend.common

import org.macrogl.Utils
import org.macrogl.Macrogl
import org.macrogl.{ Macrogl => GL }

import org.macrogl.math._

class BasicRenderToTexture(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean, systemInit: () => Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicRenderToTextureListener extends org.macrogl.FrameListener {
    
    val textureWidth = 600
    val textureHeight = 400
    
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic RenderToTexture: init")

      val mgl = systemInit()

      //#### PROJECTION (3D) ####
      
      // Shaders
      val projectionVertexSource = """
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

      val projectionFragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        varying vec3 vColor;
  
        void main(void) {
          gl_FragColor = vec4(vColor, 1.0);
        }
        """

      val projectionProgram = mgl.createProgram()
      val projectionVertex = mgl.createShader(GL.VERTEX_SHADER)
      val projectionFragment = mgl.createShader(GL.FRAGMENT_SHADER)

      mgl.shaderSource(projectionVertex, projectionVertexSource)
      mgl.shaderSource(projectionFragment, projectionFragmentSource)

      mgl.compileShader(projectionVertex)
      mgl.compileShader(projectionFragment)

      if (mgl.getShaderParameterb(projectionVertex, GL.COMPILE_STATUS) == false)
        print("Vertex compilation error: " + mgl.getShaderInfoLog(projectionVertex))
      if (mgl.getShaderParameterb(projectionFragment, GL.COMPILE_STATUS) == false)
        print("Fragment compilation error: " + mgl.getShaderInfoLog(projectionFragment))

      mgl.attachShader(projectionProgram, projectionVertex)
      mgl.attachShader(projectionProgram, projectionFragment)

      mgl.linkProgram(projectionProgram)

      if (mgl.getProgramParameterb(projectionProgram, GL.LINK_STATUS) == false)
        print("Program linking error: " + mgl.getProgramInfoLog(projectionProgram))

      mgl.validateProgram(projectionProgram)

      if (mgl.getProgramParameterb(projectionProgram, GL.VALIDATE_STATUS) == false)
        print("Program validation error: " + mgl.getProgramInfoLog(projectionProgram))

      val attribPosLocation = mgl.getAttribLocation(projectionProgram, "position")
      val attribColorLocation = mgl.getAttribLocation(projectionProgram, "color")
      val uniformProjectionLocation = mgl.getUniformLocation(projectionProgram, "projection")
      val uniformTransformLocation = mgl.getUniformLocation(projectionProgram, "transform")

      // Buffers
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

      mgl.bindBuffer(GL.ARRAY_BUFFER, colorBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, colorBufferData, GL.STATIC_DRAW)

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      // Setup matrices
      val projection = Matrix4f.perspective3D(70f, textureWidth.toFloat / textureHeight.toFloat, 0.01f, 10f)
      val projectionTransformStack = new MatrixStack(new Matrix4f)

      //#### SCREEN RENDERING (2D) ####
      
      // Shaders
      val fullscreenVertexSource = """
        uniform mat3 projection;
        uniform mat3 transform;
        
        attribute vec2 position;
        attribute vec2 texCoord;
        
        varying vec2 vTexCoord;
  
        void main(void) {
          vec3 transformedPosition = projection * transform * vec3(position, 1.0);
          gl_Position = vec4(transformedPosition.x, transformedPosition.y, 0.0, transformedPosition.z);
          vTexCoord = texCoord;
        }
        """

      val fullscreenFragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        varying vec2 vTexCoord;
        
        uniform sampler2D texSampler;
  
        void main(void) {
          gl_FragColor = texture2D(texSampler, vTexCoord);
        }
        """

      val fullscreenProgram = mgl.createProgram
      val fullscreenVertex = mgl.createShader(GL.VERTEX_SHADER)
      val fullscreenFragment = mgl.createShader(GL.FRAGMENT_SHADER)

      mgl.shaderSource(fullscreenVertex, fullscreenVertexSource)
      mgl.shaderSource(fullscreenFragment, fullscreenFragmentSource)

      mgl.compileShader(fullscreenVertex)
      mgl.compileShader(fullscreenFragment)

      if (mgl.getShaderParameterb(fullscreenVertex, GL.COMPILE_STATUS) == false)
        print("Vertex compilation error: " + mgl.getShaderInfoLog(fullscreenVertex))
      if (mgl.getShaderParameterb(fullscreenFragment, GL.COMPILE_STATUS) == false)
        print("Fragment compilation error: " + mgl.getShaderInfoLog(fullscreenFragment))

      mgl.attachShader(fullscreenProgram, fullscreenVertex)
      mgl.attachShader(fullscreenProgram, fullscreenFragment)

      mgl.linkProgram(fullscreenProgram)

      if (mgl.getProgramParameterb(fullscreenProgram, GL.LINK_STATUS) == false)
        print("Program linking error: " + mgl.getProgramInfoLog(fullscreenProgram))

      mgl.validateProgram(fullscreenProgram)

      if (mgl.getProgramParameterb(fullscreenProgram, GL.VALIDATE_STATUS) == false)
        print("Program validation error: " + mgl.getProgramInfoLog(fullscreenProgram))

      val fullscreenAttribPosLocation = mgl.getAttribLocation(fullscreenProgram, "position")
      val fullscreenAttribTexCoordLocation = mgl.getAttribLocation(fullscreenProgram, "texCoord")
      val fullscreenUniformTexLocation = mgl.getUniformLocation(fullscreenProgram, "texSampler")
      val fullscreenUniformTransformLocation = mgl.getUniformLocation(fullscreenProgram, "transform")
      val fullscreenUniformProjectionLocation = mgl.getUniformLocation(fullscreenProgram, "projection")

      // Buffers
      val fullscreenVertexBuffer = mgl.createBuffer
      val fullscreenTexCoordBuffer = mgl.createBuffer
      val fullscreenIndicesBuffer = mgl.createBuffer

      val w = textureWidth.toFloat/2
      val h = textureHeight.toFloat/2
      
      val fullscreenVertexBufferData = Macrogl.createFloatData(4 * 2)
      fullscreenVertexBufferData.put(-w).put(-h)
      fullscreenVertexBufferData.put(w).put(-h)
      fullscreenVertexBufferData.put(w).put(h)
      fullscreenVertexBufferData.put(-w).put(h)
      fullscreenVertexBufferData.rewind

      val fullscreenTexCoordBufferData = Macrogl.createFloatData(4 * 2)
      fullscreenTexCoordBufferData.put(0f).put(0f)
      fullscreenTexCoordBufferData.put(1f).put(0f)
      fullscreenTexCoordBufferData.put(1f).put(1f)
      fullscreenTexCoordBufferData.put(0f).put(1f)
      fullscreenTexCoordBufferData.rewind

      val fullscreenIndicesBufferData = Macrogl.createShortData(2 * 3)
      fullscreenIndicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
      fullscreenIndicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
      fullscreenIndicesBufferData.rewind

      mgl.bindBuffer(GL.ARRAY_BUFFER, fullscreenVertexBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, fullscreenVertexBufferData, GL.STATIC_DRAW)

      mgl.bindBuffer(GL.ARRAY_BUFFER, fullscreenTexCoordBuffer)
      mgl.bufferData(GL.ARRAY_BUFFER, fullscreenTexCoordBufferData, GL.STATIC_DRAW)

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBufferData, GL.STATIC_DRAW)
      
      // Setup matrices
      val fullscreenProjection = Matrix3f.ortho2D(-width.toFloat/2, width.toFloat/2, -height.toFloat/2, height.toFloat/2)
      val fullscreenTransformStack = new MatrixStack(new Matrix3f)
      
      //#### RENDER TO TEXTURE ####
      
      val targetTexture = mgl.createTexture
      mgl.bindTexture(GL.TEXTURE_2D, targetTexture)
      mgl.texImage2D(GL.TEXTURE_2D, 0, GL.RGB, textureWidth, textureHeight, 0, GL.RGB, GL.UNSIGNED_BYTE) // Allocate memory
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR)
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE)
      mgl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE)
      
      val renderbufferDepth = mgl.createRenderbuffer
      mgl.bindRenderbuffer(GL.RENDERBUFFER, renderbufferDepth)
      mgl.renderbufferStorage(GL.RENDERBUFFER, GL.DEPTH_COMPONENT16, textureWidth, textureHeight)
      
      val framebuffer = mgl.createFramebuffer
      mgl.bindFramebuffer(GL.FRAMEBUFFER, framebuffer)
      mgl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, targetTexture, 0) // attach the color buffer
      mgl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.RENDERBUFFER, renderbufferDepth) // attach the depth buffer
      
      if(mgl.checkFramebufferStatus(GL.FRAMEBUFFER) != GL.FRAMEBUFFER_COMPLETE)
        print("Framebuffer incomplete")

      print("Basic RenderToTexture: ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      var currentRotation: Float = 0f
      val rotationVelocity: Float = -60f
      
      var currentScreenRotation: Float = 0f
      val screenRotationVelocity: Float = -20f

      def render(fe: org.macrogl.FrameEvent): Unit = {
        // Anime the rotation using the data from the FrameEvent
        currentRotation += rotationVelocity * fe.elapsedTime
        currentScreenRotation += screenRotationVelocity * fe.elapsedTime
        
        //#### PROJECTION ####
        // Render into the framebuffer
        mgl.bindFramebuffer(GL.FRAMEBUFFER, framebuffer)
        mgl.viewport(0, 0, textureWidth, textureHeight)
        
        projectionTransformStack.push // Save the current transformation matrix

        projectionTransformStack.current = Matrix4f.translate3D(new Vector3f(0, 0, -2)) *
          Matrix4f.rotation3D(currentRotation, new Vector3f(0, 1, 0))

        mgl.useProgram(projectionProgram)
        
        mgl.clearColor(0.5f, 0.5f, 0.5f, 1)
        mgl.clear(GL.COLOR_BUFFER_BIT)

        mgl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
        mgl.vertexAttribPointer(attribPosLocation, 3, GL.FLOAT, false, 0, 0)
        mgl.enableVertexAttribArray(attribPosLocation)

        mgl.bindBuffer(GL.ARRAY_BUFFER, colorBuffer)
        mgl.enableVertexAttribArray(attribColorLocation)
        mgl.vertexAttribPointer(attribColorLocation, 3, GL.FLOAT, false, 0, 0)

        // Send the current transformation to the shader
        mgl.uniformMatrix4f(uniformTransformLocation, projectionTransformStack.current)

        // Send the projection to the shader
        mgl.uniformMatrix4f(uniformProjectionLocation, projection)

        mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
        mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

        mgl.disableVertexAttribArray(attribColorLocation)
        mgl.disableVertexAttribArray(attribPosLocation)

        projectionTransformStack.pop // Restore the transformation matrix
        
        //#### FULLSCREEN ####
        
        // Render into the screen
        mgl.bindFramebuffer(GL.FRAMEBUFFER, org.macrogl.Token.FrameBuffer.none)
        mgl.viewport(0, 0, width, height)
        
        fullscreenTransformStack.push
        
        val scaleFactor = width.toFloat / textureWidth.toFloat * 0.75f
        
        fullscreenTransformStack.current = Matrix3f.scale2D(new Vector2f(scaleFactor, scaleFactor)) * Matrix3f.rotation2D(currentScreenRotation)
        
        mgl.useProgram(fullscreenProgram)
        
        mgl.clearColor(0f, 0f, 0f, 1)
        mgl.clear(GL.COLOR_BUFFER_BIT)
        
        mgl.uniformMatrix3f(fullscreenUniformTransformLocation, fullscreenTransformStack.current)
        
        mgl.uniformMatrix3f(fullscreenUniformProjectionLocation, fullscreenProjection)
        
        mgl.activeTexture(GL.TEXTURE0)
        mgl.bindTexture(GL.TEXTURE_2D, targetTexture)
        
        mgl.uniform1i(fullscreenUniformTexLocation, 0)
        
        mgl.bindBuffer(GL.ARRAY_BUFFER, fullscreenVertexBuffer)
        mgl.vertexAttribPointer(fullscreenAttribPosLocation, 2, GL.FLOAT, false, 0, 0)
        mgl.enableVertexAttribArray(fullscreenAttribPosLocation)
        
        mgl.bindBuffer(GL.ARRAY_BUFFER, fullscreenTexCoordBuffer)
        mgl.vertexAttribPointer(fullscreenAttribTexCoordLocation, 2, GL.FLOAT, false, 0, 0)
        mgl.enableVertexAttribArray(fullscreenAttribTexCoordLocation)
        
        mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBuffer)
        mgl.drawElements(GL.TRIANGLES, fullscreenIndicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)
        
        mgl.disableVertexAttribArray(fullscreenAttribTexCoordLocation)
        mgl.disableVertexAttribArray(fullscreenAttribPosLocation)
        
        fullscreenTransformStack.pop
        
        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic RenderToTexture: closing")
        
      	mgl.deleteBuffer(fullscreenVertexBuffer)
      	mgl.deleteBuffer(fullscreenTexCoordBuffer)
      	mgl.deleteBuffer(fullscreenIndicesBuffer)
        
        mgl.deleteShader(fullscreenVertex)
        mgl.deleteShader(fullscreenFragment)

        mgl.deleteProgram(fullscreenProgram)

        mgl.deleteBuffer(indicesBuffer)
        mgl.deleteBuffer(colorBuffer)
        mgl.deleteBuffer(vertexBuffer)

        mgl.deleteShader(projectionVertex)
        mgl.deleteShader(projectionFragment)

        mgl.deleteProgram(projectionProgram)

        systemClose()

        print("Basic RenderToTexture: closed")
      }

      funcs = Some(continue, render, close)
    }

    val errMsg = "Basic RenderToTexture: not ready"

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
    org.macrogl.Utils.startFrameListener(new BasicRenderToTextureListener)
  }
}