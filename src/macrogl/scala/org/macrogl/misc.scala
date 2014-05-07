package org

import language.experimental.macros
import scala.reflect.macros.whitebox.Context

package macrogl {

  class Vec3(var x: Float, var y: Float, var z: Float) {
    override def toString = s"Vec3($x, $y, $z)"
  }

  /* operations */

  object Time {

    def apply(thunk: Any): Double = macro Macros.timeForThunk

    def in(f: Double => Any)(thunk: Any): Any = macro Macros.timedThunk

  }

  object raster {
    def clear(bits: Int)(implicit gl: Macrogl) {
      gl.clear(bits)
    }
  }

  object enabling {
    object Enable {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.enableSettings[U]
    }

    def apply(settings: Int*) = Enable
  }

  object disabling {
    object Disable {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.disableSettings[U]
    }

    def apply(settings: Int*) = Disable
  }

  object setting {
    object CullFace {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.setCullFace[U]
    }
    object Viewport {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.setViewport[U]
    }
    object BlendFunc {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.setBlendFunc[U]
    }

    def cullFace(v: Int) = CullFace
    def viewport(x: Int, y: Int, wdt: Int, hgt: Int) = Viewport
    def blendFunc(sfactor: Int, dfactor: Int) = BlendFunc
  }

  object using {
    object ShaderProgram {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.useProgram[U]
    }
    object TextureObject {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.useTexture[U]
    }
    object RenderBufferObject {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro Macros.useRenderBuffer[U]
    }
    object FrameBufferObject {
      def foreach[U](f: FrameBuffer.Binding => U)(implicit gl: Macrogl): Unit = macro Macros.useFrameBuffer[U]
    }
    object AttributeBufferObject {
      def foreach[U](f: AttributeBuffer.Access => U)(implicit gl: Macrogl): Unit = macro AttributeBuffer.using[U]
    }
    object MeshBufferObject {
      def foreach[U](f: MeshBuffer.Access => U)(implicit gl: Macrogl): Unit = macro MeshBuffer.using[U]
    }
    object VertexBuffer {
      def foreach(f: VertexBufferAccess # VertexBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.vertexBuffer
      def apply  (f: VertexBufferAccess # VertexBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.vertexBuffer
    }
    object IndexBuffer {
      def foreach(f: IndexBufferAccess # IndexBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.indexBuffer
      def apply  (f: IndexBufferAccess # IndexBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.indexBuffer
    }
    object TransformFeedbackBuffer {
      def foreach(f: TransformFeedbackBufferAccess # TransformFeedbackBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.transformFeedbackBuffer
      def apply  (f: TransformFeedbackBufferAccess # TransformFeedbackBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.transformFeedbackBuffer
    }
    object TextureBuffer {
      def foreach(f: TextureBufferAccess # TextureBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.textureBuffer
      def apply  (f: TextureBufferAccess # TextureBufferAccessInner => Unit)(implicit gl: Macrogl): Unit = macro Buffer.Macros.textureBuffer
    }

    def program(p: Program) = ShaderProgram
    def texture(texnum: Int, t: Texture) = TextureObject
    def renderbuffer(rb: RenderBuffer) = RenderBufferObject
    def framebuffer(fb: FrameBuffer) = FrameBufferObject
    def attributebuffer(mesh: AttributeBuffer) = AttributeBufferObject
    def meshbuffer(mesh: MeshBuffer) = MeshBufferObject

    def vertexbuffer(buffer: Buffer with VertexBufferAccess) = VertexBuffer
    def indexbuffer (buffer: Buffer with IndexBufferAccess)  = IndexBuffer

    def texturebuffer(texnum: Int, buffer: Buffer with TextureBufferAccess) = TextureBuffer
    def transformfeedbackbuffer(index: Int, buffer: Buffer with TransformFeedbackBufferAccess) = TransformFeedbackBuffer
  }

  /* macros */

  object Macros {
    import scala.language.implicitConversions

    implicit def c2utils(c: Context) = new Util[c.type](c)

    def useFrameBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[FrameBuffer.Binding => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(fbt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val fb = (c.Expr[FrameBuffer](fbt)).splice
        val oldbinding = gl.splice.getParameterFramebuffer(Macrogl.FRAMEBUFFER_BINDING)
        gl.splice.bindFramebuffer(Macrogl.FRAMEBUFFER, fb.token)
        try f.splice(fb.binding)
        finally gl.splice.bindFramebuffer(Macrogl.FRAMEBUFFER, oldbinding)
        ()
      }

      c.inlineAndReset(r)
    }

    def useRenderBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(rbt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val rb = (c.Expr[RenderBuffer](rbt)).splice
        val oldbinding = gl.splice.getParameterRenderbuffer(Macrogl.RENDERBUFFER_BINDING)
        gl.splice.bindRenderbuffer(Macrogl.RENDERBUFFER, rb.token)
        try f.splice(())
        finally gl.splice.bindRenderbuffer(Macrogl.RENDERBUFFER, oldbinding)
        ()
      }

      c.inlineAndReset(r)
    }

    def useTexture[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(texnum, tt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val t = (c.Expr[Texture](tt)).splice
        val oldbinding = gl.splice.getParameterTexture(t.binding)
        gl.splice.activeTexture((c.Expr[Int](texnum)).splice)
        gl.splice.bindTexture(t.target, t.token)
        try f.splice(())
        finally gl.splice.bindTexture(t.target, oldbinding)
        ()
      }

      c.inlineAndReset(r)
    }

    def useProgram[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(pt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val p = (c.Expr[Program](pt)).splice
        val opidx = gl.splice.getCurrentProgram()
        if (gl.splice.differentPrograms(opidx, p.token)) {
          gl.splice.useProgram(p.token)
        }
        try f.splice(())
        finally if (gl.splice.differentPrograms(opidx, p.token)) {
          gl.splice.useProgram(opidx)
        }
        ()
      }

      c.inlineAndReset(r)
    }

    

    def setCullFace[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(vt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val v = (c.Expr[Int](vt)).splice
        val ov = gl.splice.getParameteri(Macrogl.CULL_FACE_MODE)
        gl.splice.cullFace(v)
        try f.splice(())
        finally gl.splice.cullFace(ov)
        ()
      }

      c.inlineAndReset(r)
    }

    def setViewport[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(rt, gt, bt, at)), _), _), _), _) = c.macroApplication

      val r = reify {
        val x = (c.Expr[Int](rt)).splice
        val y = (c.Expr[Int](gt)).splice
        val w = (c.Expr[Int](bt)).splice
        val h = (c.Expr[Int](at)).splice
        Results.intResult.clear()
        gl.splice.getParameteriv(Macrogl.VIEWPORT, Results.intResult)
        val ox = Results.intResult.get(0)
        val oy = Results.intResult.get(1)
        val ow = Results.intResult.get(2)
        val oh = Results.intResult.get(3)
        gl.splice.viewport(x, y, w, h)
        try f.splice(())
        finally gl.splice.viewport(ox, oy, ow, oh)
        ()
      }

      c.inlineAndReset(r)
    }

    def setBlendFunc[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(sfactor, dfactor)), _), _), _), _) = c.macroApplication

      val r = reify {
        //val osrc = gl.splice.getParameteri(Macroglex.BLEND_SRC)
        //val odst = gl.splice.getParameteri(Macroglex.BLEND_DST)
        val osrca = gl.splice.getParameteri(Macrogl.BLEND_SRC_ALPHA)
        val osrcrgb = gl.splice.getParameteri(Macrogl.BLEND_SRC_RGB)
        val odsta = gl.splice.getParameteri(Macrogl.BLEND_DST_ALPHA)
        val odstrgb = gl.splice.getParameteri(Macrogl.BLEND_DST_RGB)
        gl.splice.blendFunc((c.Expr[Int](sfactor)).splice, c.Expr[Int](dfactor).splice)
        try f.splice(())
        finally {
          //gl.splice.blendFunc(osrc, odst)
          gl.splice.blendFuncSeparate(osrcrgb, odstrgb, osrca, odsta)
        }
        ()
      }

      c.inlineAndReset(r)
    }

    def enableSettings[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, settings), _), _), _), _) = c.macroApplication

      val stats = for {
        (s, idx) <- settings.zipWithIndex
        sx = c.Expr[Int](s)
        localName = TermName("s$" + idx)
        valexpr = reify {
          if (!gl.splice.isEnabled(sx.splice)) { gl.splice.enable(sx.splice); true } else false
        }
        disexpr = reify {
          gl.splice.disable(sx.splice)
        }
      } yield (
        ValDef(Modifiers(), localName, TypeTree(), valexpr.tree),
        If(Ident(localName), disexpr.tree, EmptyTree))
      val (enableStats, resetStats) = stats.unzip

      val r = c.Expr[Unit](
        Block(
          enableStats,
          Try((reify { f.splice(()) }).tree, Nil, Block(resetStats, (reify { () }).tree))))

      c.inlineAndReset(r)
    }

    def disableSettings[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, settings), _), _), _), _) = c.macroApplication

      val stats = for {
        (s, idx) <- settings.zipWithIndex
        sx = c.Expr[Int](s)
        localName = TermName("s$" + idx)
        valexpr = reify {
          if (gl.splice.isEnabled(sx.splice)) { gl.splice.disable(sx.splice); true } else false
        }
        disexpr = reify {
          gl.splice.enable(sx.splice)
        }
      } yield (
        ValDef(Modifiers(), localName, TypeTree(), valexpr.tree),
        If(Ident(localName), disexpr.tree, EmptyTree))
      val (enableStats, resetStats) = stats.unzip

      val r = c.Expr[Unit](
        Block(
          enableStats,
          Try((reify { f.splice(()) }).tree, Nil, Block(resetStats, (reify { () }).tree))))

      c.inlineAndReset(r)
    }

    private[macrogl] class Util[C <: Context](val c: C) {
      import c.universe._

      def inlineAndSubstituteArguments(func: c.Tree, params: c.Tree*) = {
        val f = c untypecheck func
        val q"(..$args) => $body" = f
        val decls = for {
          (arg, value) <- args zip params
        } yield {
          val q"$_ val $name: $_" = arg
          q"val $name = $value"
        }
        q"..$decls; $body"
      }

      def inlineAndReset[T](expr: c.Expr[T]): c.Expr[T] =
        c.Expr[T](inlineApplyRecursive(c untypecheck expr.tree))
  
      def inlineApplyRecursive(tree: Tree): Tree = {
        val ApplyName = TermName("apply")
  
        object inliner extends Transformer {
          override def transform(tree: Tree): Tree = {
            tree match {
              case ap @ Apply(Select(prefix, ApplyName), args) =>
                prefix match {
                  case Function(params, body) =>
                    if (params.length != args.length)
                      c.abort(c.enclosingPosition, "incorrect arity: " + (params.length, args.length))
                    // val a$0 = args(0); val b$0 = args(1); ...
                    val paramVals = params.zip(args).map {
                      case (ValDef(_, pName, _, _), a) =>
                        ValDef(Modifiers(), TermName("" + pName + "$0"), TypeTree(), a)
                    }
                    // val a = a$0; val b = b$0
                    val paramVals2 = params.zip(args).map {
                      case (ValDef(_, pName, _, _), a) =>
                        ValDef(Modifiers(), pName, TypeTree(), Ident(TermName("" + pName + "$0")))
                    }
                    // The nested blocks avoid name clashes.
                    Block(paramVals, Block(paramVals2, body))
                  case x => ap
                }
              case _ => super.transform(tree)
            }
          }
        }

        inliner.transform(tree)
      }
    }

    def timeForThunk(c: Context)(thunk: c.Expr[Any]): c.Expr[Double] = {
      import c.universe._

      reify {
        val t1 = System.nanoTime
        thunk.splice
        val time = System.nanoTime - t1
        time / 1000 * 1000 / 1000000.0
      }
    }

    def timedThunk(c: Context)(f: c.Expr[Double => Any])(thunk: c.Expr[Any]): c.Expr[Any] = {
      import c.universe._

      reify {
        val t1 = System.nanoTime
        val res = thunk.splice
        val time = System.nanoTime - t1
        f.splice(time / 1000 * 1000 / 1000000.0)
        res
      }
    }
  }
}
