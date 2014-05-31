package org.macrogl.math

/**
 * Ported from LWJGL source code
 */
class Matrix3f extends Matrix {
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

  def this(m: Matrix3f) = {
    this()
    Matrix3f.set(m, this)
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

  def load(src: org.macrogl.Data.Float, order: MajorOrder): Matrix3f = order match {
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
  def store(dst: org.macrogl.Data.Float, order: MajorOrder): Matrix3f = order match {
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

  def setIdentity(): Matrix3f = {
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
  def setZero(): Matrix3f = {
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

  def invert(): Matrix3f = {
    Matrix3f.invert(this, this)
    this
  }
  def invertedCopy(): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.invert(this, ret)
    ret
  }

  def negate(): Matrix3f = {
    Matrix3f.negate(this, this)
    this
  }
  def negatedCopy(): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.negate(this, ret)
    ret
  }

  def transpose(): Matrix3f = {
    Matrix3f.transpose(this, this)
    this
  }
  def transposedCopy(): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.transpose(this, ret)
    ret
  }

  def determinant(): Float = {
    m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20)
  }

  def copy(): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.set(this, ret)
    ret
  }

  def +(m: Matrix3f): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.add(this, m, ret)
    ret
  }

  def +=(m: Matrix3f): Matrix3f = {
    Matrix3f.add(this, m, this)
    this
  }

  def -(m: Matrix3f): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.sub(this, m, ret)
    ret
  }

  def -=(m: Matrix3f): Matrix3f = {
    Matrix3f.sub(this, m, this)
    this
  }

  def *(m: Matrix3f): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.mult(this, m, ret)
    ret
  }

  def *=(m: Matrix3f): Matrix3f = {
    Matrix3f.mult(this, m, this)
    this
  }

  def *(v: Float): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.mult(this, v, ret)
    ret
  }

  def *=(v: Float): Matrix3f = {
    Matrix3f.mult(this, v, this)
    this
  }

  def /(v: Float): Matrix3f = {
    val ret = new Matrix3f
    Matrix3f.div(this, v, ret)
    ret
  }

  def /=(v: Float): Matrix3f = {
    Matrix3f.div(this, v, this)
    this
  }

  def *(v: Vector3f): Vector3f = {
    val ret = new Vector3f
    Matrix3f.mult(this, v, ret)
    ret
  }

  override def toString: String = {
    var sb = ""
    sb += m00 + " " + m10 + " " + m20 + "\n"
    sb += m01 + " " + m11 + " " + m21 + "\n"
    sb += m02 + " " + m12 + " " + m22 + "\n"
    sb
  }
  
  override def equals(obj: Any): Boolean = {
    if(obj == null) false
    if(!obj.isInstanceOf[Matrix3f]) false
    
    val o = obj.asInstanceOf[Matrix3f]
    
    m00 == o.m00 &&
    m01 == o.m01 &&
    m02 == o.m02 &&
    m10 == o.m10 &&
    m11 == o.m11 &&
    m12 == o.m12 &&
    m20 == o.m20 &&
    m21 == o.m21 &&
    m22 == o.m22
  }
  
  override def hashCode(): Int = {
    m00.toInt ^ m01.toInt ^ m02.toInt ^
    m10.toInt ^ m11.toInt ^ m12.toInt ^
    m20.toInt ^ m21.toInt ^ m22.toInt
  }
}

