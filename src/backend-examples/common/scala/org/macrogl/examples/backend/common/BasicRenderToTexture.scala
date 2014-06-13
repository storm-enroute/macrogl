package org.macrogl.examples.backend.common

import org.macrogl
import org.macrogl.{ Macrogl => GL }
import org.macrogl.using

import org.macrogl.math._

/**
 * Basic example of render-to-texture
 * This example use features of OpenGL 3, so it will not work on some old Intel IGP,
 * you should use at least a Sandy Bridge processor, though we had it working with an Arrandale and an OpenGL 2.1 context.
 */
class BasicRenderToTexture(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => macrogl.Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicRenderToTextureListener extends org.macrogl.FrameListener {

    val textureWidth = 600
    val textureHeight = 400

    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic RenderToTexture: init")

      implicit val mgl = systemInit()

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

      val projProgram = new macrogl.Program("BasicRenderToTexture(projection)")(
        macrogl.Program.Shader.Vertex(projectionVertexSource),
        macrogl.Program.Shader.Fragment(projectionFragmentSource))
      projProgram.acquire()

      // Buffers
      val indicesBuffer = mgl.createBuffer

      // position: 1 face (4 vertices)
      val vertexBufferData = macrogl.Macrogl.createFloatData(4 * 3)
      vertexBufferData.put(-1f).put(-0.5f).put(0f)
      vertexBufferData.put(1f).put(-0.5f).put(0f)
      vertexBufferData.put(1f).put(0.5f).put(0f)
      vertexBufferData.put(-1f).put(0.5f).put(0f)
      vertexBufferData.rewind

      // color: 1 face (4 vertices)
      val colorBufferData = macrogl.Macrogl.createFloatData(4 * 3)
      colorBufferData.put(1f).put(0f).put(0f)
      colorBufferData.put(0f).put(1f).put(0f)
      colorBufferData.put(1f).put(1f).put(0f)
      colorBufferData.put(0f).put(0f).put(1f)
      colorBufferData.rewind

      // 1 faces = 2 triangles
      val indicesBufferData = macrogl.Macrogl.createShortData(2 * 3)
      indicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
      indicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
      indicesBufferData.rewind

      // Fill the OpenGL buffers with the data
      val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)
      for (_ <- using attributebuffer (vertexBuffer)) {
        val vertexAttrsLocs = Array(mgl.getAttribLocation(projProgram.token, "position"))
        val vertexAttrsCfg = Array((0, 3))

        vertexBuffer.locations = vertexAttrsLocs
        vertexBuffer.attribs = vertexAttrsCfg
      }

      val colorBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, colorBufferData.remaining() / 3, 3)
      colorBuffer.acquire()
      colorBuffer.send(0, colorBufferData)
      for (_ <- using attributebuffer (colorBuffer)) {
        val colorAttrsLocs = Array(mgl.getAttribLocation(projProgram.token, "color"))
        val colorAttrsCfg = Array((0, 3))

        colorBuffer.locations = colorAttrsLocs
        colorBuffer.attribs = colorAttrsCfg
      }

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

      val fullProgram = new macrogl.Program("BasicRenderToTexture(fullscreen)")(
        macrogl.Program.Shader.Vertex(fullscreenVertexSource),
        macrogl.Program.Shader.Fragment(fullscreenFragmentSource))
      fullProgram.acquire()

      // Buffers
      val fullscreenIndicesBuffer = mgl.createBuffer

      val w = textureWidth.toFloat / 2
      val h = textureHeight.toFloat / 2

      val fullscreenVertexBufferData = macrogl.Macrogl.createFloatData(4 * 2)
      fullscreenVertexBufferData.put(-w).put(-h)
      fullscreenVertexBufferData.put(w).put(-h)
      fullscreenVertexBufferData.put(w).put(h)
      fullscreenVertexBufferData.put(-w).put(h)
      fullscreenVertexBufferData.rewind

      val fullscreenTexCoordBufferData = macrogl.Macrogl.createFloatData(4 * 2)
      fullscreenTexCoordBufferData.put(0f).put(0f)
      fullscreenTexCoordBufferData.put(1f).put(0f)
      fullscreenTexCoordBufferData.put(1f).put(1f)
      fullscreenTexCoordBufferData.put(0f).put(1f)
      fullscreenTexCoordBufferData.rewind

      val fullscreenIndicesBufferData = macrogl.Macrogl.createShortData(2 * 3)
      fullscreenIndicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
      fullscreenIndicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
      fullscreenIndicesBufferData.rewind

      val fullscreenVertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, fullscreenVertexBufferData.remaining() / 2, 2)
      fullscreenVertexBuffer.acquire()
      fullscreenVertexBuffer.send(0, fullscreenVertexBufferData)
      for (_ <- using attributebuffer (fullscreenVertexBuffer)) {
        val vertexAttrsLocs = Array(mgl.getAttribLocation(fullProgram.token, "position"))
        val vertexAttrsCfg = Array((0, 2))

        fullscreenVertexBuffer.locations = vertexAttrsLocs
        fullscreenVertexBuffer.attribs = vertexAttrsCfg
      }

      val fullscreenTexCoordBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, colorBufferData.remaining() / 2, 2)
      fullscreenTexCoordBuffer.acquire()
      fullscreenTexCoordBuffer.send(0, fullscreenTexCoordBufferData)
      for (_ <- using attributebuffer (fullscreenTexCoordBuffer)) {
        val texCoordAttrsLocs = Array(mgl.getAttribLocation(fullProgram.token, "texCoord"))
        val texCoordAttrsCfg = Array((0, 2))

        fullscreenTexCoordBuffer.locations = texCoordAttrsLocs
        fullscreenTexCoordBuffer.attribs = texCoordAttrsCfg
      }

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBufferData, GL.STATIC_DRAW)

      // Setup matrices
      val fullscreenProjection = Matrix3f.ortho2D(-width.toFloat / 2, width.toFloat / 2, -height.toFloat / 2, height.toFloat / 2)
      val fullscreenTransformStack = new MatrixStack(new Matrix3f)

      //#### RENDER TO TEXTURE ####

      val textureUnit = 0

      val targetTexture = macrogl.Texture(GL.TEXTURE_2D) { texture =>
        mgl.texImage2D(GL.TEXTURE_2D, 0, GL.RGB, textureWidth, textureHeight, 0, GL.RGB, GL.UNSIGNED_BYTE) // Allocate memory
        texture.magFilter = GL.LINEAR
        texture.minFilter = GL.LINEAR
        texture.wrapS = GL.CLAMP_TO_EDGE
        texture.wrapT = GL.CLAMP_TO_EDGE
      }
      targetTexture.acquire()

      val renderbufferDepth = mgl.createRenderbuffer
      mgl.bindRenderbuffer(GL.RENDERBUFFER, renderbufferDepth)
      mgl.renderbufferStorage(GL.RENDERBUFFER, GL.DEPTH_COMPONENT16, textureWidth, textureHeight)

      val framebuffer = mgl.createFramebuffer
      mgl.bindFramebuffer(GL.FRAMEBUFFER, framebuffer)
      mgl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, targetTexture.token, 0) // attach the color buffer
      mgl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.RENDERBUFFER, renderbufferDepth) // attach the depth buffer

      if (mgl.checkFramebufferStatus(GL.FRAMEBUFFER) != GL.FRAMEBUFFER_COMPLETE)
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

        for (_ <- using program (projProgram)) {
          mgl.clearColor(0.5f, 0.5f, 0.5f, 1)
          mgl.clear(GL.COLOR_BUFFER_BIT)

          for (_ <- using attributebuffer (vertexBuffer)) {
            vertexBuffer.setAttributePointers()
          }
          for (_ <- using attributebuffer (colorBuffer)) {
            colorBuffer.setAttributePointers()
          }

          vertexBuffer.enableAttributeArrays()
          colorBuffer.enableAttributeArrays()

          projProgram.uniform.projection = projection
          projProgram.uniform.transform = projectionTransformStack.current

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

          colorBuffer.disableAttributeArrays()
          vertexBuffer.disableAttributeArrays()
        }

        projectionTransformStack.pop // Restore the transformation matrix

        //#### FULLSCREEN ####

        // Render into the screen
        mgl.bindFramebuffer(GL.FRAMEBUFFER, org.macrogl.Token.FrameBuffer.none)
        mgl.viewport(0, 0, width, height)

        fullscreenTransformStack.push

        val scaleFactor = width.toFloat / textureWidth.toFloat * 0.75f

        fullscreenTransformStack.current = Matrix3f.scale2D(new Vector2f(scaleFactor, scaleFactor)) *
          Matrix3f.rotation2D(currentScreenRotation)

        for {
          _ <- using program (fullProgram)
          _ <- using texture (GL.TEXTURE0 + textureUnit, targetTexture)
        } {
          mgl.clearColor(0f, 0f, 0f, 1)
          mgl.clear(GL.COLOR_BUFFER_BIT)

          for (_ <- using attributebuffer (fullscreenVertexBuffer)) {
            fullscreenVertexBuffer.setAttributePointers()
          }
          for (_ <- using attributebuffer (fullscreenTexCoordBuffer)) {
            fullscreenTexCoordBuffer.setAttributePointers()
          }

          val fullscreenUniformTexLocation = mgl.getUniformLocation(fullProgram.token, "texSampler")
          mgl.uniform1i(fullscreenUniformTexLocation, textureUnit)

          fullscreenVertexBuffer.enableAttributeArrays()
          fullscreenTexCoordBuffer.enableAttributeArrays()

          fullProgram.uniform.projection = fullscreenProjection
          fullProgram.uniform.transform = fullscreenTransformStack.current

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, fullscreenIndicesBuffer)
          mgl.drawElements(GL.TRIANGLES, fullscreenIndicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

          fullscreenTexCoordBuffer.disableAttributeArrays()
          fullscreenVertexBuffer.disableAttributeArrays()
        }

        fullscreenTransformStack.pop

        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic RenderToTexture: closing")

        targetTexture.release()

        mgl.deleteBuffer(fullscreenIndicesBuffer)
        
        fullscreenTexCoordBuffer.release()
        fullscreenVertexBuffer.release()

        fullProgram.release()

        mgl.deleteBuffer(indicesBuffer)
        
        colorBuffer.release()
        vertexBuffer.release()

        projProgram.release()

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