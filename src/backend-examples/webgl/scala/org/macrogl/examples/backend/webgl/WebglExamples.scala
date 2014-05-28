package org.macrogl.examples.backend.webgl

import scala.scalajs.js
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.dom

@JSExport
object ScalaJSExample {
  @JSExport
  def main(): Unit = {
    val paragraph = g.document.createElement("p")
    paragraph.innerHTML = "<strong>It works!</strong>"
    g.document.getElementById("playground").appendChild(paragraph)
  }

  /** Computes the square of an integer.
* This demonstrates unit testing.
*/
  def square(x: Int): Int = x*x
}
