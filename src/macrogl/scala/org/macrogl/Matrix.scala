package org.macrogl


import scala.collection._



abstract class Matrix(val array: Array[Double]) {

  def mode: Int

  def matrixMode: Int

  override def toString = {
    val invbases = array.map(v => f"$v%.5f").grouped(4).toArray.transpose
    "%s(\n%s)".format(
      this.getClass.getSimpleName,
      invbases.map(_.mkString(", ")).mkString("\n")
    )
  }

  final def apply(y: Int, x: Int) = array(x * 4 + y)

}


object Matrix {

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
    val inv8 = m(4)  * m(9) * m(15) - m(4) * m(11) * m(13) - m(8) * m(5) * m(15) + m(8) * m(7) * m(13) + m(12) * m(5) * m(11) - m(12) * m(7) * m(9)
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
    0.0, 0.0, 0.0, 1.0
  ))

}
