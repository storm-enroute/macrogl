package org.macrogl.examples.backend.common

import org.macrogl
import org.macrogl.{ Macrogl => GL }
import org.macrogl.using

/**
 * Basic example of texturing
 */
class BasicTexture(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => macrogl.Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicTextureListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Texture: init")

      implicit val mgl = systemInit()

      // Prepare data 

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

      val indicesBuffer = mgl.createBuffer

      val vertexBufferData = macrogl.Macrogl.createFloatData(4 * 3) // 4 vertices (3 components each)
      vertexBufferData.put(-0.4f).put(-0.25f).put(0)
      vertexBufferData.put(0.4f).put(-0.25f).put(0)
      vertexBufferData.put(0.4f).put(0.25f).put(0)
      vertexBufferData.put(-0.4f).put(0.25f).put(0)
      vertexBufferData.rewind()

      // WebGL does not support INTEGER for index buffers, use SHORT instead if you want a portable behavior
      val indicesBufferData = macrogl.Macrogl.createShortData(2 * 3) // 2 triangles (3 vertices each)
      indicesBufferData.put(0.toShort).put(1.toShort).put(2.toShort)
      indicesBufferData.put(0.toShort).put(2.toShort).put(3.toShort)
      indicesBufferData.rewind()

      val textureCoordBufferData = macrogl.Macrogl.createFloatData(4 * 2) // 4 vertices (2 components each)
      textureCoordBufferData.put(0).put(0)
      textureCoordBufferData.put(1).put(0)
      textureCoordBufferData.put(1).put(1)
      textureCoordBufferData.put(0).put(1)
      textureCoordBufferData.rewind()

      mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
      mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

      // Setup
      mgl.viewport(0, 0, width, height)
      mgl.clearColor(0, 0, 1, 1)

      val pp = new macrogl.Program("BasicTexture")(
        macrogl.Program.Shader.Vertex(vertexSource),
        macrogl.Program.Shader.Fragment(fragmentSource))
      pp.acquire()

      val vertexAttrsCfg = Array((0, 3))
      val vertexAttrsLocs = Array(mgl.getAttribLocation(pp.token, "position"))
      val coordAttrsCfg = Array((0, 2))
      val coordAttrsLocs = Array(mgl.getAttribLocation(pp.token, "texCoord"))

      val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
      vertexBuffer.acquire()
      vertexBuffer.send(0, vertexBufferData)

      val texCoordBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, textureCoordBufferData.remaining() / 2, 2)
      texCoordBuffer.acquire()
      texCoordBuffer.send(0, textureCoordBufferData)

      for (_ <- using attributebuffer (vertexBuffer)) {
        vertexBuffer.setLocations(vertexAttrsLocs)
        vertexBuffer.setAttribsCfg(vertexAttrsCfg)

        vertexBuffer.setAttributePointers()
      }

      for (_ <- using attributebuffer (texCoordBuffer)) {
        texCoordBuffer.setLocations(coordAttrsLocs)
        texCoordBuffer.setAttribsCfg(coordAttrsCfg)

        texCoordBuffer.setAttributePointers()
      }

      val textureUnit = 0
      val texture = macrogl.Texture(GL.TEXTURE_2D) { texture =>
        macrogl.Utils.loadTexture2DFromResources("/org/macrogl/examples/backend/common/macrogl.png", texture.token)
        // Be careful about WebGL and textures: http://www.khronos.org/webgl/wiki/WebGL_and_OpenGL_Differences
        texture.magFilter = GL.LINEAR
        texture.minFilter = GL.LINEAR

        // Not mandatory, but good to have
        texture.wrapS = GL.CLAMP_TO_EDGE
        texture.wrapT = GL.CLAMP_TO_EDGE
      }
      texture.acquire()

      // Enable transparency (looks better for textures that support it)
      mgl.enable(GL.BLEND)
      mgl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

      print("Basic Texture: ready")

      var continueCondition: Boolean = true

      def continue(): Boolean = {
        continueCondition
      }

      def render(fe: org.macrogl.FrameEvent): Unit = {

        mgl.clear(GL.COLOR_BUFFER_BIT)

        for {
          _ <- using program (pp)
          _ <- using texture (GL.TEXTURE0 + textureUnit, texture)
        } {
          vertexBuffer.enableAttributeArrays()
          texCoordBuffer.enableAttributeArrays()

          pp.uniform.texSampler = textureUnit

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

          texCoordBuffer.disableAttributeArrays()
          vertexBuffer.disableAttributeArrays()
        }

        continueCondition = systemUpdate()
      }

      def close(): Unit = {
        print("Basic Texture: closing")

        texture.release()
        texCoordBuffer.release()
        vertexBuffer.release()
        mgl.deleteBuffer(indicesBuffer)
        pp.release()

        systemClose()

        print("Basic Texture: closed")
      }

      funcs = Some(continue, render, close)
    }

    val errMsg = "Basic Texture: not ready"

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
    org.macrogl.Utils.startFrameListener(new BasicTextureListener)
  }
}