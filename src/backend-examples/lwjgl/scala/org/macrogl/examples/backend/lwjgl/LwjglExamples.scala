package org.macrogl.examples.backend.lwjgl

import org.lwjgl.opengl._
import org.macrogl.Macrogl
import org.macrogl.examples.backend.common._

object LwjglExamples {
  def main(args: Array[String]): Unit = {
    macroGLTest()
  }

  def macroGLTest(): Unit = {
    def myPrint(msg: String) = println(msg)
    def myUpdate(): Boolean = {
      Display.update()
      !Display.isCloseRequested()
    }
    def myInit(): Macrogl = {
      val contextAttributes = new ContextAttribs(2, 1)
      Display.setDisplayMode(new DisplayMode(1280, 720))
      Display.create(new PixelFormat, contextAttributes)

      Macrogl.default
    }
    def myClose(): Unit = {
      Display.destroy()
    }

    val example: DemoRenderable = new BasicProjection3D(myPrint, myUpdate, myInit, myClose)

    example.start
  }
}