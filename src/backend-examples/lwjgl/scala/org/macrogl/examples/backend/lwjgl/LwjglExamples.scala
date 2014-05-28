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
    
    macroGLTest()
    
    Display.destroy()
  }
  
  def macroGLTest():Unit = {
    val mgl:Macrogl = Macrogl.default
    
    def myPrint(msg:String) = println(msg)
    def myUpdate:Boolean = {
      Display.update()
      !Display.isCloseRequested()
    }
    
    val basicTriangle = new BasicTriangle(mgl, myPrint, myUpdate)
    
    basicTriangle.start()
  }
}