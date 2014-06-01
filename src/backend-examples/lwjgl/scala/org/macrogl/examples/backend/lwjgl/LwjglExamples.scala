package org.macrogl.examples.backend.lwjgl

import org.lwjgl.opengl._
import org.macrogl.Macrogl
import org.macrogl.examples.backend.common._
import org.macrogl.examples.backend.common.BasicTriangle

object LwjglExamples {
  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      macroGLTest(args(0))
    } else {
      println("Use one of the following values as parameter:")
      println("\t[1] For basic triangle rendering")
      println("\t[2] For basic texturing")
      println("\t[3] For basic 3D projection and animation")
      println("\t[4] For basic 3D fractale")
      println("\t[5] For basic render-to-texture")
    }
  }

  def macroGLTest(exampleName: String): Unit = {
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

    val example: DemoRenderable = exampleName match {
      case "1" => new BasicTriangle(myPrint, myUpdate, myInit, myClose)
      case "2" => new BasicTexture(myPrint, myUpdate, myInit, myClose)
      case "3" => new BasicProjection3D(myPrint, myUpdate, myInit, myClose)
      case "4" => new BasicFractale3D(myPrint, myUpdate, myInit, myClose)
      case "5" => new BasicRenderToTexture(myPrint, myUpdate, myInit, myClose)
      case _ => {
        println("\"" + exampleName + "\" is not a valid example")
        return
      }
    }

    example.start
  }

}