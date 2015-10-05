package org.macrogl.examples

import org.lwjgl.opengl._
import org.macrogl._

object EmptyExample {

  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2)
      .withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    while (!Display.isCloseRequested) {
      Display.update()
    }

    Display.destroy()
  }

}
