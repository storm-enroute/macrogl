package org.macrogl
package ex



import org.macrogl.Matrix._



object Matrix {
  lazy val defaultModelview = {
    val m = new Modelview
    view(m)(0, 0, -1, 0, 0, 0, 0, 1, 0)
    m
  }

  lazy val defaultProjection = {
    val p = new Projection
    orthoProjection(p)(-1, 1, -1, 1, 1, -1)
    p
  }

  lazy val defaultSpace = {
    val s = new Plain
    multiply(defaultProjection, defaultModelview, s)
    s
  }

  final class Projection(a: Array[Double]) extends Matrix(a) {
    def this() = this(new Array[Double](16))
    def mode = Macroglex.PROJECTION
    def matrixMode = Macroglex.PROJECTION_MATRIX
  }

  implicit val projectionCtor = new Ctor[Projection] {
    def newMatrix(a: Array[Double]) = new Projection(a)
  }

  final class Modelview(a: Array[Double]) extends Matrix(a) {
    def this() = this(new Array[Double](16))
    def mode = Macroglex.MODELVIEW
    def matrixMode = Macroglex.MODELVIEW_MATRIX
  }

  implicit val modelviewCtor = new Ctor[Modelview] {
    def newMatrix(a: Array[Double]) = new Modelview(a)
  }

  final class Texture(a: Array[Double]) extends Matrix(a) {
    def this() = this(new Array[Double](16))
    def mode = Macrogl.TEXTURE
    def matrixMode = Macroglex.TEXTURE_MATRIX
  }

  implicit val textureCtor = new Ctor[Texture] {
    def newMatrix(a: Array[Double]) = new Texture(a)
  }

  def orthoProjection(m: Projection)(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double)(implicit glex: Macroglex) {
    val oldmode = glex.getParameteri(Macroglex.MATRIX_MODE)
    glex.matrixMode(Macroglex.PROJECTION)
    glex.pushMatrix()
    try {
      glex.loadIdentity()
      glex.ortho(left, right, bottom, top, nearPlane, farPlane)
      Results.doubleResult.rewind()
      glex.getParameterdv(Macroglex.PROJECTION_MATRIX, Results.doubleResult)
      Results.doubleResult.get(m.array, 0, 16)
    } finally {
      glex.popMatrix()
      glex.matrixMode(oldmode)
      Macrogl.default.checkError()
    }
  }

  def perspectiveProjection(m: Projection)(left: Double, right: Double, bottom: Double, top: Double, nearPlane: Double, farPlane: Double)(implicit glex: Macroglex) {
    val oldmode = glex.getParameteri(Macroglex.MATRIX_MODE)
    glex.matrixMode(Macroglex.PROJECTION)
    glex.pushMatrix()
    try {
      glex.loadIdentity()
      glex.frustum(left, right, bottom, top, nearPlane, farPlane)
      Results.doubleResult.rewind()
      glex.getParameterdv(Macroglex.PROJECTION_MATRIX, Results.doubleResult)
      Results.doubleResult.get(m.array, 0, 16)
    } finally {
      glex.popMatrix()
      glex.matrixMode(oldmode)
      Macrogl.default.checkError()
    }
  }

  def view(m: Modelview)(xfrom: Float, yfrom: Float, zfrom: Float, xto: Float, yto: Float, zto: Float, xup: Float, yup: Float, zup: Float)(implicit glex: Macroglex) {
    val oldmode = glex.getParameteri(Macroglex.MATRIX_MODE)
    glex.matrixMode(Macroglex.MODELVIEW)
    glex.pushMatrix()
    try {
      glex.loadIdentity()
      glex.lookAt(xfrom, yfrom, zfrom, xto, yto, zto, xup, yup, zup)
      Results.doubleResult.rewind()
      glex.getParameterdv(Macroglex.MODELVIEW_MATRIX, Results.doubleResult)
      Results.doubleResult.get(m.array, 0, 16)
    } finally {
      glex.popMatrix()
      glex.matrixMode(oldmode)
      Macrogl.default.checkError()
    }
  }
}
