package org.macrogl.math

class Vector4 extends Vector {
  var x, y, z, w: Float = _
  
  def this(v1: Float, v2: Float, v3: Float, v4: Float) = {
    this()
    x = v1
    y = v2
    z = v3
    z = v4
  }
  
  def this(v: Vector4) = {
    this()
    Vector4.set(v, this)
  }
  
  def apply(pos: Int): Float = pos match {
    case 0 => x
    case 1 => y
    case 2 => z
    case 3 => w
    case _ => throw new IndexOutOfBoundsException
  }
  
  def update(pos: Int, v: Float): Unit = pos match {
    case 0 => x = v
    case 1 => y = v
    case 2 => z = v
    case 3 => w = v
    case _ => throw new IndexOutOfBoundsException
  }
  
  def load(src: org.macrogl.Data.Float): Vector4 = {
    x = src.get
    y = src.get
    z = src.get
    w = src.get
    this
  }
  def store(dst: org.macrogl.Data.Float): Vector4 = {
    dst.put(x)
    dst.put(y)
    dst.put(z)
    dst.put(w)
    this
  }

  def normalise(): Vector4 = {
    val l = length
    this /= l
  }
  
  def normalizedCopy(): Vector4 = {
    val l = length
    this / l
  }

  def negate(): Vector4 = {
    Vector4.negate(this, this)
    this
  }
  
  def negatedCopy(): Vector4 = {
    val ret = new Vector4
    Vector4.negate(this, ret)
    ret
  }

  def lengthSquared(): Float = {
    x*x + y*y + z*z + w*w
  }
  def length(): Float = {
    Math.sqrt(this.lengthSquared).toFloat
  }
  
  def copy(): Vector4 = {
    val ret = new Vector4
    Vector4.set(this, ret)
    ret
  }
  
  def +(v: Vector4): Vector4 = {
    val ret = new Vector4
    Vector4.add(this, v, ret)
    ret
  }
  
  def -(v: Vector4): Vector4 = {
    val ret = new Vector4
    Vector4.sub(this, v, ret)
    ret
  }
  
  def *(v: Vector4): Float = {
    Vector4.dot(this, v)
  }
  
  def *(v: Float): Vector4 = {
    val ret = new Vector4
    Vector4.mult(this, v, ret)
    ret
  }
  
  def /(v: Float): Vector4 = {
    val ret = new Vector4
    Vector4.div(this, v, ret)
    ret
  }
  
  def +=(v: Vector4): Vector4 = {
    Vector4.add(this, v, this)
    this
  }
  
  def -=(v: Vector4): Vector4 = {
    Vector4.sub(this, v, this)
    this
  }
  
  def *=(v: Float): Vector4 = {
    Vector4.mult(this, v, this)
    this
  }
  
  def /=(v: Float): Vector4 = {
    Vector4.div(this, v, this)
    this
  }
  
  override def toString = {
    "Vector4f[" + x + ", " + y + ", " + z + ", " + w + "]"
  }
}

object Vector4 {
  def set(src: Vector4, dst: Vector4): Unit = {
    dst.x = src.x
    dst.y = src.y
    dst.z = src.z
    dst.w = src.w
  }
  
  def negate(v1: Vector4, dst: Vector4): Unit = {
    dst.x = -v1.x
    dst.y = -v1.y
    dst.z = -v1.z
    dst.w = -v1.w
  }
  
  def add(v1: Vector4, v2: Vector4, dst: Vector4): Unit = {
    dst.x = v1.x + v2.x
    dst.y = v1.y + v2.y
    dst.z = v1.z + v2.z
    dst.w = v1.w + v2.w
  }
  
  def sub(v1: Vector4, v2: Vector4, dst: Vector4): Unit = {
    dst.x = v1.x - v2.x
    dst.y = v1.y - v2.y
    dst.z = v1.z - v2.z
    dst.w = v1.w - v2.w
  }
  
  def dot(v1: Vector4, v2: Vector4): Float = {
    v1.x*v2.x + v1.y*v2.y + v1.z*v2.z + v1.w*v2.w
  }
  
  def mult(v1: Vector4, v: Float, dst: Vector4): Unit = {
    dst.x = v1.x * v
    dst.y = v1.y * v
    dst.z = v1.z * v
    dst.w = v1.w * v
  }
  
  def div(v1: Vector4, v: Float, dst: Vector4): Unit = {
    dst.x = v1.x / v
    dst.y = v1.y / v
    dst.z = v1.z / v
    dst.w = v1.w / v
  }
}