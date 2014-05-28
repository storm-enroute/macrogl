package org.macrogl

import scala.concurrent._
import ExecutionContext.Implicits.global
import javax.imageio.ImageIO
import java.util.concurrent.ConcurrentLinkedQueue

import org.lwjgl.opengl._

object Utils {
  object LWJGLSettings {
    private var context: Option[SharedDrawable] = None
    def storeCurrentThreadContext = {
      val ctx = new SharedDrawable(Display.getDrawable())
      context = Some(ctx)
    }
    def useStoredContextForCurrentThread = context match {
      case Some(ctx) => ctx.makeCurrent()
      case None => throw new RuntimeException("There is no context currently stored")
    }
  }
  
  def WebGLSettings: Nothing = throw new UnsupportedOperationException("Available only when using Scala.js")

  def loadTexture2DFromResources(resourceName: String, gl: Macrogl, texture: Token.Texture, textureInternalFormat: Int, preload: => Boolean = { true }): Unit = {
    val stream = this.getClass().getClassLoader().getResourceAsStream(resourceName)

    // TODO should we have our own ExecutionContext?

    Future {
      // Should support JPEG, PNG, BMP, WBMP and GIF
      val image = ImageIO.read(stream)

      val height = image.getHeight()
      val width = image.getWidth()

      val byteBuffer = Macrogl.createByteData(4 * width * height) // Stored as RGBA value: 4 bytes per pixel

      var y = 0
      while (y < height) {

        var x = 0
        while (x < width) {

          val rgba = image.getRGB(x, y)
          byteBuffer.putInt(rgba)

          x += 1
        }

        y += 1
      }

      // Don't load it now, we want it done synchronously in the main loop to avoid concurrency issue
      executionList.add({ () =>
        if (preload) {
          val previousTexture = gl.getParameterTexture(Macrogl.TEXTURE_BINDING_2D)
          gl.bindTexture(Macrogl.TEXTURE_2D, texture)
          gl.texImage2D(Macrogl.TEXTURE_2D, 0, textureInternalFormat, width, height, 0, Macrogl.RGBA, Macrogl.UNSIGNED_BYTE, byteBuffer)
          gl.bindTexture(Macrogl.TEXTURE_2D, previousTexture)
        }
      })
    }
  }

  private val executionList = new ConcurrentLinkedQueue[() => Unit]
  private def flushExecutionList(): Unit = {
    var current: () => Unit = null
    while ({ current = executionList.poll(); current } != null) {
      current()
    }
  }

  private var lastLoopTime: Long = 0
  def setRenderingLoop(cond: => Boolean)(onLoop: FrameEvent => Unit)(close: => Unit): Unit = {
    LWJGLSettings.storeCurrentThreadContext
    
    lastLoopTime = System.nanoTime()

    val renderingThread = new Thread(new Runnable {
      def run() {
        LWJGLSettings.useStoredContextForCurrentThread
        
        while (cond) {
          val currentTime: Long = System.nanoTime()
          val diff = ((currentTime - lastLoopTime) / 1e9).toFloat
          lastLoopTime = currentTime

          val frameEvent = FrameEvent(diff)

          onLoop(frameEvent)

          flushExecutionList
        }

        close
      }
    })
    
    renderingThread.start()
  }
}