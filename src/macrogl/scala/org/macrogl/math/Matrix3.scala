package org.macrogl.math

class Matrix3 extends Matrix {
  private var m00, m11, m22: Float = 1
  private var m01, m02, m10, m12, m20, m21: Float = 0

  def this(a00: Float, a01: Float, a02: Float, a10: Float, a11: Float, a12: Float, a20: Float, a21: Float, a22: Float) = {
    this()
    // Internally stored as Column-major
    m00 = a00
    m10 = a01
    m20 = a02

    m01 = a10
    m11 = a11
    m21 = a12

    m02 = a20
    m12 = a21
    m22 = a22
  }

  def this(m: Matrix3) = {
    this()
    Matrix3.set(m, this)
  }

  def apply(row: Int, col: Int): Float = (row, col) match {
    case (0, 0) => m00
    case (0, 1) => m10
    case (0, 2) => m20

    case (1, 0) => m01
    case (1, 1) => m11
    case (1, 2) => m21

    case (2, 0) => m02
    case (2, 1) => m12
    case (2, 2) => m22
    case _ => throw new IndexOutOfBoundsException
  }

  def update(row: Int, col: Int, v: Float): Unit = (row, col) match {
    case (0, 0) => m00 = v
    case (0, 1) => m10 = v
    case (0, 2) => m20 = v

    case (1, 0) => m01 = v
    case (1, 1) => m11 = v
    case (1, 2) => m21 = v

    case (2, 0) => m02 = v
    case (2, 1) => m12 = v
    case (2, 2) => m22 = v
    case _ => throw new IndexOutOfBoundsException
  }

  def load(src: org.macrogl.Data.Float, order: MajorOrder): Matrix3 = order match {
    case RowMajor =>
      m00 = src.get()
      m10 = src.get()
      m20 = src.get()

      m01 = src.get()
      m11 = src.get()
      m21 = src.get()

      m02 = src.get()
      m12 = src.get()
      m22 = src.get()
      this
    case ColumnMajor =>
      m00 = src.get()
      m01 = src.get()
      m02 = src.get()

      m10 = src.get()
      m11 = src.get()
      m12 = src.get()

      m20 = src.get()
      m21 = src.get()
      m22 = src.get()
      this
  }
  def store(dst: org.macrogl.Data.Float, order: MajorOrder): Matrix3 = order match {
    case RowMajor =>
      dst.put(m00)
      dst.put(m10)
      dst.put(m20)

      dst.put(m01)
      dst.put(m11)
      dst.put(m21)

      dst.put(m02)
      dst.put(m12)
      dst.put(m22)
      this
    case ColumnMajor =>
      dst.put(m00)
      dst.put(m01)
      dst.put(m02)

      dst.put(m10)
      dst.put(m11)
      dst.put(m12)

      dst.put(m20)
      dst.put(m21)
      dst.put(m22)
      this
  }

  def setIdentity(): Matrix3 = {
    m00 = 1
    m11 = 1
    m22 = 1

    m01 = 0
    m02 = 0

    m10 = 0
    m12 = 0

    m20 = 0
    m21 = 0

    this
  }
  def setZero(): Matrix3 = {
    m00 = 0
    m01 = 0
    m02 = 0

    m10 = 0
    m11 = 0
    m12 = 0

    m20 = 0
    m21 = 0
    m22 = 0
    this
  }

  def invert(): Matrix3 = {
    Matrix3.invert(this, this)
    this
  }
  def invertedCopy(): Matrix3 = {
    val ret = new Matrix3
    Matrix3.invert(this, ret)
    ret
  }

  def negate(): Matrix3 = {
    Matrix3.negate(this, this)
    this
  }
  def negatedCopy(): Matrix3 = {
    val ret = new Matrix3
    Matrix3.negate(this, ret)
    ret
  }

  def transpose(): Matrix3 = {
    Matrix3.transpose(this, this)
    this
  }
  def transposedCopy(): Matrix3 = {
    val ret = new Matrix3
    Matrix3.transpose(this, ret)
    ret
  }

  def determinant(): Float = {
    m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20)
  }

  def copy(): Matrix3 = {
    val ret = new Matrix3
    Matrix3.set(this, ret)
    ret
  }

  def +(m: Matrix3): Matrix3 = {
    val ret = new Matrix3
    Matrix3.add(this, m, ret)
    ret
  }

  def +=(m: Matrix3): Matrix3 = {
    Matrix3.add(this, m, this)
    this
  }

  def -(m: Matrix3): Matrix3 = {
    val ret = new Matrix3
    Matrix3.sub(this, m, ret)
    ret
  }

  def -=(m: Matrix3): Matrix3 = {
    Matrix3.sub(this, m, this)
    this
  }

  def *(m: Matrix3): Matrix3 = {
    val ret = new Matrix3
    Matrix3.mult(this, m, ret)
    ret
  }

  def *=(m: Matrix3): Matrix3 = {
    Matrix3.mult(this, m, this)
    this
  }

  def *(v: Vector3): Vector3 = {
    val ret = new Vector3
    Matrix3.mult(this, v, ret)
    ret
  }

  override def toString: String = {
    var sb = ""
    sb += m00 + " " + m10 + " " + m20 + "\n"
    sb += m01 + " " + m11 + " " + m21 + "\n"
    sb += m02 + " " + m12 + " " + m22 + "\n"
    sb
  }
}

