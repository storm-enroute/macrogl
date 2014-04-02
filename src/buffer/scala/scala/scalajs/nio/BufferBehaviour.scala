package scala.scalajs.nio

trait BufferBehaviour {
  self: Buffer =>

  protected var mCapacity: Int
  protected var mLimit: Int
  protected var mPosition: Int
  protected var mMark: Int

  require(mCapacity >= mLimit)
  require(mLimit >= mPosition)
  require(mPosition >= mMark && mPosition >= 0)
  require(mMark >= 0 || mMark == -1)

  // Public methods implementations
  def capacity(): Int = this.mCapacity
  def clear(): Buffer = {
    this.mPosition = 0
    this.mLimit = this.mCapacity
    this.mMark = -1
    this
  }
  def flip(): Buffer = {
    this.mLimit = this.mPosition
    this.mPosition = 0
    this.mMark = -1
    this
  }
  def hasRemaining(): Boolean = this.mPosition < this.mLimit

  def limit(): Int = this.mLimit
  def limit(newLimit: Int): Buffer = {
    require(this.mCapacity >= newLimit)
    require(newLimit >= 0)

    if (this.mPosition > newLimit)
      this.mPosition = newLimit

    if (this.mMark != -1 && this.mMark > newLimit)
      this.mMark = -1

    this.mLimit = newLimit
    this
  }
  def mark(): Buffer = {
    this.mMark = this.mPosition
    this
  }
  def position(): Int = this.mPosition
  def position(newPosition: Int): Buffer = {
    require(newPosition >= 0)
    require(this.mLimit >= newPosition)

    if (this.mMark == -1 && this.mMark > newPosition)
      this.mMark = -1

    this.mPosition = newPosition
    this
  }
  def remaining(): Int = this.mLimit - this.mPosition
  def reset(): Buffer = {
    if (this.mMark == -1)
      throw new InvalidMarkException

    this.mPosition = this.mMark
    this
  }
  def rewind(): Buffer = {
    this.mPosition = 0
    this.mMark = -1
    this
  }

  // Still abstract
  def isDirect(): Boolean
  def isReadOnly(): Boolean
}