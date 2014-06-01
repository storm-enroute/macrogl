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
    val width = 1280
    val height = 720
    
    def myPrint(msg: String) = println(msg)
    def myUpdate(): Boolean = {
      Display.update()
      !Display.isCloseRequested()
    }
    def myInit(): Macrogl = {
      val contextAttributes = new ContextAttribs(2, 1)
      Display.setDisplayMode(new DisplayMode(width, height))
      Display.create(new PixelFormat, contextAttributes)

      Macrogl.default
    }
    def myClose(): Unit = {
      Display.destroy()
    }
    
    val example: DemoRenderable = exampleName match {
      case "1" => new BasicTriangle(width, height, myPrint, myUpdate, myInit, myClose)
      case "2" => new BasicTexture(width, height, myPrint, myUpdate, myInit, myClose)
      case "3" => new BasicProjection3D(width, height, myPrint, myUpdate, myInit, myClose)
      case "4" => new BasicFractale3D(width, height, myPrint, myUpdate, myInit, myClose)
      case "5" => new BasicRenderToTexture(width, height, myPrint, myUpdate, myInit, myClose)
      case _ => {
        println("\"" + exampleName + "\" is not a valid example")
        return
      }
    }

    example.start
  }

}