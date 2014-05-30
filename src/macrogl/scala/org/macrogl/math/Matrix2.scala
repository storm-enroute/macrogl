package org.macrogl.math

class Matrix2 extends Matrix {
  private var m00, m11: Float = 1
  private var m01, m10: Float = 0

  def this(a00: Float, a01: Float, a10: Float, a11: Float) = {
    this()
    // Internally stored as Column-major
    m00 = a00
    m01 = a10
    m10 = a01
    m11 = a11
  }

  def this(m: Matrix2) = {
    this()
    Matrix2.set(m, this)
  }

  def apply(row: Int, col: Int): Float = (row, col) match {
    case (0, 0) => m00
    case (0, 1) => m10
    case (1, 0) => m01
    case (1, 1) => m11
    case _ => throw new IndexOutOfBoundsException
  }

  def update(row: Int, col: Int, v: Float): Unit = (row, col) match {
    case (0, 0) => m00 = v
    case (0, 1) => m10 = v
    case (1, 0) => m01 = v
    case (1, 1) => m11 = v
    case _ => throw new IndexOutOfBoundsException
  }

  def load(src: org.macrogl.Data.Float, order: MajorOrder): Matrix2 = order match {
    case RowMajor =>
      m00 = src.get()
      m10 = src.get()
      m01 = src.get()
      m11 = src.get()
      this
    case ColumnMajor =>
      m00 = src.get()
      m01 = src.get()
      m10 = src.get()
      m11 = src.get()
      this
  }
  def store(dst: org.macrogl.Data.Float, order: MajorOrder): Matrix2 = order match {
    case RowMajor =>
      dst.put(m00)
      dst.put(m10)
      dst.put(m01)
      dst.put(m11)
      this
    case ColumnMajor =>
      dst.put(m00)
      dst.put(m01)
      dst.put(m10)
      dst.put(m11)
      this
  }

  def setIdentity(): Matrix2 = {
    m00 = 1
    m01 = 0
    m10 = 0
    m11 = 1
    this
  }
  def setZero(): Matrix2 = {
    m00 = 0
    m01 = 0
    m10 = 0
    m11 = 0
    this
  }

  def invert(): Matrix2 = {
    Matrix2.invert(this, this)
    this
  }
  def invertedCopy(): Matrix2 = {
    val ret = new Matrix2
    Matrix2.invert(this, ret)
    ret
  }

  def negate(): Matrix2 = {
    Matrix2.negate(this, this)
    this
  }
  def negatedCopy(): Matrix2 = {
    val ret = new Matrix2
    Matrix2.negate(this, ret)
    ret
  }

  def transpose(): Matrix2 = {
    Matrix2.transpose(this, this)
    this
  }
  def transposedCopy(): Matrix2 = {
    val ret = new Matrix2
    Matrix2.transpose(this, ret)
    ret
  }

  def determinant(): Float = {
    m00 * m11 - m01 * m10
  }

  def copy(): Matrix2 = {
    val ret = new Matrix2
    Matrix2.set(this, ret)
    ret
  }

  def +(m: Matrix2): Matrix2 = {
    val ret = new Matrix2
    Matrix2.add(this, m, ret)
    ret
  }

  def +=(m: Matrix2): Matrix2 = {
    Matrix2.add(this, m, this)
    this
  }

  def -(m: Matrix2): Matrix2 = {
    val ret = new Matrix2
    Matrix2.sub(this, m, ret)
    ret
  }

  def -=(m: Matrix2): Matrix2 = {
    Matrix2.sub(this, m, this)
    this
  }

  def *(m: Matrix2): Matrix2 = {
    val ret = new Matrix2
    Matrix2.mult(this, m, ret)
    ret
  }

  def *=(m: Matrix2): Matrix2 = {
    Matrix2.mult(this, m, this)
    this
  }

  def *(v: Vector2): Vector2 = {
    val ret = new Vector2
    Matrix2.mult(this, v, ret)
    ret
  }

  override def toString: String = {
    var sb = ""
    sb += m00 + " " + m10 + "\n"
    sb += m01 + " " + m11 + "\n"
    sb
  }
}

object Matrix2 {
  def set(src: Matrix2, dst: Matrix2): Unit = {
    dst.m00 = src.m00
    dst.m01 = src.m01
    dst.m10 = src.m10
    dst.m11 = src.m11
  }

  def negate(src: Matrix2, dst: Matrix2): Unit = {
    dst.m00 = -src.m00
    dst.m01 = -src.m01
    dst.m10 = -src.m10
    dst.m11 = -src.m11
  }

  def invert(src: Matrix2, dst: Matrix2): Unit = {
    val det = src.determinant

    if (det != 0) {
      val det_inv = 1f / det

      val t00 = src.m11 * det_inv
      val t01 = -src.m01 * det_inv
      val t11 = src.m00 * det_inv
      val t10 = -src.m10 * det_inv

      dst.m00 = t00
      dst.m01 = t01
      dst.m10 = t10
      dst.m11 = t11
    }
  }

  def transpose(src: Matrix2, dst: Matrix2): Unit = {
    val t10 = src.m10
    val t01 = src.m01

    dst.m00 = src.m00
    dst.m01 = t10
    dst.m10 = t01
    dst.m11 = src.m11
  }

  def add(m1: Matrix2, m2: Matrix2, dst: Matrix2): Unit = {
    dst.m00 = m1.m00 + m2.m00
    dst.m01 = m1.m10 + m2.m10
    dst.m10 = m1.m01 + m2.m01
    dst.m11 = m1.m11 + m2.m11
  }

  def sub(m1: Matrix2, m2: Matrix2, dst: Matrix2): Unit = {
    dst.m00 = m1.m00 - m2.m00
    dst.m01 = m1.m10 - m2.m10
    dst.m10 = m1.m01 - m2.m01
    dst.m11 = m1.m11 - m2.m11
  }

  def mult(left: Matrix2, right: Matrix2, dst: Matrix2): Unit = {
    val m00 = left.m00 * right.m00 + left.m10 * right.m01
    val m01 = left.m01 * right.m00 + left.m11 * right.m01
    val m10 = left.m00 * right.m10 + left.m10 * right.m11
    val m11 = left.m01 * right.m10 + left.m11 * right.m11

    dst.m00 = m00
    dst.m01 = m01
    dst.m10 = m10
    dst.m11 = m11
  }

  def mult(left: Matrix2, right: Vector2, dst: Vector2): Unit = {
    val x = left.m00 * right.x + left.m10 * right.y;
    val y = left.m01 * right.x + left.m11 * right.y;

    dst.x = x
    dst.y = y
  }
}