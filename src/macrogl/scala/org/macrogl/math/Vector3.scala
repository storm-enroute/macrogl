package org.macrogl.math

class Vector3 extends Vector {
  var x, y, z: Float = _
  
  def this(v1: Float, v2: Float, v3: Float) = {
    this()
    x = v1
    y = v2
    z = v3
  }
  
  def this(v: Vector3) = {
    this()
    Vector3.set(v, this)
  }
  
  def apply(pos: Int): Float = pos match {
    case 0 => x
    case 1 => y
    case 2 => z
    case _ => throw new IndexOutOfBoundsException
  }
  
  def update(pos: Int, v: Float): Unit = pos match {
    case 0 => x = v
    case 1 => y = v
    case 2 => z = v
    case _ => throw new IndexOutOfBoundsException
  }
  
  def load(src: org.macrogl.Data.Float): Vector3 = {
    x = src.get
    y = src.get
    z = src.get
    this
  }
  def store(dst: org.macrogl.Data.Float): Vector3 = {
    dst.put(x)
    dst.put(y)
    dst.put(z)
    this
  }

  def normalise(): Vector3 = {
    val l = length
    this /= l
  }
  
  def normalizedCopy(): Vector3 = {
    val l = length
    this / l
  }

  def negate(): Vector3 = {
    Vector3.negate(this, this)
    this
  }
  
  def negatedCopy(): Vector3 = {
    val ret = new Vector3
    Vector3.negate(this, ret)
    ret
  }

  def lengthSquared(): Float = {
    x*x + y*y + z*z
  }
  def length(): Float = {
    Math.sqrt(this.lengthSquared).toFloat
  }
  
  def copy(): Vector3 = {
    val ret = new Vector3
    Vector3.set(this, ret)
    ret
  }
  
  def +(v: Vector3): Vector3 = {
    val ret = new Vector3
    Vector3.add(this, v, ret)
    ret
  }
  
  def -(v: Vector3): Vector3 = {
    val ret = new Vector3
    Vector3.sub(this, v, ret)
    ret
  }
  
  def *(v: Vector3): Float = {
    Vector3.dot(this, v)
  }
  
  def *(v: Float): Vector3 = {
    val ret = new Vector3
    Vector3.mult(this, v, ret)
    ret
  }
  
  def /(v: Float): Vector3 = {
    val ret = new Vector3
    Vector3.div(this, v, ret)
    ret
  }
  
  def +=(v: Vector3): Vector3 = {
    Vector3.add(this, v, this)
    this
  }
  
  def -=(v: Vector3): Vector3 = {
    Vector3.sub(this, v, this)
    this
  }
  
  def *=(v: Float): Vector3 = {
    Vector3.mult(this, v, this)
    this
  }
  
  def /=(v: Float): Vector3 = {
    Vector3.div(this, v, this)
    this
  }
  
  override def toString = {
    "Vector3f[" + x + ", " + y + ", " + z + "]"
  }
}

object Vector3 {
  def set(src: Vector3, dst: Vector3): Unit = {
    dst.x = src.x
    dst.y = src.y
    dst.z = src.z
  }
  
  def negate(v1: Vector3, dst: Vector3): Unit = {
    dst.x = -v1.x
    dst.y = -v1.y
    dst.z = -v1.z
  }
  
  def add(v1: Vector3, v2: Vector3, dst: Vector3): Unit = {
    dst.x = v1.x + v2.x
    dst.y = v1.y + v2.y
    dst.z = v1.z + v2.z
  }
  
  def sub(v1: Vector3, v2: Vector3, dst: Vector3): Unit = {
    dst.x = v1.x - v2.x
    dst.y = v1.y - v2.y
    dst.z = v1.z - v2.z
  }
  
  def dot(v1: Vector3, v2: Vector3): Float = {
    v1.x*v2.x + v1.y*v2.y + v1.z*v2.z
  }
  
  def mult(v1: Vector3, v: Float, dst: Vector3): Unit = {
    dst.x = v1.x * v
    dst.y = v1.y * v
    dst.z = v1.z * v
  }
  
  def div(v1: Vector3, v: Float, dst: Vector3): Unit = {
    dst.x = v1.x / v
    dst.y = v1.y / v
    dst.z = v1.z / v
  }
}