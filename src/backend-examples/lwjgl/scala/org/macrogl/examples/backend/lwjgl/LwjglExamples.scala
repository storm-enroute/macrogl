package org.macrogl.examples.backend.lwjgl

import org.lwjgl.opengl._
import org.lwjgl.input.{Keyboard, Mouse}
import org.macrogl.Macrogl
import org.macrogl.examples.backend.common.BasicTriangle
import org.lwjgl.input.Keyboard

object LwjglExamples {
  def main(args: Array[String]): Unit = {
    val contextAttributes = new ContextAttribs(2, 1)
    
    Display.setDisplayMode(new DisplayMode(1280, 720))
    Display.create(new PixelFormat, contextAttributes)
    
    val mgl:Macrogl = Macrogl.default
    
    val basicTriangle = new BasicTriangle(mgl, msg => println(msg))
    basicTriangle.draw()
    
    while (!Display.isCloseRequested) {
      Display.update()
      Thread.sleep(50);
    }
    
    Display.destroy()
  }
}