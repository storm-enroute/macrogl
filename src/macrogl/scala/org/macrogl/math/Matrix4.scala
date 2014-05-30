package org.macrogl.math

class Matrix4 extends Matrix {
  private var m00, m11, m22, m33: Float = 1
  private var m01, m02, m03, m10, m12, m13, m20, m21, m23, m30, m31, m32: Float = 0

  def this(a00: Float, a01: Float, a02: Float, a03: Float, a10: Float, a11: Float, a12: Float, a13: Float, a20: Float, a21: Float, a22: Float, a23: Float, a30: Float, a31: Float, a32: Float, a33: Float) = {
    this()
    // Internally stored as Column-major
    m00 = a00
    m10 = a01
    m20 = a02
    m30 = a03

    m01 = a10
    m11 = a11
    m21 = a12
    m31 = a13

    m02 = a20
    m12 = a21
    m22 = a22
    m32 = a23

    m03 = a30
    m13 = a31
    m23 = a32
    m33 = a33
  }

  def this(m: Matrix4) = {
    this()
    Matrix4.set(m, this)
  }

  def apply(row: Int, col: Int): Float = (row, col) match {
    case (0, 0) => m00
    case (0, 1) => m10
    case (0, 2) => m20
    case (0, 3) => m30

    case (1, 0) => m01
    case (1, 1) => m11
    case (1, 2) => m21
    case (1, 3) => m31

    case (2, 0) => m02
    case (2, 1) => m12
    case (2, 2) => m22
    case (2, 3) => m32

    case (3, 0) => m03
    case (3, 1) => m13
    case (3, 2) => m23
    case (3, 3) => m33
    case _ => throw new IndexOutOfBoundsException
  }

  def update(row: Int, col: Int, v: Float): Unit = (row, col) match {
    case (0, 0) => m00 = v
    case (0, 1) => m10 = v
    case (0, 2) => m20 = v
    case (0, 3) => m30 = v

    case (1, 0) => m01 = v
    case (1, 1) => m11 = v
    case (1, 2) => m21 = v
    case (1, 3) => m31 = v

    case (2, 0) => m02 = v
    case (2, 1) => m12 = v
    case (2, 2) => m22 = v
    case (2, 3) => m32 = v

    case (3, 0) => m03 = v
    case (3, 1) => m13 = v
    case (3, 2) => m23 = v
    case (3, 3) => m33 = v
    case _ => throw new IndexOutOfBoundsException
  }

  def load(src: org.macrogl.Data.Float, order: MajorOrder): Matrix4 = order match {
    case RowMajor =>
      m00 = src.get()
      m10 = src.get()
      m20 = src.get()
      m30 = src.get()

      m01 = src.get()
      m11 = src.get()
      m21 = src.get()
      m31 = src.get()

      m02 = src.get()
      m12 = src.get()
      m22 = src.get()
      m32 = src.get()

      m03 = src.get()
      m13 = src.get()
      m23 = src.get()
      m33 = src.get()
      this
    case ColumnMajor =>
      m00 = src.get()
      m01 = src.get()
      m02 = src.get()
      m03 = src.get()

      m10 = src.get()
      m11 = src.get()
      m12 = src.get()
      m13 = src.get()

      m20 = src.get()
      m21 = src.get()
      m22 = src.get()
      m23 = src.get()

      m30 = src.get()
      m31 = src.get()
      m32 = src.get()
      m33 = src.get()
      this
  }
  def store(dst: org.macrogl.Data.Float, order: MajorOrder): Matrix4 = order match {
    case RowMajor =>
      dst.put(m00)
      dst.put(m10)
      dst.put(m20)
      dst.put(m30)

      dst.put(m01)
      dst.put(m11)
      dst.put(m21)
      dst.put(m31)

      dst.put(m02)
      dst.put(m12)
      dst.put(m22)
      dst.put(m32)

      dst.put(m03)
      dst.put(m13)
      dst.put(m23)
      dst.put(m33)
      this
    case ColumnMajor =>
      dst.put(m00)
      dst.put(m01)
      dst.put(m02)
      dst.put(m03)

      dst.put(m10)
      dst.put(m11)
      dst.put(m12)
      dst.put(m13)

      dst.put(m20)
      dst.put(m21)
      dst.put(m22)
      dst.put(m23)

      dst.put(m30)
      dst.put(m31)
      dst.put(m32)
      dst.put(m33)
      this
  }

