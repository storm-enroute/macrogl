package org.macrogl.examples.backend.webgl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

import org.macrogl.Macrogl

import org.macrogl.examples.backend.common.BasicTriangle

@JSExport
object WebglExamples {
  @JSExport
  def main(): Unit = {
    macroGLTest()
  }

  def macroGLTest():Unit = {
    val canvas = g.document.getElementById("playground-canvas")
    val gl = canvas.getContext("webgl").asInstanceOf[dom.WebGLRenderingContext]
    val mgl: Macrogl = new Macrogl()(gl)

    val basicTriangle = new BasicTriangle(mgl, msg => g.console.log(msg))
    basicTriangle.draw()
  }
}