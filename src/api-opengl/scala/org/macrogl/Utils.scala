package org.macrogl

import scala.concurrent._
import ExecutionContext.Implicits.global
import javax.imageio.ImageIO
import java.util.concurrent.ConcurrentLinkedQueue
import org.lwjgl.opengl._
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.ByteArrayOutputStream

object Utils {
  /**
   * Specifics when using the LWJGL back end with the JVM.
   * Not accessible when not using the JVM.
   */
  object LWJGLSpecifics {
    private val pendingTaskList = new ConcurrentLinkedQueue[() => Unit]

    /**
     * Flush all the pending tasks.
     * You don't need to explicitly use this method if you use the FrameListener loop system.
     * Warning: this should be called only from the OpenGL thread!
     */
    def flushPendingTaskList(): Unit = {
      var current: () => Unit = null
      while ({ current = pendingTaskList.poll(); current } != null) {
        current()
      }
    }

    /**
     * Add a task to be executed by the OpenGL thread.
     * Tasks are usually executed at the beginning of the next iteration of the FrameListener loop system.
     */
    def addPendingTask(task: () => Unit): Unit = {
      pendingTaskList.add(task)
    }
  }

  /**
   * Specifics when using the WebGL back end with Scala.js.
   * Not accessible when not using Scala.js.
   */
  def WebGLSpecifics: Nothing = throw new UnsupportedOperationException("Available only when using the Scala.js platform")

  private def inputStreamForResource(resourceName: String): InputStream = {
    this.getClass().getResourceAsStream(resourceName)
  }

  /**
   * Load a image from the resources into an OpenGL 2D texture.
   * The textures are stored using the 32 bits per pixel RGBA format.
   * Supported images format: PNG, JPEG, BMP and GIF.
   *
   * @param resourceName The Fully qualified path of the resource image
   * @param texture The token of the texture where the decoded texture have to be loaded
   * @param gl The Macrogl instance to use to load the texture into OpenGL
   * @param preload Optional function called by the OpenGL thread after the image has been decoded but before it is loaded into OpenGL.
   * A returned value false means aborting the texture loading
   */
  def loadTexture2DFromResources(resourceName: String, texture: Token.Texture, preload: => Boolean = true)(implicit gl: Macrogl): Unit = {
    val stream = inputStreamForResource(resourceName)

    // TODO should we have our own ExecutionContext?

    Future {
      // Should support JPEG, PNG, BMP, WBMP and GIF
      val image = ImageIO.read(stream)

      val height = image.getHeight()
      val width = image.getWidth()

      val byteBuffer = Macrogl.createByteData(4 * width * height) // Stored as RGBA value: 4 bytes per pixel

      val tmp = new Array[Byte](4)

      var y = height - 1
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

      stream.close()
      byteBuffer.rewind

      // Don't load it now, we want it done synchronously in the main loop to avoid concurrency issue
      LWJGLSpecifics.addPendingTask({ () =>
        if (preload) {
          val previousTexture = gl.getParameterTexture(Macrogl.TEXTURE_BINDING_2D)
          gl.bindTexture(Macrogl.TEXTURE_2D, texture)
          gl.texImage2D(Macrogl.TEXTURE_2D, 0, Macrogl.RGBA, width, height, 0, Macrogl.RGBA, Macrogl.UNSIGNED_BYTE, byteBuffer)
          gl.bindTexture(Macrogl.TEXTURE_2D, previousTexture)
        }
      })
    }
  }

  /**
   * Asynchronously load a text file from the resources and pass it to the provided callback.
   *
   * @param resourceName The Fully qualified path of the resource
   * @param callback The function to call once the data are in memory
   */
  def getTextFileFromResources(resourceName: String)(callback: Array[String] => Unit): Unit = {
    val stream = inputStreamForResource(resourceName)

    Future {
      val streamReader = new InputStreamReader(stream)
      val reader = new BufferedReader(streamReader)

      var list: List[String] = Nil
      var line: String = null

      while ({ line = reader.readLine(); line } != null) {
        list = line :: list
      }

      reader.close()
      streamReader.close()
      stream.close()

      val lines = list.toArray

      LWJGLSpecifics.addPendingTask({ () =>
        callback(lines)
      })
    }
  }

  /**
   * Asynchronously load a binary file from the resources and pass it to the provided callback.
   *
   * @param resourceName The Fully qualified path of the resource
   * @param callback The function to call once the data are in memory
   */
  def getBinaryFileFromResources(resourceName: String)(callback: org.macrogl.Data.Byte => Unit): Unit = {
    val stream = inputStreamForResource(resourceName)

    Future {
      val byteStream = new ByteArrayOutputStream()

      val tmpData: Array[Byte] = new Array[Byte](1024) // 1KB of temp data
      var tmpDataContentSize: Int = 0

      while ({ tmpDataContentSize = stream.read(tmpData); tmpDataContentSize } >= 0) {
        byteStream.write(tmpData, 0, tmpDataContentSize)
      }

      stream.close()

      val byteArray = byteStream.toByteArray()
      val byteBuffer = org.macrogl.Macrogl.createByteData(byteArray.length)

      byteBuffer.put(byteArray)
      byteBuffer.rewind()

      LWJGLSpecifics.addPendingTask({ () =>
        callback(byteBuffer)
      })
    }
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

    val frameListenerThread = new Thread(new Runnable {
      def run() {
        var lastLoopTime: Long = System.nanoTime()

        fl.init

        while (fl.continue) {
          LWJGLSpecifics.flushPendingTaskList()

          val currentTime: Long = System.nanoTime()
          val diff = ((currentTime - lastLoopTime) / 1e9).toFloat
          lastLoopTime = currentTime

          val frameEvent = FrameEvent(diff)

          fl.render(frameEvent)
        }

        fl.close
      }
    })

    // Start listener
    frameListenerThread.start()
  }
}