package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



final class FrameBuffer(implicit gl: Macrogl) extends Handle {
  private var fbtoken = Token.FrameBuffer.invalid
  private val result = new Array[Int](1)

  def token = fbtoken

  val binding = new FrameBuffer.Binding

  def acquire() {
    release()
    fbtoken = gl.createFramebuffer
  }

  def release() {
    if (!gl.validFramebuffer(fbtoken)) {
      gl.deleteFramebuffer(fbtoken)
      fbtoken = Token.FrameBuffer.invalid
    }
  }

}


object FrameBuffer {

  import Macros._

  class Binding private[FrameBuffer] () {
    object AttachTexture2D {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro FrameBuffer.bindTexture[U]
    }

    object AttachRenderBuffer {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro FrameBuffer.bindRenderBuffer[U]
    }

    def attachTexture2D(attachment: Int, t: Texture, level: Int) = AttachTexture2D
    def attachRenderBuffer(target: Int, attachment: Int, rb: RenderBuffer) = AttachRenderBuffer
  }

  def bindTexture[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(tattachment, ttexture, tlevel)), _), _), _), _) = c.macroApplication

    val attachment = c.Expr[Int](tattachment)
    val texture = c.Expr[Texture](ttexture)
    val level = c.Expr[Int](tlevel)
    val r = reify {
      val a = attachment.splice
      val t = texture.splice
      val l = level.splice
      gl.splice.framebufferTexture2D(Macrogl.FRAMEBUFFER, a, t.target, t.token, l)
      gl.splice.checkError()
      try f.splice(())
      finally gl.splice.framebufferTexture2D(Macrogl.FRAMEBUFFER, a, t.target, Token.Texture.none, l)
      ()
    }

    c.inlineAndReset(r)
  }

  def bindRenderBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(ttarget, tattachment, trbuff)), _), _), _), _) = c.macroApplication

    val target = c.Expr[Int](ttarget)
    val attachment = c.Expr[Int](tattachment)
    val rbuff = c.Expr[RenderBuffer](trbuff)
    val r = reify {
      val t = target.splice
      val a = attachment.splice
      val rb = rbuff.splice
      gl.splice.framebufferRenderbuffer(t, a, Macrogl.RENDERBUFFER, rb.token)
      gl.splice.checkError()
      try f.splice(())
      finally gl.splice.framebufferRenderbuffer(t, a, Macrogl.RENDERBUFFER, Token.RenderBuffer.none)
      ()
    }

    c.inlineAndReset(r)
  }

}
