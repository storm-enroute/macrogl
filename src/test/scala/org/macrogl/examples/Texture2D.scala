package org.macrogl.examples

import org.lwjgl.opengl._
import org.lwjgl.input.Keyboard
import org.lwjgl.BufferUtils
import org.macrogl._

object Texture2D {
  var texCoord = 1.0f
  var wrapS = GL11.GL_REPEAT

  var magFilter = GL11.GL_NEAREST
  var minFilter = GL11.GL_NEAREST
  
  def main(args: Array[String]) {
    val contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)

    Display.setDisplayMode(new DisplayMode(800, 600))
    Display.create(new PixelFormat, contextAttributes)

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vertices = Array(
      -0.5f,  0.5f, 0.0f,  0.0f,     0.0f,
      -0.5f, -0.5f, 0.0f,  0.0f,     texCoord,
       0.5f,  0.5f, 0.0f,  texCoord, 0.0f
    )

    val fb = BufferUtils.createFloatBuffer(vertices.length)
    fb.put(vertices)
    fb.flip()

    val mb = new AttributeBuffer(GL15.GL_DYNAMIC_DRAW, 3, 5)
    mb.acquire()
    mb.send(0, fb)

    val textureSize = 16

    val ab = new scala.collection.mutable.ArrayBuilder.ofByte
    for {
      i <- 0 until textureSize
      j <- 0 until textureSize
    } {
      val half = textureSize / 2 
      val black = (i < half && j < half) || (i >= half && j >= half)
      ab ++= { if (black) Seq(0, 0, 0) else Seq(0xFF.toByte, 0xFF.toByte, 0xFF.toByte) }
    }
    val textureData = ab.result

    val textureBuffer = BufferUtils.createByteBuffer(textureData.length)
    textureBuffer.put(textureData)
    textureBuffer.flip()

    val texture = Texture(GL11.GL_TEXTURE_2D) { tex =>
      for (_ <- using.texture(GL13.GL_TEXTURE0, tex)) {
        GL11.glTexImage2D(tex.target, 0, GL11.GL_RGB, textureSize, textureSize, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffer)

        tex.wrapS = wrapS
        tex.magFilter = magFilter
        tex.minFilter = minFilter
      }
    }

    texture.acquire()
    
    def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

    val pp = new Program("test")(
      Program.Shader.Vertex  (readResource("/org/macrogl/examples/Texture2D.vert")),
      Program.Shader.Fragment(readResource("/org/macrogl/examples/Texture2D.frag"))
    )

    pp.acquire()

    val attr = Array((0, 3), (3, 2))

    while (!Display.isCloseRequested) {
      val stateChanged = processInput()
      if (stateChanged) {
        vertices(9)  = texCoord
        vertices(13) = texCoord
        fb.put(vertices)
        fb.flip()
        mb.send(0, fb)

        texture.wrapS = wrapS
        texture.minFilter = minFilter
        texture.magFilter = magFilter
      }

      for {
        _   <- using.program(pp)
        _   <- using.texture(GL13.GL_TEXTURE0, texture)
        acc <- using.attributebuffer(mb)
      } {
        GL11.glClearColor(0.0f, 0.64f, 0.91f, 1.0f)
        raster.clear(GL11.GL_COLOR_BUFFER_BIT)

        pp.uniform.testTexture = 0

        acc.render(GL11.GL_TRIANGLES, attr)
      }

      Display.update()
    }

    pp.release()
    texture.release()
    mb.release()
    Display.destroy()
  }
  def processInput(): Boolean = {
    var stateChanged = false

    while (Keyboard.next()) {
      if (!Keyboard.getEventKeyState()) {
        val ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)

        stateChanged ||= {
          Keyboard.getEventKey() match {
            case Keyboard.KEY_1 if ctrlPressed => wrapS = GL12.GL_CLAMP_TO_EDGE;   true
            case Keyboard.KEY_2 if ctrlPressed => wrapS = GL13.GL_CLAMP_TO_BORDER; true
            case Keyboard.KEY_3 if ctrlPressed => wrapS = GL14.GL_MIRRORED_REPEAT; true
            case Keyboard.KEY_4 if ctrlPressed => wrapS = GL11.GL_REPEAT;          true

            case Keyboard.KEY_A => texCoord += 0.1f; true
            case Keyboard.KEY_S => texCoord -= 0.1f; true

            case Keyboard.KEY_Z => minFilter = GL11.GL_LINEAR;  true
            case Keyboard.KEY_X => minFilter = GL11.GL_NEAREST; true

            case Keyboard.KEY_C => magFilter = GL11.GL_LINEAR;  true
            case Keyboard.KEY_V => magFilter = GL11.GL_NEAREST; true

            case _ => false
          }
        }
      }
    }
    stateChanged
  }
}
