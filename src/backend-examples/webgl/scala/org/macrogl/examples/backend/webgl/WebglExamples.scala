package org.macrogl.examples.backend.webgl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

import org.macrogl.Macrogl
import org.macrogl.Utils

import org.macrogl.examples.backend.common.BasicTriangle

@JSExport
object WebglExamples {
  @JSExport
  def main(): Unit = {
    macroGLTest()
  }

  def macroGLTest(): Unit = {

    def myPrint(msg: String): Unit = g.console.log(msg)
    def myUpdate: Boolean = true
    def myInit: Macrogl = {
      val canvas = g.document.getElementById("playground-canvas")
      val gl = canvas.getContext("webgl").asInstanceOf[dom.WebGLRenderingContext]
      new Macrogl()(gl)
    }
    def myClose: Unit = {
      // Nothing to do
    }

    val basicTriangle = new BasicTriangle(myPrint, myUpdate, myInit, myClose)

    basicTriangle.start
  }
}