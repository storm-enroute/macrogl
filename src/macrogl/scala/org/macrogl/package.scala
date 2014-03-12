package org



import language.experimental.macros
import scala.reflect.macros.Context
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.util.glu.GLU



package object macrogl {
  
  class Vec3(var x: Float, var y: Float, var z: Float) {
    override def toString = s"Vec3($x, $y, $z)"
  }

  /* operations */

  def timeFor(thunk: Any) = macro timeForThunk

  def timed(f: Double => Any)(thunk: Any) = macro timedThunk
  
  object raster {
    def clear(bits: Int) {
      glClear(bits)
    }
    def drawbuffers(b0: Int, b1: Int, b2: Int) {
      val ib = Results.intResult
      ib.clear()
      ib.put(0, b0)
      ib.put(1, b1)
      ib.put(2, b2)
      ib.rewind()
      ib.limit(3)
      glDrawBuffers(ib)
    }
    def drawbuffer(b: Int) {
      val ib = Results.intResult
      ib.clear()
      ib.put(0, b)
      ib.rewind()
      ib.limit(1)
      glDrawBuffers(ib)
    }
    def readbuffer(b: Int) {
      glReadBuffer(b)
    }
    def draw(mode: Int)(body: =>Unit) {
      glBegin(GL_QUADS)
      body
      glEnd()
    }
  }

  object enabling {
    object Enable {
      def foreach[U](f: Unit => U): Unit = macro enableSettings[U]
    }

    def apply(settings: Int*) = Enable
  }

  object disabling {
    object Disable {
      def foreach[U](f: Unit => U): Unit = macro disableSettings[U]
    }

    def apply(settings: Int*) = Disable
  }

  object setting {
    object Color {
      def foreach[U](f: Unit => U): Unit = macro setColor[U]
    }
    object CullFace {
      def foreach[U](f: Unit => U): Unit = macro setCullFace[U]
    }
    object Viewport {
      def foreach[U](f: Unit => U): Unit = macro setViewport[U]
    }
    object BlendFunc {
      def foreach[U](f: Unit => U): Unit = macro setBlendFunc[U]
    }

    def color(r: Float, g: Float, b: Float, a: Float) = Color
    def cullFace(v: Int) = CullFace
    def viewport(x: Int, y: Int, wdt: Int, hgt: Int) = Viewport
    def blendFunc(sfactor: Int, dfactor: Int) = BlendFunc
  }

  object using {
    object ShaderProgram {
      def foreach[U](f: Unit => U)(implicit gl: Macrogl): Unit = macro useProgram[U]
    }
    object TransformationMatrix {
      def foreach[U](f: Unit => U): Unit = macro useMatrix[U]
    }
    object TextureObject {
      def foreach[U](f: Unit => U): Unit = macro useTexture[U]
    }
    object RenderBufferObject {
      def foreach[U](f: Unit => U): Unit = macro useRenderBuffer[U]
    }
    object FrameBufferObject {
      def foreach[U](f: FrameBuffer.Binding => U)(implicit gl: Macrogl): Unit = macro useFrameBuffer[U]
    }
    object AttributeBufferObject {
      def foreach[U](f: AttributeBuffer.Access => U)(implicit gl: Macrogl): Unit = macro AttributeBuffer.using[U]
    }
    object MeshBufferObject {
      def foreach[U](f: MeshBuffer.Access => U)(implicit gl: Macrogl): Unit = macro MeshBuffer.using[U]
    }

    def program(p: Program) = ShaderProgram
    def matrix(m: Matrix) = TransformationMatrix
    def texture(texnum: Int, t: Texture) = TextureObject
    def renderbuffer(rb: RenderBuffer) = RenderBufferObject
    def framebuffer(fb: FrameBuffer) = FrameBufferObject
    def attributebuffer(mesh: AttributeBuffer) = AttributeBufferObject
    def meshbuffer(mesh: MeshBuffer) = MeshBufferObject
  }

  /* macros */

  implicit def c2utils(c: Context) = new Util[c.type](c)

  def useFrameBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[FrameBuffer.Binding => U])(gl: c.Expr[Macrogl]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(fbt)), _), _), _), _) = c.macroApplication

    val r = reify {
      val fb = (c.Expr[FrameBuffer](fbt)).splice
      val oldbinding = gl.splice.getInteger(Macrogl.GL_FRAMEBUFFER_BINDING)
      gl.splice.bindFrameBuffer(Macrogl.GL_FRAMEBUFFER, fb.token)
      try f.splice(fb.binding)
      finally gl.splice.bindFrameBuffer(Macrogl.GL_FRAMEBUFFER, oldbinding)
      ()
    }

    c.inlineAndReset(r)
  }

  def useRenderBuffer[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(rbt)), _), _), _) = c.macroApplication

    val r = reify {
      val rb = (c.Expr[RenderBuffer](rbt)).splice
      val oldbinding = GL11.glGetInteger(GL_RENDERBUFFER_BINDING)
      glBindRenderbuffer(GL_RENDERBUFFER, rb.index)
      try f.splice(())
      finally glBindRenderbuffer(GL_RENDERBUFFER, oldbinding)
      ()
    }

    c.inlineAndReset(r)
  }

  def useTexture[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(texnum, tt)), _), _), _) = c.macroApplication

    val r = reify {
      val t = (c.Expr[Texture](tt)).splice
      val oldbinding = GL11.glGetInteger(t.binding)
      glActiveTexture((c.Expr[Int](texnum)).splice)
      glBindTexture(t.target, t.index)
      try f.splice(())
      finally glBindTexture(t.target, oldbinding)
      ()
    }

    c.inlineAndReset(r)
  }

  def useMatrix[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(mt)), _), _), _) = c.macroApplication

    val r = reify {
      val m = (c.Expr[Matrix](mt)).splice
      val oldmode = GL11.glGetInteger(GL_MATRIX_MODE)
      val dr = Results.doubleResult
      glMatrixMode(m.mode)
      glPushMatrix()
      dr.rewind()
      dr.put(m.array, 0, 16)
      dr.rewind()
      glLoadMatrix(dr)
      try f.splice(())
      finally {
        glMatrixMode(m.mode)
        glPopMatrix()
        glMatrixMode(oldmode)
      }
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

  def setColor[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(rt, gt, bt, at)), _), _), _) = c.macroApplication

    val r = reify {
      val r = (c.Expr[Float](rt)).splice
      val g = (c.Expr[Float](gt)).splice
      val b = (c.Expr[Float](bt)).splice
      val a = (c.Expr[Float](at)).splice
      Results.floatResult.rewind()
      glGetFloat(GL_CURRENT_COLOR, Results.floatResult)
      val or = Results.floatResult.get(0)
      val og = Results.floatResult.get(1)
      val ob = Results.floatResult.get(2)
      val oa = Results.floatResult.get(3)
      glColor4f(r, g, b, a)
      try f.splice(())
      finally glColor4f(or, og, ob, oa)
      ()
    }

    c.inlineAndReset(r)
  }

  def setCullFace[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(vt)), _), _), _), _) = c.macroApplication

    val r = reify {
      val v = (c.Expr[Int](vt)).splice
      val ov = GL11.glGetInteger(GL_CULL_FACE_MODE)
      glCullFace(v)
      try f.splice(())
      finally glCullFace(ov)
      ()
    }

    c.inlineAndReset(r)
  }

  def setViewport[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, List(rt, gt, bt, at)), _), _), _) = c.macroApplication

    val r = reify {
      val x = (c.Expr[Int](rt)).splice
      val y = (c.Expr[Int](gt)).splice
      val w = (c.Expr[Int](bt)).splice
      val h = (c.Expr[Int](at)).splice
      Results.intResult.clear()
      GL11.glGetInteger(GL_VIEWPORT, Results.intResult)
      val ox = Results.intResult.get(0)
      val oy = Results.intResult.get(1)
      val ow = Results.intResult.get(2)
      val oh = Results.intResult.get(3)
      glViewport(x, y, w, h)
      try f.splice(())
      finally glViewport(ox, oy, ow, oh)
      ()
    }

    c.inlineAndReset(r)
  }

  def setBlendFunc[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(Apply(TypeApply(Select(Apply(_, List(sfactor, dfactor)), _), _), _), _) = c.macroApplication

    val r = reify {
      val osrc = GL11.glGetInteger(GL_BLEND_SRC)
      val odst = GL11.glGetInteger(GL_BLEND_DST)
      glBlendFunc((c.Expr[Int](sfactor)).splice, c.Expr[Int](dfactor).splice)
      try f.splice(())
      finally glBlendFunc(osrc, odst)
      ()
    }

    c.inlineAndReset(r)
  }

  def enableSettings[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, settings), _), _), _) = c.macroApplication

    val stats = for {
      (s, idx) <- settings.zipWithIndex
      sx = c.Expr[Int](s)
      localName = newTermName("s$" + idx)
      valexpr = reify {
        if (!glIsEnabled(sx.splice)) { glEnable(sx.splice); true } else false
      }
      disexpr = reify {
        glDisable(sx.splice)
      }
    } yield (
      ValDef(Modifiers(), localName, TypeTree(), valexpr.tree),
      If(Ident(localName), disexpr.tree, EmptyTree)
    )
    val (enableStats, resetStats) = stats.unzip

    val r = c.Expr[Unit](
      Block(
        enableStats,
        Try((reify { f.splice(()) }).tree, Nil, Block(resetStats, (reify { () }).tree))
      )
    )

    c.inlineAndReset(r)
  }

  def disableSettings[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U]): c.Expr[Unit] = {
    import c.universe._

    val Apply(TypeApply(Select(Apply(_, settings), _), _), _) = c.macroApplication

    val stats = for {
      (s, idx) <- settings.zipWithIndex
      sx = c.Expr[Int](s)
      localName = newTermName("s$" + idx)
      valexpr = reify {
        if (glIsEnabled(sx.splice)) { glDisable(sx.splice); true } else false
      }
      disexpr = reify {
        glEnable(sx.splice)
      }
    } yield (
      ValDef(Modifiers(), localName, TypeTree(), valexpr.tree),
      If(Ident(localName), disexpr.tree, EmptyTree)
    )
    val (enableStats, resetStats) = stats.unzip

    val r = c.Expr[Unit](
      Block(
        enableStats,
        Try((reify { f.splice(()) }).tree, Nil, Block(resetStats, (reify { () }).tree))
      )
    )

    c.inlineAndReset(r)
  }

  private[macrogl] class Util[C <: Context](val c: C) {
    import c.universe._

    def inlineAndReset[T](expr: c.Expr[T]): c.Expr[T] =
      c.Expr[T](c resetAllAttrs inlineApplyRecursive(expr.tree))

    def inlineApplyRecursive(tree: Tree): Tree = {
      val ApplyName = newTermName("apply")

      object inliner extends Transformer {
        override def transform(tree: Tree): Tree = {
          tree match {
            case ap @ Apply(Select(prefix, ApplyName), args) =>
              prefix match {
                case Function(params, body)  =>
                  if (params.length != args.length)
                    c.abort(c.enclosingPosition, "incorrect arity: " + (params.length, args.length))
                  // val a$0 = args(0); val b$0 = args(1); ...
                  val paramVals = params.zip(args).map {
                    case (ValDef(_, pName, _, _), a) =>
                      ValDef(Modifiers(), newTermName("" + pName + "$0"), TypeTree(), a)
                  }
                  // val a = a$0; val b = b$0
                  val paramVals2 = params.zip(args).map {
                    case (ValDef(_, pName, _, _), a) =>
                      ValDef(Modifiers(), pName, TypeTree(), Ident(newTermName("" + pName + "$0")))
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