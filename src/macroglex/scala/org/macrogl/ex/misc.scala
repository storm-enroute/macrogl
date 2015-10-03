package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context



package ex {

  object raster {
    def drawbuffers(b0: Int, b1: Int, b2: Int)(implicit gl: Macroglex) {
      val ib = Results.intResult
      ib.clear()
      ib.put(0, b0)
      ib.put(1, b1)
      ib.put(2, b2)
      ib.rewind()
      ib.limit(3)
      gl.drawBuffers(ib)
    }
    def drawbuffer(b: Int)(implicit gl: Macroglex) {
      val ib = Results.intResult
      ib.clear()
      ib.put(0, b)
      ib.rewind()
      ib.limit(1)
      gl.drawBuffers(ib)
    }
    def readbuffer(b: Int)(implicit gl: Macroglex) {
      gl.readBuffer(b)
    }
    def draw(mode: Int)(body: => Unit)(implicit gl: Macroglex) {
      gl.begin(mode)
      body
      gl.end()
    }
  }

  object setting {
    object Color {
      def foreach[U](f: Unit => U)(implicit gl: Macroglex): Unit =
        macro Macros.setColor[U]
    }

    def color(r: Float, g: Float, b: Float, a: Float) = Color
  }

  object using {
    object TransformationMatrix {
      def foreach[U](f: Unit => U)(implicit gl: Macroglex): Unit =
        macro Macros.useMatrix[U]
    }
    object IndexBufferObject {
      def foreach[U](f: IndexBuffer.Access => U)(implicit gl: Macroglex): Unit =
        macro ex.IndexBuffer.using[U]
    }

    def matrix(m: Matrix) = TransformationMatrix
    def indexbuffer(mesh: IndexBuffer) = IndexBufferObject
  }

  object Macros {

    import scala.language.implicitConversions

    implicit def c2utils(c: Context) = new org.macrogl.Macros.Util[c.type](c)

    def useMatrix[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macroglex]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(mt)), _), _), _), _) = c.macroApplication

      val r = reify {
        val m = (c.Expr[Matrix](mt)).splice
        val oldmode = gl.splice.getParameteri(Macroglex.MATRIX_MODE)
        val dr = Results.doubleResult
        gl.splice.matrixMode(m.mode)
        gl.splice.pushMatrix()
        dr.rewind()
        dr.put(m.array, 0, 16)
        dr.rewind()
        gl.splice.loadMatrix(dr)
        try f.splice(())
        finally {
          gl.splice.matrixMode(m.mode)
          gl.splice.popMatrix()
          gl.splice.matrixMode(oldmode)
        }
        ()
      }

      c.inlineAndReset(r)
    }

    def setColor[U: c.WeakTypeTag](c: Context)(f: c.Expr[Unit => U])(gl: c.Expr[Macroglex]): c.Expr[Unit] = {
      import c.universe._

      val Apply(Apply(TypeApply(Select(Apply(_, List(rt, gt, bt, at)), _), _), _), _) = c.macroApplication

      val r = reify {
        val r = (c.Expr[Float](rt)).splice
        val g = (c.Expr[Float](gt)).splice
        val b = (c.Expr[Float](bt)).splice
        val a = (c.Expr[Float](at)).splice
        Results.floatResult.rewind()
        gl.splice.getParameterfv(Macroglex.CURRENT_COLOR, Results.floatResult)
        val or = Results.floatResult.get(0)
        val og = Results.floatResult.get(1)
        val ob = Results.floatResult.get(2)
        val oa = Results.floatResult.get(3)
        gl.splice.color4f(r, g, b, a)
        try f.splice(())
        finally gl.splice.color4f(or, og, ob, oa)
        ()
      }

      c.inlineAndReset(r)
    }
  }
}