  def setIdentity(): Matrix4 = {
    m00 = 1
    m11 = 1
    m22 = 1
    m33 = 1

    m01 = 0
    m02 = 0
    m03 = 0

    m10 = 0
    m12 = 0
    m13 = 0

    m20 = 0
    m21 = 0
    m23 = 0

    m30 = 0
    m31 = 0
    m32 = 0

    this
  }
  def setZero(): Matrix4 = {
    m00 = 0
    m01 = 0
    m02 = 0
    m03 = 0

    m10 = 0
    m11 = 0
    m12 = 0
    m13 = 0

    m20 = 0
    m21 = 0
    m22 = 0
    m23 = 0

    m30 = 0
    m31 = 0
    m32 = 0
    m33 = 0
    this
  }

  def invert(): Matrix4 = {
    Matrix4.invert(this, this)
    this
  }
  def invertedCopy(): Matrix4 = {
    val ret = new Matrix4
    Matrix4.invert(this, ret)
    ret
  }

  def negate(): Matrix4 = {
    Matrix4.negate(this, this)
    this
  }
  def negatedCopy(): Matrix4 = {
    val ret = new Matrix4
    Matrix4.negate(this, ret)
    ret
  }

  def transpose(): Matrix4 = {
    Matrix4.transpose(this, this)
    this
  }
  def transposedCopy(): Matrix4 = {
    val ret = new Matrix4
    Matrix4.transpose(this, ret)
    ret
  }

  def determinant(): Float = {
    val f0 = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33)
    val f1 = m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33)
    val f2 = m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33)
    val f3 = m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32)

    (f0 - f1 + f2 - f3)
  }

  def copy(): Matrix4 = {
    val ret = new Matrix4
    Matrix4.set(this, ret)
    ret
  }

  def +(m: Matrix4): Matrix4 = {
    val ret = new Matrix4
    Matrix4.add(this, m, ret)
    ret
  }

  def +=(m: Matrix4): Matrix4 = {
    Matrix4.add(this, m, this)
    this
  }

  def -(m: Matrix4): Matrix4 = {
    val ret = new Matrix4
    Matrix4.sub(this, m, ret)
    ret
  }

  def -=(m: Matrix4): Matrix4 = {
    Matrix4.sub(this, m, this)
    this
  }

  def *(m: Matrix4): Matrix4 = {
    val ret = new Matrix4
    Matrix4.mult(this, m, ret)
    ret
  }

  def *=(m: Matrix4): Matrix4 = {
    Matrix4.mult(this, m, this)
    this
  }

  def *(v: Vector4): Vector4 = {
    val ret = new Vector4
    Matrix4.mult(this, v, ret)
    ret
  }

  override def toString: String = {
    var sb = ""
    sb += m00 + " " + m10 + " " + m20 + " " + m30 + "\n"
    sb += m01 + " " + m11 + " " + m21 + " " + m31 + "\n"
    sb += m02 + " " + m12 + " " + m22 + " " + m32 + "\n"
    sb += m03 + " " + m13 + " " + m23 + " " + m33 + "\n"
    sb
  }
}

object Matrix4 {
  def set(src: Matrix4, dst: Matrix4): Unit = {
    dst.m00 = src.m00
    dst.m01 = src.m01
    dst.m02 = src.m02
    dst.m03 = src.m03

    dst.m10 = src.m10
    dst.m11 = src.m11
    dst.m12 = src.m12
    dst.m13 = src.m13

    dst.m20 = src.m20
    dst.m21 = src.m21
    dst.m22 = src.m22
    dst.m23 = src.m23

    dst.m30 = src.m30
    dst.m31 = src.m31
    dst.m32 = src.m32
    dst.m33 = src.m33
  }

