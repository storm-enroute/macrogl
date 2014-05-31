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
    macroGLTest()
  }

  def macroGLTest(): Unit = {
    org.macrogl.Utils.WebGLSettings.setResourcePath("./target/scala-2.11/classes")

    def myPrint(msg: String): Unit = g.console.log(msg)
    def myUpdate(): Boolean = true
    def myInit(): Macrogl = {
      val canvas = g.document.getElementById("playground-canvas")
      val gl = canvas.getContext("webgl").asInstanceOf[dom.WebGLRenderingContext]
      new Macrogl()(gl)
    }
    def myClose(): Unit = {
      // Nothing to do
    }

    val example: DemoRenderable = new BasicProjection3D(myPrint, myUpdate, myInit, myClose)

    example.start
  }
}