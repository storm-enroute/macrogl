package org.macrogl.math

class Vector2f extends Vector {
  var x, y: Float = _

  def this(v1: Float, v2: Float) = {
    this()
    x = v1
    y = v2
  }

  def this(v: Vector2f) = {
    this()
    Vector2f.set(v, this)
  }

  def apply(pos: Int): Float = pos match {
    case 0 => x
    case 1 => y
    case _ => throw new IndexOutOfBoundsException
  }

  def update(pos: Int, v: Float): Unit = pos match {
    case 0 => x = v
    case 1 => y = v
    case _ => throw new IndexOutOfBoundsException
  }

  def load(src: org.macrogl.Data.Float): Vector2f = {
    x = src.get
    y = src.get
    this
  }
  def store(dst: org.macrogl.Data.Float): Vector2f = {
    dst.put(x)
    dst.put(y)
    this
  }

  def normalise(): Vector2f = {
    val l = length
    this /= l
  }

  def normalizedCopy(): Vector2f = {
    val l = length
    this / l
  }

  def negate(): Vector2f = {
    Vector2f.negate(this, this)
    this
  }

  def negatedCopy(): Vector2f = {
    val ret = new Vector2f
    Vector2f.negate(this, ret)
    ret
  }

  def lengthSquared(): Float = {
    x * x + y * y
  }
  def length(): Float = {
    Math.sqrt(this.lengthSquared).toFloat
  }

  def copy(): Vector2f = {
    val ret = new Vector2f
    Vector2f.set(this, ret)
    ret
  }

  def +(v: Vector2f): Vector2f = {
    val ret = new Vector2f
    Vector2f.add(this, v, ret)
    ret
  }

  def -(v: Vector2f): Vector2f = {
    val ret = new Vector2f
    Vector2f.sub(this, v, ret)
    ret
  }

  def *(v: Vector2f): Float = {
    Vector2f.dot(this, v)
  }
  
  def dot(v: Vector2f): Float = {
    Vector2f.dot(this, v)
  }

  def *(v: Float): Vector2f = {
    val ret = new Vector2f
    Vector2f.mult(this, v, ret)
    ret
  }

  def /(v: Float): Vector2f = {
    val ret = new Vector2f
    Vector2f.div(this, v, ret)
    ret
  }

  def +=(v: Vector2f): Vector2f = {
    Vector2f.add(this, v, this)
    this
  }

  def -=(v: Vector2f): Vector2f = {
    Vector2f.sub(this, v, this)
    this
  }

  def *=(v: Float): Vector2f = {
    Vector2f.mult(this, v, this)
    this
  }

  def /=(v: Float): Vector2f = {
    Vector2f.div(this, v, this)
    this
  }

  override def toString = {
    "Vector2ff[" + x + ", " + y + "]"
  }
  
  override def equals(obj: Any): Boolean = {
    if(obj == null) false
    if(!obj.isInstanceOf[Vector2f]) false
    
    val o = obj.asInstanceOf[Vector2f]
    
    x == o.x &&
    y == o.y
  }
  
  override def hashCode(): Int = {
    x.toInt ^
    y.toInt
  }
}

object Vector2f {
  def set(src: Vector2f, dst: Vector2f): Unit = {
    dst.x = src.x
    dst.y = src.y
  }

  def negate(v1: Vector2f, dst: Vector2f): Unit = {
    dst.x = -v1.x
    dst.y = -v1.y
  }

  def add(v1: Vector2f, v2: Vector2f, dst: Vector2f): Unit = {
    dst.x = v1.x + v2.x
    dst.y = v1.y + v2.y
  }

  def sub(v1: Vector2f, v2: Vector2f, dst: Vector2f): Unit = {
    dst.x = v1.x - v2.x
    dst.y = v1.y - v2.y
  }

  def dot(v1: Vector2f, v2: Vector2f): Float = {
    v1.x * v2.x + v1.y * v2.y
  }

  def mult(v1: Vector2f, v: Float, dst: Vector2f): Unit = {
    dst.x = v1.x * v
    dst.y = v1.y * v
  }

  def div(v1: Vector2f, v: Float, dst: Vector2f): Unit = {
    dst.x = v1.x / v
    dst.y = v1.y / v
  }
}