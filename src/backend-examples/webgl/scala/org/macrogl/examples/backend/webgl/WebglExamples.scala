package org.macrogl.examples.backend.webgl

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom

import org.macrogl.Macrogl
import org.macrogl.examples.backend.common._

/**
 * Framework for the examples using the WebGL back end
 * Demos should be visible at the page backend-examples-webgl/index.html
 */
@JSExport
object WebglExamples {
  @JSExport
  def main(): Unit = {
    org.macrogl.Utils.WebGLSpecifics.setResourcePath("./target/scala-2.11/classes")
    macroGLTest()
  }

  def macroGLTest(): Unit = {
    val width = 640
    val height = 360

    def myPrint(msg: String): Unit = js.Dynamic.global.console.log(msg)
    def myUpdate(): Boolean = true
    def myClose(): Unit = {
      // Nothing to do
    }
    def customInit(canvasName: String): () => Macrogl = {
      def myInit(): Macrogl = {
        val canvas = js.Dynamic.global.document.getElementById(canvasName)
        canvas.width = width
        canvas.height = height
        var gl = canvas.getContext("webgl")
        if (gl == null) gl = canvas.getContext("experimental-webgl")
        if (gl == null) throw new RuntimeException("WebGL not supported")
        new Macrogl()(gl.asInstanceOf[dom.WebGLRenderingContext])
      }

      myInit _
    }

    new BasicTriangle(width, height, myPrint, myUpdate, customInit("canvas-triangle"),
      myClose).start()
  }

  @JSExport
  def mainBanner(bannerId: String): Unit = {
    val width = 1400
    val height = 600
    val init = () => {
      val canvas = js.Dynamic.global.document.getElementById(bannerId)
      var gl = canvas.getContext("webgl")
      if (gl == null) gl = canvas.getContext("experimental-webgl")
      if (gl == null) throw new RuntimeException("WebGL not supported")
      new Macrogl()(gl.asInstanceOf[dom.WebGLRenderingContext])
    }
    new MainBanner(width, height, init).start()
  }
}
