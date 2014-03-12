package org.macrogl



import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._



final class FrameBuffer extends Handle {
  private var fbindex = -1
  private val result = new Array[Int](1)

  def index = fbindex

  val binding = new FrameBuffer.Binding

  def acquire() {
    release()
    fbindex = glGenFramebuffers()
  }

  def release() {
    if (fbindex != -1) {
      glDeleteFramebuffers(fbindex)
      fbindex = -1
    }
  }

}


object FrameBuffer {

  class Binding private[FrameBuffer] () {
    object AttachTexture2D {
      def foreach[U](f: Unit => U) = macro FrameBuffer.bindTexture[U]
    }

    object AttachRenderBuffer {
      def foreach[U](f: Unit => U) = macro FrameBuffer.bindRenderBuffer[U]
    }

    def attachTexture2D(attachment: Int, t: Texture, level: Int) = AttachTexture2D
    def attachRenderBuffer(target: Int, attachment: Int, rb: RenderBuffer) = AttachRenderBuffer
  }

  def bindTexture[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(tattachment, ttexture, tlevel)), _), _), _) = c.macroApplication

    val attachment = c.Expr[Int](tattachment)
    val texture = c.Expr[Texture](ttexture)
    val level = c.Expr[Int](tlevel)
    val r = reify {
      val a = attachment.splice
      val t = texture.splice
      val l = level.splice
      glFramebufferTexture2D(GL_FRAMEBUFFER, a, t.target, t.index, l)
      glutils.status.check()
      try f.splice(())
      finally glFramebufferTexture2D(GL_FRAMEBUFFER, a, t.target, 0, l)
      ()
    }

    c.inlineAndReset(r)
  }

  def bindRenderBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(ttarget, tattachment, trbuff)), _), _), _) = c.macroApplication

    val target = c.Expr[Int](ttarget)
    val attachment = c.Expr[Int](tattachment)
    val rbuff = c.Expr[RenderBuffer](trbuff)
    val r = reify {
      val t = target.splice
      val a = attachment.splice
      val rb = rbuff.splice
      glFramebufferRenderbuffer(t, a, GL_RENDERBUFFER, rb.index)
      glutils.status.check()
      try f.splice(())
      finally glFramebufferRenderbuffer(t, a, GL_RENDERBUFFER, 0)
      ()
    }

    c.inlineAndReset(r)
  }

}

