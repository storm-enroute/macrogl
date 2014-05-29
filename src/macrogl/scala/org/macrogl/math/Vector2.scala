package org.macrogl.math

class Vector2 extends Vector {
  var x, y: Float = _

  def this(v1: Float, v2: Float) = {
    this()
    x = v1
    y = v2
  }

  def this(v: Vector2) = {
    this()
    Vector2.set(v, this)
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

  def load(src: org.macrogl.Data.Float): Vector2 = {
    x = src.get
    y = src.get
    this
  }
  def store(dst: org.macrogl.Data.Float): Vector2 = {
    dst.put(x)
    dst.put(y)
    this
  }

  def normalise(): Vector2 = {
    val l = length
    this /= l
  }

  def normalizedCopy(): Vector2 = {
    val l = length
    this / l
  }

  def negate(): Vector2 = {
    Vector2.negate(this, this)
    this
  }

  def negatedCopy(): Vector2 = {
    val ret = new Vector2
    Vector2.negate(this, ret)
    ret
  }

  def lengthSquared(): Float = {
    x * x + y * y
  }
  def length(): Float = {
    Math.sqrt(this.lengthSquared).toFloat
  }

  def copy(): Vector2 = {
    val ret = new Vector2
    Vector2.set(this, ret)
    ret
  }

  def +(v: Vector2): Vector2 = {
    val ret = new Vector2
    Vector2.add(this, v, ret)
    ret
  }

  def -(v: Vector2): Vector2 = {
    val ret = new Vector2
    Vector2.sub(this, v, ret)
    ret
  }

  def *(v: Vector2): Float = {
    Vector2.dot(this, v)
  }

  def *(v: Float): Vector2 = {
    val ret = new Vector2
    Vector2.mult(this, v, ret)
    ret
  }

  def /(v: Float): Vector2 = {
    val ret = new Vector2
    Vector2.div(this, v, ret)
    ret
  }

  def +=(v: Vector2): Vector2 = {
    Vector2.add(this, v, this)
    this
  }

  def -=(v: Vector2): Vector2 = {
    Vector2.sub(this, v, this)
    this
  }

  def *=(v: Float): Vector2 = {
    Vector2.mult(this, v, this)
    this
  }

  def /=(v: Float): Vector2 = {
    Vector2.div(this, v, this)
    this
  }

  override def toString = {
    "Vector2f[" + x + ", " + y + "]"
  }
}

object Vector2 {
  def set(src: Vector2, dst: Vector2): Unit = {
    dst.x = src.x
    dst.y = src.y
  }

  def negate(v1: Vector2, dst: Vector2): Unit = {
    dst.x = -v1.x
    dst.y = -v1.y
  }

  def add(v1: Vector2, v2: Vector2, dst: Vector2): Unit = {
    dst.x = v1.x + v2.x
    dst.y = v1.y + v2.y
  }

  def sub(v1: Vector2, v2: Vector2, dst: Vector2): Unit = {
    dst.x = v1.x - v2.x
    dst.y = v1.y - v2.y
  }

  def dot(v1: Vector2, v2: Vector2): Float = {
    v1.x * v2.x + v1.y * v2.y
  }

  def mult(v1: Vector2, v: Float, dst: Vector2): Unit = {
    dst.x = v1.x * v
    dst.y = v1.y * v
  }

  def div(v1: Vector2, v: Float, dst: Vector2): Unit = {
    dst.x = v1.x / v
    dst.y = v1.y / v
  }
}