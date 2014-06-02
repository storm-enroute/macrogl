package org.macrogl.examples.backend.webgl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

import org.macrogl.Macrogl
import org.macrogl.examples.backend.common._

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
    
    def myPrint(msg: String): Unit = g.console.log(msg)
    def myUpdate(): Boolean = true
    def myClose(): Unit = {
      // Nothing to do
    }
    def customInit(canvasName: String): () => Macrogl = {
      def myInit(): Macrogl = {
        val canvas = g.document.getElementById(canvasName)
        canvas.width = width
        canvas.height = height
        var gl = canvas.getContext("webgl").asInstanceOf[dom.WebGLRenderingContext]
        if(gl == null) gl = canvas.getContext("experimental-webgl").asInstanceOf[dom.WebGLRenderingContext]
        if(gl == null) throw new RuntimeException("WebGL not supported")
        new Macrogl()(gl)
      }
      
      myInit _
    }

    new BasicTriangle(width, height, myPrint, myUpdate, customInit("canvas-triangle"), myClose).start()
    new BasicTexture(width, height, myPrint, myUpdate, customInit("canvas-texture"), myClose).start()
    new BasicProjection3D(width, height, myPrint, myUpdate, customInit("canvas-projection"), myClose).start()
    new BasicFractale3D(width, height, myPrint, myUpdate, customInit("canvas-fractale"), myClose).start()
    new BasicRenderToTexture(width, height, myPrint, myUpdate, customInit("canvas-renderToTexture"), myClose).start()
  }
}