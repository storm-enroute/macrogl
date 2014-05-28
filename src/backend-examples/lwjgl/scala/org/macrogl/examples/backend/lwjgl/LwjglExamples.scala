package org.macrogl.examples.backend.lwjgl

import org.lwjgl.opengl._

import org.macrogl.Macrogl
import org.macrogl.Utils

import org.macrogl.examples.backend.common.BasicTriangle

object LwjglExamples {
  def main(args: Array[String]): Unit = {
    val contextAttributes = new ContextAttribs(2, 1)
    
    Display.setDisplayMode(new DisplayMode(1280, 720))
    Display.create(new PixelFormat, contextAttributes)
    
    var i = 0
    
    Utils.loopUntil{i == 5} {
      println("Log " + i)
      i += 1
    }
    
    macroGLTest()
    
    while (!Display.isCloseRequested) {
      Display.update()
      Thread.sleep(50);
    }
    
    Display.destroy()
  }
  
  def macroGLTest():Unit = {
    val mgl:Macrogl = Macrogl.default
    
    val basicTriangle = new BasicTriangle(mgl, msg => println(msg))
    basicTriangle.render(0)
    
    basicTriangle.close()
  }
}