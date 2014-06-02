package org.macrogl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

object Utils {
  def LWJGLSpecifics: Nothing = throw new UnsupportedOperationException("Available only when using the JVM platform")
  
  object WebGLSpecifics {
    private var resourceRelativePath: String = "."

    def setResourcePath(path: String): Unit = {
      resourceRelativePath = path
    }

    def getResourcePath = resourceRelativePath
  }

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
  
  private def now():Double = g.Date.now().asInstanceOf[js.Number].toDouble
  
  private class FrameListenerLoopContext {
    var lastLoopTime: Double = now()
  }
  
  def startFrameListener(fl: FrameListener): Unit = {
    val ctx = new FrameListenerLoopContext
    
    def loop(timeStamp: js.Any): Unit = {
      if(fl.continue) {
        val currentTime = now()
        val diff = ((currentTime - ctx.lastLoopTime)/1e3).toFloat
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