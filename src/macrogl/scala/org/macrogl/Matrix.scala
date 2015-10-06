package org.macrogl



import scala.collection._



abstract class Matrix(val array: Array[Double]) {

  def mode: Int

  def matrixMode: Int

  override def toString = {
    val invbases = array.map(v => f"$v%.5f").grouped(4).toArray.transpose
    "%s(\n%s)".format(
      this.getClass.getSimpleName,
      invbases.map(_.mkString(", ")).mkString("\n"))
  }

  final def apply(y: Int, x: Int) = array(x * 4 + y)

}


object Matrix {

  final class Plain(a: Array[Double]) extends Matrix(a) {
    def this() = this(new Array[Double](16))
    def mode = ???
    def matrixMode = ???
  }

  implicit val plainCtor = new Ctor[Plain] {
    def newMatrix(a: Array[Double]) = new Plain(a)
  }

  def multiply[M <: Matrix: Matrix.Ctor](thiz: Matrix, that: Matrix): M = {
    val array = new Array[Double](16)
    val result = implicitly[Matrix.Ctor[M]].newMatrix(array)

    multiply(thiz, that, result)

    result
  }

  def multiply[M <: Matrix](thiz: Matrix, that: Matrix, result: M): M = {
    // multiply this and that
    val thizarr = thiz.array
    val thatarr = that.array
    val resarr = result.array
    var y = 0
    while (y < 4) {
      var x = 0
      while (x < 4) {
        var sum = 0.0
        var i = 0
        while (i < 4) {
          sum += thizarr(i * 4 + y) * thatarr(x * 4 + i)
          i += 1
        }
        resarr(x * 4 + y) = sum
        x += 1
      }
      y += 1
    }

    result
  }

  def inverse[M <: Matrix](thiz: Matrix, result: M): Boolean = {
    val inv = result.array
    val m = thiz.array

    val inv0 = m(5) * m(10) * m(15) - m(5) * m(11) * m(14) - m(9) * m(6) * m(15) + m(9) * m(7) * m(14) + m(13) * m(6) * m(11) - m(13) * m(7) * m(10)
    val inv4 = -m(4) * m(10) * m(15) + m(4) * m(11) * m(14) + m(8) * m(6) * m(15) - m(8) * m(7) * m(14) - m(12) * m(6) * m(11) + m(12) * m(7) * m(10)
    val inv8 = m(4) * m(9) * m(15) - m(4) * m(11) * m(13) - m(8) * m(5) * m(15) + m(8) * m(7) * m(13) + m(12) * m(5) * m(11) - m(12) * m(7) * m(9)
    val inv12 = -m(4) * m(9) * m(14) + m(4) * m(10) * m(13) + m(8) * m(5) * m(14) - m(8) * m(6) * m(13) - m(12) * m(5) * m(10) + m(12) * m(6) * m(9)
    val inv1 = -m(1) * m(10) * m(15) + m(1) * m(11) * m(14) + m(9) * m(2) * m(15) - m(9) * m(3) * m(14) - m(13) * m(2) * m(11) + m(13) * m(3) * m(10)
    val inv5 = m(0) * m(10) * m(15) - m(0) * m(11) * m(14) - m(8) * m(2) * m(15) + m(8) * m(3) * m(14) + m(12) * m(2) * m(11) - m(12) * m(3) * m(10)
    val inv9 = -m(0) * m(9) * m(15) + m(0) * m(11) * m(13) + m(8) * m(1) * m(15) - m(8) * m(3) * m(13) - m(12) * m(1) * m(11) + m(12) * m(3) * m(9)
    val inv13 = m(0) * m(9) * m(14) - m(0) * m(10) * m(13) - m(8) * m(1) * m(14) + m(8) * m(2) * m(13) + m(12) * m(1) * m(10) - m(12) * m(2) * m(9)
    val inv2 = m(1) * m(6) * m(15) - m(1) * m(7) * m(14) - m(5) * m(2) * m(15) + m(5) * m(3) * m(14) + m(13) * m(2) * m(7) - m(13) * m(3) * m(6)
    val inv6 = -m(0) * m(6) * m(15) + m(0) * m(7) * m(14) + m(4) * m(2) * m(15) - m(4) * m(3) * m(14) - m(12) * m(2) * m(7) + m(12) * m(3) * m(6)
    val inv10 = m(0) * m(5) * m(15) - m(0) * m(7) * m(13) - m(4) * m(1) * m(15) + m(4) * m(3) * m(13) + m(12) * m(1) * m(7) - m(12) * m(3) * m(5)
    val inv14 = -m(0) * m(5) * m(14) + m(0) * m(6) * m(13) + m(4) * m(1) * m(14) - m(4) * m(2) * m(13) - m(12) * m(1) * m(6) + m(12) * m(2) * m(5)
    val inv3 = -m(1) * m(6) * m(11) + m(1) * m(7) * m(10) + m(5) * m(2) * m(11) - m(5) * m(3) * m(10) - m(9) * m(2) * m(7) + m(9) * m(3) * m(6)
    val inv7 = m(0) * m(6) * m(11) - m(0) * m(7) * m(10) - m(4) * m(2) * m(11) + m(4) * m(3) * m(10) + m(8) * m(2) * m(7) - m(8) * m(3) * m(6)
    val inv11 = -m(0) * m(5) * m(11) + m(0) * m(7) * m(9) + m(4) * m(1) * m(11) - m(4) * m(3) * m(9) - m(8) * m(1) * m(7) + m(8) * m(3) * m(5)
    val inv15 = m(0) * m(5) * m(10) - m(0) * m(6) * m(9) - m(4) * m(1) * m(10) + m(4) * m(2) * m(9) + m(8) * m(1) * m(6) - m(8) * m(2) * m(5)

    var det = m(0) * inv0 + m(1) * inv4 + m(2) * inv8 + m(3) * inv(12)

    if (det == 0) false
    else {
      det = 1.0 / det
      inv(0) = det * inv0
      inv(1) = det * inv1
      inv(2) = det * inv2
      inv(3) = det * inv3
      inv(4) = det * inv4
      inv(5) = det * inv5
      inv(6) = det * inv6
      inv(7) = det * inv7
      inv(8) = det * inv8
      inv(9) = det * inv9
      inv(10) = det * inv10
      inv(11) = det * inv11
      inv(12) = det * inv12
      inv(13) = det * inv13
      inv(14) = det * inv14
      inv(15) = det * inv15
      true
    }
  }