object Matrix3f {
  def set(src: Matrix3f, dst: Matrix3f): Unit = {
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

  def negate(src: Matrix3f, dst: Matrix3f): Unit = {
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

  def invert(src: Matrix3f, dst: Matrix3f): Unit = {
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

  def transpose(src: Matrix3f, dst: Matrix3f): Unit = {
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

  def add(left: Matrix3f, right: Matrix3f, dst: Matrix3f): Unit = {
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

  def sub(left: Matrix3f, right: Matrix3f, dst: Matrix3f): Unit = {
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

  def mult(left: Matrix3f, right: Matrix3f, dst: Matrix3f): Unit = {
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

  def mult(left: Matrix3f, right: Vector3f, dst: Vector3f): Unit = {
    val x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z
    val y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z
    val z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z

    dst.x = x
    dst.y = y
    dst.z = z
  }

  def mult(left: Matrix3f, right: Float, dst: Matrix3f): Unit = {
    dst.m00 = left.m00 * right
    dst.m01 = left.m01 * right
    dst.m02 = left.m02 * right

    dst.m10 = left.m10 * right
    dst.m11 = left.m11 * right
    dst.m12 = left.m12 * right

    dst.m20 = left.m20 * right
    dst.m21 = left.m21 * right
    dst.m22 = left.m22 * right
  }

  def div(left: Matrix3f, right: Float, dst: Matrix3f): Unit = {
    dst.m00 = left.m00 / right
    dst.m01 = left.m01 / right
    dst.m02 = left.m02 / right

    dst.m10 = left.m10 / right
    dst.m11 = left.m11 / right
    dst.m12 = left.m12 / right

    dst.m20 = left.m20 / right
    dst.m21 = left.m21 / right
    dst.m22 = left.m22 / right
  }
  
  /**
   * Generates the homogeneous rotation matrix for a given angle (in degrees) around the origin
   */
  def rotation2D(angle: Float): Matrix3f = {
    val ret = new Matrix3f
    setRotation2D(angle, ret)
    ret
  }

  def setRotation2D(angle: Float, dst: Matrix3f): Unit = {
    val radAngle = Utils.degToRad(angle)

    val c = Math.cos(radAngle).toFloat
    val s = Math.sin(radAngle).toFloat

    dst.m00 = c
    dst.m10 = -s
    dst.m20 = 0f

    dst.m01 = s
    dst.m11 = c
    dst.m21 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = 1f
  }

  /**
   * Generates the homogeneous translation matrix for a given translation vector
   */
  def translate2D(mov: Vector2f): Matrix3f = {
    val ret = new Matrix3f
    setTranslate2D(mov, ret)
    ret
  }

  def setTranslate2D(mov: Vector2f, dst: Matrix3f): Unit = {
    dst.m00 = 1f
    dst.m10 = 0f
    dst.m20 = mov.x

    dst.m01 = 0f
    dst.m11 = 1f
    dst.m21 = mov.y

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = 1f
  }

  /**
   * Generates the homogeneous scaling matrix for a given scale vector around the origin
   */
  def scale2D(scale: Vector2f): Matrix3f = {
    val ret = new Matrix3f
    setScale2D(scale, ret)
    ret
  }

  def setScale2D(scale: Vector2f, dst: Matrix3f): Unit = {
    dst.m00 = scale.x
    dst.m10 = 0f
    dst.m20 = 0f

    dst.m01 = 0f
    dst.m11 = scale.y
    dst.m21 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = 1f
  }
  
  /**
   * Generates the non-homogeneous rotation matrix for a given angle (in degrees) and a given unitary axis around the origin
   */
  def rotation3D(angle: Float, axis: Vector3f): Matrix3f = {
    val ret = new Matrix3f
    setRotation3D(angle, axis, ret)
    ret
  }

  def setRotation3D(angle: Float, axis: Vector3f, dst: Matrix3f): Unit = {
    val radAngle = Utils.degToRad(angle)

    val c = Math.cos(radAngle).toFloat
    val s = Math.sin(radAngle).toFloat

    val x = axis.x
    val y = axis.y
    val z = axis.z

    dst.m00 = x * x * (1 - c) + c
    dst.m10 = x * y * (1 - c) - z * s
    dst.m20 = x * z * (1 - c) + y * s

    dst.m01 = y * x * (1 - c) + z * s
    dst.m11 = y * y * (1 - c) + c
    dst.m21 = y * z * (1 - c) - x * s

    dst.m02 = x * z * (1 - c) - y * s
    dst.m12 = y * z * (1 - c) + x * s
    dst.m22 = z * z * (1 - c) + c
  }

  /**
   * Generates the non-homogeneous scaling matrix for a given scale vector around the origin
   */
  def scale3D(scale: Vector3f): Matrix3f = {
    val ret = new Matrix3f
    setScale3D(scale, ret)
    ret
  }

  def setScale3D(scale: Vector3f, dst: Matrix3f): Unit = {
    dst.m00 = scale.x
    dst.m10 = 0f
    dst.m20 = 0f

    dst.m01 = 0f
    dst.m11 = scale.y
    dst.m21 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = scale.z
  }
}