package org.macrogl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

object Utils {
  /**
   * Specifics when using the LWJGL back end with the JVM.
   * Not accessible when not using the JVM.
   */
  def LWJGLSpecifics: Nothing = throw new UnsupportedOperationException("Available only when using the JVM platform")

  /**
   * Specifics when using the WebGL back end with Scala.js.
   * Not accessible when not using Scala.js.
   */
  object WebGLSpecifics {
    private var resourceRelativePath: String = "."

    /**
     * Sets the relative path to access the resources.
     * If you the page is at the root of your SBT, the path should look like "./target/scala-2.11/classes" by default.
     */
    def setResourcePath(path: String): Unit = {
      resourceRelativePath = path
    }

    /**
     * Gets the relative path to access the resources.
     */
    def getResourcePath = resourceRelativePath
  }

  /**
   * Load a image from the resources into an OpenGL 2D texture.
   *
   *
   * @param resourceName The Fully qualified path of the resource image
   * @param texture The token of the texture where the decoded texture have to be loaded
   * @param gl The Macrogl instance to use to load the texture into OpenGL
   * @param preload Optional function called by the OpenGL thread after the image has been decoded but before it is loaded into OpenGL.
   * A returned value false means aborting the texture loading
   */
  def loadTexture2DFromResources(resourceName: String, texture: Token.Texture, gl: Macrogl, preload: => Boolean = { true }): Unit = {
    val image = dom.document.createElement("img").asInstanceOf[js.Dynamic]
    image.onload = ({ (e: dom.Event) =>
      if (preload) {
        val previousTexture = gl.getParameterTexture(Macrogl.TEXTURE_BINDING_2D)
        gl.bindTexture(Macrogl.TEXTURE_2D, texture)
        val webglRenderingContext = gl.getWebGLRenderingContext().asInstanceOf[js.Dynamic]
        webglRenderingContext.pixelStorei(webglRenderingContext.UNPACK_FLIP_Y_WEBGL, true)
        webglRenderingContext.texImage2D(Macrogl.TEXTURE_2D, 0, Macrogl.RGBA, Macrogl.RGBA, Macrogl.UNSIGNED_BYTE, image)
        gl.bindTexture(Macrogl.TEXTURE_2D, previousTexture)
      }
    })
    image.src = WebGLSpecifics.getResourcePath + resourceName
  }

  private def now(): Double = g.Date.now().asInstanceOf[js.Number].toDouble

  private class FrameListenerLoopContext {
    var lastLoopTime: Double = now()
  }

  /**
   * Start the FrameListener into a separate thread while the following logical flow:
   * {{{
   * val fl:FrameListener = ...
   * fl.init
   * while(fl.continue) {
   *   fl.render
   * }
   * fl.close
   * }}}
   */
  def startFrameListener(fl: FrameListener): Unit = {
    val ctx = new FrameListenerLoopContext

    def loop(timeStamp: js.Any): Unit = {
      if (fl.continue) {
        val currentTime = now()
        val diff = ((currentTime - ctx.lastLoopTime) / 1e3).toFloat
        ctx.lastLoopTime = currentTime

        val frameEvent = FrameEvent(diff)

        fl.render(frameEvent)

        g.window.requestAnimationFrame(loop _)
      } else {
        fl.close
      }
    }

    def loopInit(timeStamp: js.Any): Unit = {
      fl.init
      loop(timeStamp)
    }

    // Start listener
    g.window.requestAnimationFrame(loopInit _)
  }
}