  def negate(src: Matrix4, dst: Matrix4): Unit = {
    dst.m00 = -src.m00
    dst.m01 = -src.m01
    dst.m02 = -src.m02
    dst.m03 = -src.m03

    dst.m10 = -src.m10
    dst.m11 = -src.m11
    dst.m12 = -src.m12
    dst.m13 = -src.m13

    dst.m20 = -src.m20
    dst.m21 = -src.m21
    dst.m22 = -src.m22
    dst.m23 = -src.m23

    dst.m30 = -src.m30
    dst.m31 = -src.m31
    dst.m32 = -src.m32
    dst.m33 = -src.m33
  }

  private def determinant3x3(t00: Float, t01: Float, t02: Float, t10: Float, t11: Float, t12: Float, t20: Float, t21: Float, t22: Float): Float = {
    t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20)
  }

  def invert(src: Matrix4, dst: Matrix4): Unit = {
    val determinant = src.determinant

    if (determinant != 0) {
      val determinant_inv = 1f / determinant

      // first row
      val t00 = determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33)
      val t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33)
      val t02 = determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33)
      val t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32)
      // second row
      val t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33)
      val t11 = determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33)
      val t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33)
      val t13 = determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32)
      // third row
      val t20 = determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33)
      val t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32, src.m33)
      val t22 = determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33)
      val t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31, src.m32)
      // fourth row
      val t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22, src.m23)
      val t31 = determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23)
      val t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21, src.m23)
      val t33 = determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22)

      // transpose and divide by the determinant
      dst.m00 = t00 * determinant_inv
      dst.m11 = t11 * determinant_inv
      dst.m22 = t22 * determinant_inv
      dst.m33 = t33 * determinant_inv
      dst.m01 = t10 * determinant_inv
      dst.m10 = t01 * determinant_inv
      dst.m20 = t02 * determinant_inv
      dst.m02 = t20 * determinant_inv
      dst.m12 = t21 * determinant_inv
      dst.m21 = t12 * determinant_inv
      dst.m03 = t30 * determinant_inv
      dst.m30 = t03 * determinant_inv
      dst.m13 = t31 * determinant_inv
      dst.m31 = t13 * determinant_inv
      dst.m32 = t23 * determinant_inv
      dst.m23 = t32 * determinant_inv
    }
  }

  def transpose(src: Matrix4, dst: Matrix4): Unit = {
    val m00 = src.m00
    val m01 = src.m10
    val m02 = src.m20
    val m03 = src.m30
    val m10 = src.m01
    val m11 = src.m11
    val m12 = src.m21
    val m13 = src.m31
    val m20 = src.m02
    val m21 = src.m12
    val m22 = src.m22
    val m23 = src.m32
    val m30 = src.m03
    val m31 = src.m13
    val m32 = src.m23
    val m33 = src.m33

    dst.m00 = m00
    dst.m01 = m01
    dst.m02 = m02
    dst.m03 = m03
    dst.m10 = m10
    dst.m11 = m11
    dst.m12 = m12
    dst.m13 = m13
    dst.m20 = m20
    dst.m21 = m21
    dst.m22 = m22
    dst.m23 = m23
    dst.m30 = m30
    dst.m31 = m31
    dst.m32 = m32
    dst.m33 = m33
  }

  def add(left: Matrix4, right: Matrix4, dst: Matrix4): Unit = {
    dst.m00 = left.m00 + right.m00
    dst.m01 = left.m01 + right.m01
    dst.m02 = left.m02 + right.m02
    dst.m03 = left.m03 + right.m03
    dst.m10 = left.m10 + right.m10
    dst.m11 = left.m11 + right.m11
    dst.m12 = left.m12 + right.m12
    dst.m13 = left.m13 + right.m13
    dst.m20 = left.m20 + right.m20
    dst.m21 = left.m21 + right.m21
    dst.m22 = left.m22 + right.m22
    dst.m23 = left.m23 + right.m23
    dst.m30 = left.m30 + right.m30
    dst.m31 = left.m31 + right.m31
    dst.m32 = left.m32 + right.m32
    dst.m33 = left.m33 + right.m33
  }

  def sub(left: Matrix4, right: Matrix4, dst: Matrix4): Unit = {
    dst.m00 = left.m00 - right.m00
    dst.m01 = left.m01 - right.m01
    dst.m02 = left.m02 - right.m02
    dst.m03 = left.m03 - right.m03
    dst.m10 = left.m10 - right.m10
    dst.m11 = left.m11 - right.m11
    dst.m12 = left.m12 - right.m12
    dst.m13 = left.m13 - right.m13
    dst.m20 = left.m20 - right.m20
    dst.m21 = left.m21 - right.m21
    dst.m22 = left.m22 - right.m22
    dst.m23 = left.m23 - right.m23
    dst.m30 = left.m30 - right.m30
    dst.m31 = left.m31 - right.m31
    dst.m32 = left.m32 - right.m32
    dst.m33 = left.m33 - right.m33
  }

  def mult(left: Matrix4, right: Matrix4, dst: Matrix4): Unit = {
    val m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03
    val m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03
    val m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03
    val m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03
    val m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13
    val m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13
    val m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13
    val m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13
    val m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23
    val m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23
    val m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23
    val m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23
    val m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33
    val m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33
    val m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33
    val m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33

    dst.m00 = m00
    dst.m01 = m01
    dst.m02 = m02
    dst.m03 = m03
    dst.m10 = m10
    dst.m11 = m11
    dst.m12 = m12
    dst.m13 = m13
    dst.m20 = m20
    dst.m21 = m21
    dst.m22 = m22
    dst.m23 = m23
    dst.m30 = m30
    dst.m31 = m31
    dst.m32 = m32
    dst.m33 = m33
  }

  def mult(left: Matrix4, right: Vector4, dst: Vector4): Unit = {
    val x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
    val y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
    val z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
    val w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;

    dst.x = x;
    dst.y = y;
    dst.z = z;
    dst.w = w;
  }

  /**
   * Generates the homogeneous rotation matrix for a given angle (in degrees) and a given unitary axis around the origin
   * See: https://www.opengl.org/sdk/docs/man2/xhtml/glRotate.xml
   */
  def rotation3D(angle: Float, axis: Vector3): Matrix4 = {
    val ret = new Matrix4
    setRotation3D(angle, axis, ret)
    ret
  }

  def setRotation3D(angle: Float, axis: Vector3, dst: Matrix4): Unit = {
    val radAngle = Utils.degToRad(angle)

    val c = Math.cos(radAngle).toFloat
    val s = Math.sin(radAngle).toFloat

    val x = axis.x
    val y = axis.y
    val z = axis.z

    dst.m00 = x * x * (1 - c) + c
    dst.m10 = x * y * (1 - c) - z * s
    dst.m20 = x * z * (1 - c) + y * s
    dst.m30 = 0f

    dst.m01 = y * x * (1 - c) + z * s
    dst.m11 = y * y * (1 - c) + c
    dst.m21 = y * z * (1 - c) - x * s
    dst.m31 = 0f

    dst.m02 = x * z * (1 - c) - y * s
    dst.m12 = y * z * (1 - c) + x * s
    dst.m22 = z * z * (1 - c) + c
    dst.m32 = 0f

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = 0f
    dst.m33 = 1f
  }

  /**
   * Generates the homogeneous translation matrix for a given translation vector
   * See: http://www.opengl.org/sdk/docs/man2/xhtml/glTranslate.xml
   */
  def translate3D(mov: Vector3): Matrix4 = {
    val ret = new Matrix4
    setTranslate3D(mov, ret)
    ret
  }

  def setTranslate3D(mov: Vector3, dst: Matrix4): Unit = {
    dst.m00 = 1f
    dst.m10 = 0f
    dst.m20 = 0f
    dst.m30 = mov.x

    dst.m01 = 0f
    dst.m11 = 1f
    dst.m21 = 0f
    dst.m31 = mov.y

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = 1f
    dst.m32 = mov.z

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = 0f
    dst.m33 = 1f
  }

  /**
   * Generates the homogeneous scaling matrix for a given scale vector around the origin
   * See: https://www.opengl.org/sdk/docs/man2/xhtml/glScale.xml
   */
  def scale3D(scale: Vector3): Matrix4 = {
    val ret = new Matrix4
    setScale3D(scale, ret)
    ret
  }

  def setScale3D(scale: Vector3, dst: Matrix4): Unit = {
    dst.m00 = scale.x
    dst.m10 = 0f
    dst.m20 = 0f
    dst.m30 = 0f

    dst.m01 = 0f
    dst.m11 = scale.y
    dst.m21 = 0f
    dst.m31 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = scale.z
    dst.m32 = 0f

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = 0f
    dst.m33 = 1f
  }

  /**
   * Generates the homogeneous projection matrix given the details of the perspective frustum
   * See: http://www.opengl.org/sdk/docs/man2/xhtml/glFrustum.xml
   */
  def frustum3D(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 = {
    val ret = new Matrix4
    setFrustum3D(left, right, bottom, top, near, far, ret)
    ret
  }

  def setFrustum3D(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float, dst: Matrix4): Unit = {
    val a = (right + left) / (right - left)
    val b = (top + bottom) / (top - bottom)
    val c = -(far + near) / (far - near)
    val d = -(2 * far * near) / (far - near)

    val e = (2 * near * far) / (right - left)
    val f = (2 * near * far) / (top - bottom)

    dst.m00 = e
    dst.m10 = 0f
    dst.m20 = a
    dst.m30 = 0f

    dst.m01 = 0f
    dst.m11 = f
    dst.m21 = b
    dst.m31 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = c
    dst.m32 = d

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = -1f
    dst.m33 = 0f
  }
  
  /**
   * Generates the homogeneous projection matrix given the basic properties of the perspective frustum (fovy in degrees)
   * See: http://www.opengl.org/sdk/docs/man2/xhtml/gluPerspective.xml
   */
  def perspective3D(fovy: Float, aspect: Float, near: Float, far: Float): Matrix4 = {
    val ret = new Matrix4
    setPerspective3D(fovy, aspect, near, far, ret)
    ret
  }
  
  def setPerspective3D(fovy: Float, aspect: Float, near: Float, far: Float, dst: Matrix4): Unit = {
    val fovyRad = Utils.degToRad(fovy)
    val f = Utils.cotan(fovyRad/2).toFloat
    
    dst.m00 = f / aspect
    dst.m10 = 0f
    dst.m20 = 0f
    dst.m30 = 0f

    dst.m01 = 0f
    dst.m11 = f
    dst.m21 = 0f
    dst.m31 = 0f

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = (far + near) / (near - far)
    dst.m32 = (2 * far * near) / (near - far)

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = -1f
    dst.m33 = 0f
  }
  
  /**
   * Generates the homogeneous projection matrix given the details of the orthographic projection
   * See: http://www.opengl.org/sdk/docs/man2/xhtml/glOrtho.xml
   */
  def ortho3D(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 = {
    val ret = new Matrix4
    setOrtho3D(left, right, bottom, top, near, far, ret)
    ret
  }
  
  def setOrtho3D(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float, dst: Matrix4): Unit = {
    dst.m00 = 2 / (right - left)
    dst.m10 = 0f
    dst.m20 = 0f
    dst.m30 = -(right + left) / (right - left)

    dst.m01 = 0f
    dst.m11 = 2 / (top - bottom)
    dst.m21 = 0f
    dst.m31 = -(top + bottom) / (top - bottom)

    dst.m02 = 0f
    dst.m12 = 0f
    dst.m22 = -2 / (far - near)
    dst.m32 = -(far + near) / (far - near)

    dst.m03 = 0f
    dst.m13 = 0f
    dst.m23 = 0f
    dst.m33 = 1f
  }
}