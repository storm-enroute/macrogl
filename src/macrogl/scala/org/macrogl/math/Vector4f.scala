package org.macrogl.math

class Vector4f extends Vector {
  var x, y, z, w: Float = _
  
  def this(v1: Float, v2: Float, v3: Float, v4: Float) = {
    this()
    x = v1
    y = v2
    z = v3
    z = v4
  }
  
  def this(v: Vector4f) = {
    this()
    Vector4f.set(v, this)
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
  
  def load(src: org.macrogl.Data.Float): Vector4f = {
    x = src.get
    y = src.get
    z = src.get
    w = src.get
    this
  }
  def store(dst: org.macrogl.Data.Float): Vector4f = {
    dst.put(x)
    dst.put(y)
    dst.put(z)
    dst.put(w)
    this
  }

  def normalise(): Vector4f = {
    val l = length
    this /= l
  }
  
  def normalizedCopy(): Vector4f = {
    val l = length
    this / l
  }

  def negate(): Vector4f = {
    Vector4f.negate(this, this)
    this
  }
  
  def negatedCopy(): Vector4f = {
    val ret = new Vector4f
    Vector4f.negate(this, ret)
    ret
  }

  def lengthSquared(): Float = {
    x*x + y*y + z*z + w*w
  }
  def length(): Float = {
    Math.sqrt(this.lengthSquared).toFloat
  }
  
  def copy(): Vector4f = {
    val ret = new Vector4f
    Vector4f.set(this, ret)
    ret
  }
  
  def +(v: Vector4f): Vector4f = {
    val ret = new Vector4f
    Vector4f.add(this, v, ret)
    ret
  }
  
  def -(v: Vector4f): Vector4f = {
    val ret = new Vector4f
    Vector4f.sub(this, v, ret)
    ret
  }
  
  def *(v: Vector4f): Float = {
    Vector4f.dot(this, v)
  }
  
  def dot(v: Vector4f): Float = {
    Vector4f.dot(this, v)
  }
  
  def *(v: Float): Vector4f = {
    val ret = new Vector4f
    Vector4f.mult(this, v, ret)
    ret
  }
  
  def /(v: Float): Vector4f = {
    val ret = new Vector4f
    Vector4f.div(this, v, ret)
    ret
  }
  
  def +=(v: Vector4f): Vector4f = {
    Vector4f.add(this, v, this)
    this
  }
  
  def -=(v: Vector4f): Vector4f = {
    Vector4f.sub(this, v, this)
    this
  }
  
  def *=(v: Float): Vector4f = {
    Vector4f.mult(this, v, this)
    this
  }
  
  def /=(v: Float): Vector4f = {
    Vector4f.div(this, v, this)
    this
  }
  
  override def toString = {
    "Vector4f[" + x + ", " + y + ", " + z + ", " + w + "]"
  }
  
  override def equals(obj: Any): Boolean = {
    if(obj == null) false
    if(!obj.isInstanceOf[Vector4f]) false
    
    val o = obj.asInstanceOf[Vector4f]
    
    x == o.x &&
    y == o.y &&
    z == o.z &&
    w == o.w
  }
  
  override def hashCode(): Int = {
    x.toInt ^
    y.toInt ^
    z.toInt ^
    w.toInt
  }
}

object Vector4f {
  def set(src: Vector4f, dst: Vector4f): Unit = {
    dst.x = src.x
    dst.y = src.y
    dst.z = src.z
    dst.w = src.w
  }
  
  def negate(v1: Vector4f, dst: Vector4f): Unit = {
    dst.x = -v1.x
    dst.y = -v1.y
    dst.z = -v1.z
    dst.w = -v1.w
  }
  
  def add(v1: Vector4f, v2: Vector4f, dst: Vector4f): Unit = {
    dst.x = v1.x + v2.x
    dst.y = v1.y + v2.y
    dst.z = v1.z + v2.z
    dst.w = v1.w + v2.w
  }
  
  def sub(v1: Vector4f, v2: Vector4f, dst: Vector4f): Unit = {
    dst.x = v1.x - v2.x
    dst.y = v1.y - v2.y
    dst.z = v1.z - v2.z
    dst.w = v1.w - v2.w
  }
  
  def dot(v1: Vector4f, v2: Vector4f): Float = {
    v1.x*v2.x + v1.y*v2.y + v1.z*v2.z + v1.w*v2.w
  }
  
  def mult(v1: Vector4f, v: Float, dst: Vector4f): Unit = {
    dst.x = v1.x * v
    dst.y = v1.y * v
    dst.z = v1.z * v
    dst.w = v1.w * v
  }
  
  def div(v1: Vector4f, v: Float, dst: Vector4f): Unit = {
    dst.x = v1.x / v
    dst.y = v1.y / v
    dst.z = v1.z / v
    dst.w = v1.w / v
  }
}