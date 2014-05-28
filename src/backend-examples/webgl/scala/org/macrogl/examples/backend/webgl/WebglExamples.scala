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
    val paragraph = g.document.createElement("p")

    macroGLTest()

    paragraph.innerHTML = "<strong>End of macroGL test reached.</strong>"
    g.document.getElementById("playground").appendChild(paragraph)
  }

  def macroGLTest() {
    val canvas = g.document.getElementById("playground-canvas")
    val gl = canvas.getContext("webgl").asInstanceOf[dom.WebGLRenderingContext]
    val mgl: Macrogl = new Macrogl()(gl)

    val basicTriangle = new BasicTriangle(mgl, msg => g.console.log(msg))
    
    basicTriangle.draw()
    
  }
}