object Matrix3 {
  def set(src: Matrix3, dst: Matrix3): Unit = {
    dst.m00 = src.m00
    dst.m01 = src.m01
    dst.m02 = src.m02

    dst.m10 = src.m10
    dst.m11 = src.m11
    dst.m12 = src.m12

    dst.m20 = src.m20
    dst.m21 = src.m21
    dst.m22 = src.m22
  }

  def negate(src: Matrix3, dst: Matrix3): Unit = {
    dst.m00 = -src.m00
    dst.m01 = -src.m01
    dst.m02 = -src.m02

    dst.m10 = -src.m10
    dst.m11 = -src.m11
    dst.m12 = -src.m12

    dst.m20 = -src.m20
    dst.m21 = -src.m21
    dst.m22 = -src.m22
  }

  def invert(src: Matrix3, dst: Matrix3): Unit = {
    val determinant = src.determinant

    if (determinant != 0) {
      val determinant_inv = 1f / determinant

      val t00 = src.m11 * src.m22 - src.m12 * src.m21
      val t01 = -src.m10 * src.m22 + src.m12 * src.m20
      val t02 = src.m10 * src.m21 - src.m11 * src.m20
      val t10 = -src.m01 * src.m22 + src.m02 * src.m21
      val t11 = src.m00 * src.m22 - src.m02 * src.m20
      val t12 = -src.m00 * src.m21 + src.m01 * src.m20
      val t20 = src.m01 * src.m12 - src.m02 * src.m11
      val t21 = -src.m00 * src.m12 + src.m02 * src.m10
      val t22 = src.m00 * src.m11 - src.m01 * src.m10

      dst.m00 = t00 * determinant_inv
      dst.m11 = t11 * determinant_inv
      dst.m22 = t22 * determinant_inv
      dst.m01 = t10 * determinant_inv
      dst.m10 = t01 * determinant_inv
      dst.m20 = t02 * determinant_inv
      dst.m02 = t20 * determinant_inv
      dst.m12 = t21 * determinant_inv
      dst.m21 = t12 * determinant_inv
    }
  }

  def transpose(src: Matrix3, dst: Matrix3): Unit = {
    val m00 = src.m00
    val m01 = src.m10
    val m02 = src.m20
    val m10 = src.m01
    val m11 = src.m11
    val m12 = src.m21
    val m20 = src.m02
    val m21 = src.m12
    val m22 = src.m22

    dst.m00 = m00
    dst.m01 = m01
    dst.m02 = m02
    dst.m10 = m10
    dst.m11 = m11
    dst.m12 = m12
    dst.m20 = m20
    dst.m21 = m21
    dst.m22 = m22
  }

  def add(left: Matrix3, right: Matrix3, dst: Matrix3): Unit = {
    dst.m00 = left.m00 + right.m00
    dst.m01 = left.m01 + right.m01
    dst.m02 = left.m02 + right.m02
    dst.m10 = left.m10 + right.m10
    dst.m11 = left.m11 + right.m11
    dst.m12 = left.m12 + right.m12
    dst.m20 = left.m20 + right.m20
    dst.m21 = left.m21 + right.m21
    dst.m22 = left.m22 + right.m22
  }

  def sub(left: Matrix3, right: Matrix3, dst: Matrix3): Unit = {
    dst.m00 = left.m00 - right.m00
    dst.m01 = left.m01 - right.m01
    dst.m02 = left.m02 - right.m02
    dst.m10 = left.m10 - right.m10
    dst.m11 = left.m11 - right.m11
    dst.m12 = left.m12 - right.m12
    dst.m20 = left.m20 - right.m20
    dst.m21 = left.m21 - right.m21
    dst.m22 = left.m22 - right.m22
  }

  def mult(left: Matrix3, right: Matrix3, dst: Matrix3): Unit = {
    val m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02
    val m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02
    val m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02
    val m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12
    val m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12
    val m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12
    val m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22
    val m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22
    val m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22

    dst.m00 = m00
    dst.m01 = m01
    dst.m02 = m02
    dst.m10 = m10
    dst.m11 = m11
    dst.m12 = m12
    dst.m20 = m20
    dst.m21 = m21
    dst.m22 = m22
  }

  def mult(left: Matrix3, right: Vector3, dst: Vector3): Unit = {
    val x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z
    val y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z
    val z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z

    dst.x = x
    dst.y = y
    dst.z = z
  }
}