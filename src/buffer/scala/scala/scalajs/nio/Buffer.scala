package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract class Buffer(private var cMark: Int, private var cPosition: Int, private var cLimit: Int, private var cCapacity: Int) extends Object {
  if (cMark > 0)
    require(cMark <= cPosition)
  position(cPosition)
  limit(cLimit)
  require(cCapacity >= 0)

  // java.nio.Buffer shared methods

  def capacity(): Int = cCapacity
  def clear(): Buffer = {
    cMark = -1
    cPosition = 0
    cLimit = cCapacity
    this
  }
  def flip(): Buffer = {
    cLimit = cPosition
    cPosition = 0
    cMark = -1
    this
  }

  def hasRemaining(): Boolean = remaining > 0

  def limit(): Int = cLimit
  def limit(newLimit: Int): Buffer = {
    require(newLimit >= 0)
    require(newLimit <= cCapacity)
    cLimit = newLimit
    if (cPosition > cLimit) cPosition = cLimit
    if (cMark > cLimit) cMark = -1
    this
  }
  def mark(): Buffer = {
    cMark = cPosition
    this
  }
  def position(): Int = cPosition
  def position(newPosition: Int): Buffer = {
    require(newPosition >= 0)
    require(newPosition <= cLimit)
    cPosition = newPosition
    this
  }
  def remaining(): Int = cLimit - cPosition
  def reset(): Buffer = {
    if (cMark < 0)
      throw new InvalidMarkException()

    cPosition = cMark
    this
  }
  def rewind(): Buffer = {
    cPosition = 0
    cMark = -1
    this
  }

  // Abstract methods
  def array(): Object // abstract optional
  def arrayOffset(): Int // abstract optional
  def hasArray(): Boolean // abstract
  def isDirect(): Boolean // abstract
  def isReadOnly(): Boolean // abstract

  // Helper methods for subclasses
  protected def nextGetIndex(): Int = {
    if (cPosition >= limit)
      throw new BufferUnderflowException()
    val curPos = cPosition
    cPosition += 1
    curPos
  }

  protected def nextGetIndex(nb: Int): Int = {
    if (cLimit - cPosition < nb)
      throw new BufferUnderflowException()
    val curPos = cPosition
    cPosition += nb
    curPos
  }

  protected def nextPutIndex(): Int = {
    if (cPosition >= limit)
      throw new BufferUnderflowException()
    val curPos = cPosition
    cPosition += 1
    curPos
  }

  protected def nextPutIndex(nb: Int): Int = {
    if (cLimit - cPosition < nb)
      throw new BufferUnderflowException()
    val curPos = cPosition
    cPosition += nb
    curPos
  }

  protected def checkIndex(i: Int): Int = {
    if ((i < 0) || (i >= cLimit))
      throw new IndexOutOfBoundsException()
    i
  }

  protected def checkIndex(i: Int, nb: Int): Int = {
    if ((i < 0) || (nb > cLimit - i))
      throw new IndexOutOfBoundsException()
    i
  }

  protected def markValue(): Int = cMark

  protected def discardMark(): Unit = cMark = -1

  protected def checkBounds(off: Int, len: Int, size: Int): Unit = {
    //if ((off | len | (off + len) | (size - (off + len))) < 0)
    if (off < 0 || len < 0 || off + len < 0 || size - (off + len) < 0)
      throw new IndexOutOfBoundsException()
  }

  // ScalaJS specific methods

  // Abstract methods
  def jsBuffer(): js.Dynamic
  def jsBufferOffset(): Int
  
  // the dataView and the typed array should be agnostic of the actual offset in the buffer: just access them, it should be fine
  def jsDataView(): js.Dynamic
  
  def hasJsArray(): Boolean
  def jsArray(): js.Array[js.Number]
  
  def bytes_per_element():Int
}