package org.macrogl

import scala.concurrent._
import ExecutionContext.Implicits.global
import javax.imageio.ImageIO
import java.util.concurrent.ConcurrentLinkedQueue

import org.lwjgl.opengl._

object Utils {
  def WebGLSettings: Nothing = throw new UnsupportedOperationException("Available only when using Scala.js")

  def loadTexture2DFromResources(resourceName: String, texture: Token.Texture, gl: Macrogl, preload: => Boolean = { true }): Unit = {
    val stream = this.getClass().getResourceAsStream(resourceName)
    
    // TODO should we have our own ExecutionContext?

    Future {
      // Should support JPEG, PNG, BMP, WBMP and GIF
      val image = ImageIO.read(stream)

      val height = image.getHeight()
      val width = image.getWidth()

      val byteBuffer = Macrogl.createByteData(4 * width * height) // Stored as RGBA value: 4 bytes per pixel

      val tmp = new Array[Byte](4)
      
      var y = height-1
      while (y >= 0) {

        var x = 0
        while (x < width) {

          val argb = image.getRGB(x, y)
          
          tmp(2) = argb.toByte // blue
          tmp(1) = (argb >> 8).toByte // green
          tmp(0) = (argb >> 16).toByte // red
          tmp(3) = (argb >> 24).toByte // alpha
          
          byteBuffer.put(tmp)

          x += 1
        }

        y -= 1
      }
      
      byteBuffer.rewind

      // Don't load it now, we want it done synchronously in the main loop to avoid concurrency issue
      executionList.add({ () =>
        if (preload) {
          val previousTexture = gl.getParameterTexture(Macrogl.TEXTURE_BINDING_2D)
          gl.bindTexture(Macrogl.TEXTURE_2D, texture)
          gl.texImage2D(Macrogl.TEXTURE_2D, 0, Macrogl.RGBA, width, height, 0, Macrogl.RGBA, Macrogl.UNSIGNED_BYTE, byteBuffer)
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

  def startFrameListener(fl: FrameListener): Unit = {

    val frameListenerThread = new Thread(new Runnable {
      def run() {

        fl.init

        var lastLoopTime: Long = System.nanoTime()
        while (fl.continue) {
          val currentTime: Long = System.nanoTime()
          val diff = ((currentTime - lastLoopTime) / 1e9).toFloat
          lastLoopTime = currentTime

          val frameEvent = FrameEvent(diff)

          fl.render(frameEvent)

          flushExecutionList
        }

        fl.close
      }
    })
    
    // Start listener
    frameListenerThread.start()
  }
}