  trait Ctor[M <: Matrix] {
    def newMatrix(array: Array[Double]): M
  }

  def identity[M <: Matrix: Ctor] = implicitly[Ctor[M]].newMatrix(Array[Double](
    1.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0))

  def perspectiveProjection(fovDegrees: Double, aspectRatio: Double, nearZ: Double,
    farZ: Double): Matrix.Plain = {
    val rad = scala.math.toRadians(fovDegrees)

    val y = 1.0 / scala.math.tan(rad / 2.0)
    val x = y / aspectRatio
    val z = -(farZ + nearZ) / (farZ - nearZ)
    val t = -2 * farZ * nearZ / (farZ - nearZ)

    val arr = Array(
      x, 0, 0, 0,
      0, y, 0, 0,
      0, 0, z, -1,
      0, 0, t, 0)

    new Matrix.Plain(arr)
  }

  def orthoProjection(left: Double, right: Double, bottom: Double, top: Double,
    near: Double, far: Double): Matrix.Plain = {
    val x = 2 / (right - left)
    val y = 2 / (top - bottom)
    val z = -2 / (far - near)
    val tx = -(right + left) / (right - left)
    val ty = -(top + bottom) / (top - bottom)
    val tz = -(far + near) / (far - near)

    new Matrix.Plain(
      Array(
        x, 0, 0, 0,
        0, y, 0, 0,
        0, 0, z, 0,
        tx, ty, tz, 1))
  }

  class Camera(x: Double, y: Double, z: Double) {
    val position = Array(x, y, z)
    var horizontalAngle = 0.0
    var verticalAngle = 0.0

    def orientation = {
      import scala.math.{ sin, cos }

      val cv = cos(verticalAngle)
      val sv = sin(verticalAngle)
      val vRotate = Array[Double](
        1, 0, 0, 0,
        0, cv, sv, 0,
        0, -sv, cv, 0,
        0, 0, 0, 1)

      val ch = cos(horizontalAngle)
      val sh = sin(horizontalAngle)
      val hRotate = Array[Double](
        ch, 0, sh, 0,
        0, 1, 0, 0,
        -sh, 0, ch, 0,
        0, 0, 0, 1)

      Matrix.multiply[Matrix.Plain](
        new Matrix.Plain(vRotate), new Matrix.Plain(hRotate))
    }

    def invertedOrientation =
      new Matrix.Plain(orientation.array.grouped(4).toArray.transpose.flatten)

    def forwardDirection = {
      val inv = invertedOrientation
      Array(
        inv(0, 3) - inv(0, 2),
        inv(1, 3) - inv(1, 2),
        inv(2, 3) - inv(2, 2))
    }

    def rightDirection = {
      val inv = invertedOrientation
      Array(
        inv(0, 3) + inv(0, 0),
        inv(1, 3) + inv(1, 0),
        inv(2, 3) + inv(2, 0))
    }

    def moveRight(distance: Double): Unit = {
      val rd = rightDirection
      position(0) += rd(0) * distance
      position(1) += rd(1) * distance
      position(2) += rd(2) * distance
    }
    def moveLeft(distance: Double): Unit = moveRight(-distance)

    def moveForward(distance: Double): Unit = {
      val fd = forwardDirection
      position(0) += fd(0) * distance
      position(1) += fd(1) * distance
      position(2) += fd(2) * distance
    }
    def moveBackward(distance: Double): Unit = moveForward(-distance)

    def transform = {
      val translation = new Matrix.Plain(
        Array[Double](
          1, 0, 0, 0,
          0, 1, 0, 0,
          0, 0, 1, 0,
          -position(0), -position(1), -position(2), 1))
      Matrix.multiply[Matrix.Plain](orientation, translation)
    }

    def setOrientation(h: Double, v: Double): Unit = {
      horizontalAngle = h
      verticalAngle = v
    }

  }

}
