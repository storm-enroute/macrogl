package org.scalajs.nio

trait TypedBufferBehaviour[ContentType <: AnyVal, BufferType <: TypedBuffer[ContentType, BufferType]] extends BufferBehaviour {
  self: BufferType =>

  // Helper methods
  protected def nextIndex(read: Boolean): Int = {
    nextIndex(read, 1)
  }

  protected def nextIndex(read: Boolean, size: Int): Int = {
    if (this.mPosition + size > this.mLimit) {
      if (read) {
        throw new BufferUnderflowException
      } else {
        throw new BufferOverflowException
      }
    }
    val curPos = this.mPosition
    this.mPosition += size
    curPos
  }

  protected def checkIndex(index: Int): Unit = {
    checkIndex(index, 1)
  }

  protected def checkIndex(index: Int, size: Int): Unit = {
    //if (index + size > this.mLimit) {
    if (index > this.mLimit - size) { // Should reduce mistake due to wrap-around
      throw new ArrayIndexOutOfBoundsException
    }
  }

  // To be implemented by subclasses
  protected def iGet(index: Int): ContentType
  protected def iSet(index: Int, value: ContentType): Unit
  protected def iCmpElements(v1: ContentType, v2: ContentType): Int
  protected def toBufferType(ob: Any): Option[BufferType]
  protected def contentTypeToInt(value: ContentType): Int

  def duplicate(): BufferType
  def slice(): BufferType
  def asReadOnlyBuffer(): BufferType
  def order(): ByteOrder

  // Behaviour implementation
  def compact(): BufferType = {
    // Generic implementation, should override this for cases where it's possible to be optimized
    for (i <- 0 until this.remaining) {
      this.iSet(i, this.iGet(this.position + i))
    }
    this.mPosition = this.remaining
    this.mLimit = this.mCapacity
    this.mMark = -1
    this
  }
  def get(): ContentType = this.iGet(this.nextIndex(true))
  def get(dst: Array[ContentType]): BufferType = this.get(dst, 0, dst.length)
  def get(dst: Array[ContentType], offset: Int, length: Int): BufferType = {
    this.checkIndex(this.mPosition, length)

    if (length > this.remaining)
      throw new BufferUnderflowException

    for (i <- 0 until length) {
      dst(offset + i) = this.iGet(this.mPosition + i)
    }
    this.mPosition = this.mPosition + length
    this
  }
  def get(index: Int): ContentType = {
    this.checkIndex(index)
    this.iGet(index)
  }

  def put(value: ContentType): BufferType = {
    this.iSet(this.nextIndex(false), value)
    this
  }
  // Removed due to the type erasure problem
  //def put(src: Array[ContentType]): BufferType = this.put(src, 0, src.length)
  def put(src: Array[ContentType], offset: Int, length: Int): BufferType = {
    this.checkIndex(this.mPosition, length)

    if (length > this.remaining)
      throw new BufferOverflowException

    for (i <- 0 until length) {
      this.iSet(this.position + i, src(offset + i))
    }
    this
  }
  def put(src: BufferType): BufferType = {
    // Generic implementation, should override this for cases where it's possible to be optimized
    val srcLength = src.remaining
    if (srcLength > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until srcLength) {
      this.iSet(this.mPosition + i, src.get)
    }
    this.position(this.position + srcLength)
    this
  }
  def put(index: Int, value: ContentType): BufferType = {
    this.checkIndex(index)
    this.iSet(index, value)
    this
  }

  def compareTo(that: BufferType): Int = {
    val length = Math.min(this.remaining, that.remaining)

    val i = this.position
    val j = that.position

    for (k <- 0 until length) {
      val curI = this.get(i + k)
      val curJ = that.get(j + k)

      val cmp = iCmpElements(curI, curJ)

      if (cmp != 0)
        return cmp
    }

    this.remaining - that.remaining
  }
  override def equals(ob: Any): Boolean = this.toBufferType(ob) match {
    case Some(that) => {
      if (this.remaining != that.remaining)
        return false
      val length = this.remaining

      val i = this.position
      val j = that.position

      for (k <- 0 until length) {
        val curI = this.get(i + k)
        val curJ = that.get(j + k)

        if (iCmpElements(curI, curJ) != 0)
          return false
      }

      return true
    }
    case None => false
  }
  override def hashCode(): Int = {
    var h: Int = 1
    val length = this.remaining

    for (i <- 0 until length) {
      h = 31 * h + this.contentTypeToInt(this.iGet(this.mPosition + i))
    }

    (h | 0) // just making just the JavaScript Number (Double) doesn't play a trick on us here
  }
}