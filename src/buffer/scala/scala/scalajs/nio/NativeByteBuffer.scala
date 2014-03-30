package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

class NativeByteBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int, cBuffer: js.Dynamic, cBufferOffset: Int, cByteOrder: ByteOrder) extends ByteBuffer(cMark, cPosition, cLimit, cCapacity) with JsNativeBuffer[Byte] {
  order(cByteOrder)
  
  // java.nio.NativeByteBuffer shared methods

  def asCharBuffer(): Nothing = ??? // CharBuffer not implemented
  def asDoubleBuffer(): DoubleBuffer = {
    val doubleCapacity = this.capacity / NativeDoubleBuffer.BYTES_PER_ELEMENT
    
    if(this.order == ByteOrder.nativeOrder)
      new NativeDoubleBuffer(-1, 0, doubleCapacity, doubleCapacity, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveDoubleBuffer(-1, 0, doubleCapacity, doubleCapacity, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asFloatBuffer(): NativeFloatBuffer = {
    val floatCapacity = this.capacity / NativeFloatBuffer.BYTES_PER_ELEMENT
    
    if(this.order == ByteOrder.nativeOrder)
      new NativeFloatBuffer(-1, 0, floatCapacity, floatCapacity, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveFloatBuffer(-1, 0, floatCapacity, floatCapacity, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asIntBuffer(): IntBuffer = {
    val intCapacity = this.capacity / NativeIntBuffer.BYTES_PER_ELEMENT
    
    if(this.order == ByteOrder.nativeOrder)
      new NativeIntBuffer(-1, 0, intCapacity, intCapacity, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveIntBuffer(-1, 0, intCapacity, intCapacity, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asLongBuffer(): LongBuffer = {
    val longCapacity = this.capacity / NativeLongBuffer.BYTES_PER_ELEMENT
    
    if(this.order == ByteOrder.nativeOrder)
      new NativeLongBuffer(-1, 0, longCapacity, longCapacity, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveLongBuffer(-1, 0, longCapacity, longCapacity, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def asReadOnlyBuffer(): NativeByteBuffer = throw new NotImplementedError // Not sure how we could implement this without condition everywhere or more duplicated code
  def asShortBuffer(): ShortBuffer = {
    val shortCapacity = this.capacity / NativeShortBuffer.BYTES_PER_ELEMENT
    
    if(this.order == ByteOrder.nativeOrder)
      new NativeShortBuffer(-1, 0, shortCapacity, shortCapacity, this.jsBuffer, this.jsBufferOffset)
    else
      new AdaptiveShortBuffer(-1, 0, shortCapacity, shortCapacity, this.jsBuffer, this.jsBufferOffset, this.order)
  }
  def compact(): NativeByteBuffer = {
    for (i <- 0 until this.remaining) {
      this.typedArray(0 + i) = this.typedArray(this.position + i)
    }
    this.position(this.remaining)
    this.limit(this.capacity)
    this.discardMark()
    this
  }
  def duplicate(): NativeByteBuffer = new NativeByteBuffer(this.markValue, this.position, this.limit, this.capacity, this.jsBuffer, this.jsBufferOffset, this.order)

  def get(): Byte = this.typedArray(this.nextGetIndex).byteValue
  def get(dst: Array[Byte]): NativeByteBuffer = this.get(dst, 0, dst.length)
  def get(dst: Array[Byte], offset: Int, length: Int): NativeByteBuffer = {
    this.checkBounds(offset, length, dst.length)
    if (length > this.remaining)
      throw new BufferUnderflowException
    for (i <- 0 until length) {
      dst(offset + i) = this.typedArray(this.position + i).byteValue
    }
    this.position(this.position + length)
    this
  }
  def get(index: Int): Byte = {
    this.typedArray(this.checkIndex(index)).byteValue
  }
  def getChar(): Char = ??? // Char?
  def getChar(index: Int): Char = ??? // Char?
  def getDouble(): Double = this.getDouble(this.nextGetIndex(NativeDoubleBuffer.BYTES_PER_ELEMENT))
  def getDouble(index: Int): Double = this.dataView.getFloat64(this.checkIndex(index, NativeDoubleBuffer.BYTES_PER_ELEMENT), this.littleEndian).asInstanceOf[js.Number].doubleValue
  def getFloat(): Float = this.getFloat(this.nextGetIndex(NativeFloatBuffer.BYTES_PER_ELEMENT))
  def getFloat(index: Int): Float = this.dataView.getFloat32(this.checkIndex(index, NativeFloatBuffer.BYTES_PER_ELEMENT), this.littleEndian).asInstanceOf[js.Number].floatValue
  def getInt(): Int = this.getInt(this.nextGetIndex(NativeIntBuffer.BYTES_PER_ELEMENT))
  def getInt(index: Int) = this.dataView.getInt32(this.checkIndex(index, NativeIntBuffer.BYTES_PER_ELEMENT), this.littleEndian).asInstanceOf[js.Number].intValue
  def getLong(): Long = this.getLong(this.nextGetIndex(NativeLongBuffer.BYTES_PER_ELEMENT))
  def getLong(index: Int): Long = {
    this.checkIndex(index, NativeLongBuffer.BYTES_PER_ELEMENT)

    val (lower, upper) = {
      if (littleEndian)
        (this.dataView.getInt32(index + 0, this.littleEndian).asInstanceOf[js.Number].intValue,
          this.dataView.getInt32(index + 4, this.littleEndian).asInstanceOf[js.Number].intValue)
      else
        (this.dataView.getInt32(index + 4, this.littleEndian).asInstanceOf[js.Number].intValue,
          this.dataView.getInt32(index + 0, this.littleEndian).asInstanceOf[js.Number].intValue)
    }

    Bits.pairIntToLong(upper, lower)
  }
  def getShort(): Short = this.getShort(this.nextGetIndex(NativeShortBuffer.BYTES_PER_ELEMENT))
  def getShort(index: Int): Short = this.dataView.getInt16(this.checkIndex(index, NativeShortBuffer.BYTES_PER_ELEMENT), this.littleEndian).asInstanceOf[js.Number].shortValue
  def order(): ByteOrder = {
    if (littleEndian)
      ByteOrder.LITTLE_ENDIAN
    else
      ByteOrder.BIG_ENDIAN
  }
  def order(bo: ByteOrder): NativeByteBuffer = {
    littleEndian = bo match {
      case ByteOrder.LITTLE_ENDIAN => true
      case ByteOrder.BIG_ENDIAN => false
      case _ => throw new IllegalArgumentException()
    }
    this
  }
  def put(b: Byte): NativeByteBuffer = {
    this.typedArray(this.nextPutIndex) = b
    this
  }
  def put(src: Array[Byte]): NativeByteBuffer = this.put(src, 0, src.length)
  def put(src: Array[Byte], offset: Int, length: Int): NativeByteBuffer = {
    this.checkBounds(offset, length, src.length)
    if (length > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until length) {
      this.typedArray(this.position + i) = src(offset + i)
    }
    this.position(this.position + length)
    this
  }
  def put(src: ByteBuffer): NativeByteBuffer = {
    // TODO Is there an efficient way to do that if the other NativeByteBuffer is backed by another JS array buffer? Didn't find anything related..
    val srcLength = src.remaining
    if (srcLength > this.remaining)
      throw new BufferOverflowException
    for (i <- 0 until srcLength) {
      this.typedArray(this.position + i) = src.get
    }
    this.position(this.position + srcLength)
    this
  }
  def put(index: Int, b: Byte): NativeByteBuffer = {
    this.typedArray(checkIndex(index)) = b
    this
  }
  def putChar(value: Char): NativeByteBuffer = ??? // Char?
  def putChar(index: Int, value: Char): NativeByteBuffer = ??? // Char?
  def putDouble(value: Double): NativeByteBuffer = this.putDouble(this.nextPutIndex(NativeDoubleBuffer.BYTES_PER_ELEMENT), value)
  def putDouble(index: Int, value: Double): NativeByteBuffer = {
    this.dataView.setFloat64(this.checkIndex(index, NativeDoubleBuffer.BYTES_PER_ELEMENT), value, this.littleEndian)
    this
  }
  def putFloat(value: Float): NativeByteBuffer = this.putFloat(this.nextPutIndex(NativeFloatBuffer.BYTES_PER_ELEMENT), value)
  def putFloat(index: Int, value: Float): NativeByteBuffer = {
    this.dataView.setFloat32(this.checkIndex(index, NativeFloatBuffer.BYTES_PER_ELEMENT), value, this.littleEndian)
    this
  }
  def putInt(value: Int): NativeByteBuffer = this.putInt(this.nextPutIndex(NativeIntBuffer.BYTES_PER_ELEMENT), value)
  def putInt(index: Int, value: Int): NativeByteBuffer = {
    this.dataView.setInt32(this.checkIndex(index, NativeIntBuffer.BYTES_PER_ELEMENT), value, this.littleEndian)
    this
  }
  def putLong(value: Long): NativeByteBuffer = this.putLong(this.nextPutIndex(NativeLongBuffer.BYTES_PER_ELEMENT), value)
  def putLong(index: Int, value: Long): NativeByteBuffer = {
    this.checkIndex(index, NativeLongBuffer.BYTES_PER_ELEMENT)

    val (upper, lower) = Bits.longToPairInt(value)

    if (littleEndian) {
      this.dataView.setInt32(index + 0, lower, this.littleEndian)
      this.dataView.setInt32(index + 4, upper, this.littleEndian)
    } else {
      this.dataView.setInt32(index + 4, lower, this.littleEndian)
      this.dataView.setInt32(index + 0, upper, this.littleEndian)
    }
    this
  }
  def putShort(value: Short): NativeByteBuffer = this.putShort(this.nextPutIndex(NativeShortBuffer.BYTES_PER_ELEMENT), value)
  def putShort(index: Int, value: Short): NativeByteBuffer = {
    this.dataView.setInt16(this.checkIndex(index, NativeShortBuffer.BYTES_PER_ELEMENT), value, this.littleEndian)
    this
  }
  def slice(): NativeByteBuffer = {
    // Why not passing the endianness? Don't know, Java doesn't do it... Seems weird, right?
    new NativeByteBuffer(-1, 0, this.remaining, this.remaining, this.cBuffer, this.cBufferOffset + this.position, ByteOrder.BIG_ENDIAN)
  }

  def compareElement(b1: Byte, b2: Byte): Int = {
    if (b1 < b2)
      -1
    else if (b1 > b2)
      +1
    else
      0
  }

  def compareTo(that: ByteBuffer): Int = {
    val length = Math.min(this.remaining, that.remaining)

    val i = this.position
    val j = that.position

    for (k <- 0 until length) {
      val curI = this.get(i + k)
      val curJ = that.get(j + k)

      val cmp = compareElement(curI, curJ)

      if (cmp != 0)
        return cmp
    }

    this.remaining - that.remaining
  }
  override def equals(ob: Any): Boolean = ob match {
    case that: NativeByteBuffer => {
      if (this.remaining != that.remaining)
        return false
      val length = this.remaining

      val i = this.position
      val j = that.position

      for (k <- 0 until length) {
        val curI = this.get(i + k)
        val curJ = that.get(j + k)

        if (compareElement(curI, curJ) != 0)
          return false
      }

      return true
    }
    case _ => false
  }
  override def hashCode(): Int = {
    var h: Int = 1
    val length = this.remaining

    for (i <- 0 until length) {
      h = 31 * h + this.get(this.position + i).toInt
    }

    (h & 0xFFFFFFFF) // just making just the JavaScript Number (Double) doesn't play a trick on us here
  }

  override def toString(): String = "NativeByteBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  // ScalaJS specific methods
  def jsBuffer(): js.Dynamic = this.cBuffer
  def jsBufferOffset(): Int = this.cBufferOffset
  
  def hasJsArray(): Boolean = true
  def jsArray(): js.Array[js.Number] = typedArray

  // ScalaJS internals
  protected val typedArray: js.Array[js.Number] = g.Int8Array(cBuffer, cBufferOffset, cCapacity).asInstanceOf[js.Array[js.Number]]

  protected var littleEndian: Boolean = _
}

object NativeByteBuffer {
  def allocate(capacity: Int): NativeByteBuffer = {
    val jsBuffer = g.ArrayBuffer(capacity)
    // Why not native endianness? No idea, ask Oracle!
    val NativeByteBuffer = new NativeByteBuffer(-1, 0, capacity, capacity, jsBuffer, 0, ByteOrder.BIG_ENDIAN)
    NativeByteBuffer
  }
  def allocateDirect(capacity: Int): NativeByteBuffer = allocate(capacity)

  /**
   * WARNING: Behavior is different from the original wrap() methods from java.nio._ as the content of the array and the buffer are NOT shared in this implementation (unlike in Java).
   * This is basically just an helper method to create a buffer from an array, not an actual wrapper for an array.
   */
  def wrap(array: Array[Byte]): NativeByteBuffer = wrap(array, 0, array.length)
  def wrap(array: Array[Byte], offset: Int, length: Int): NativeByteBuffer = {
    val NativeByteBuffer = allocate(length)
    val internalJsArray = NativeByteBuffer.jsArray
    for (i <- 0 until length) {
      internalJsArray(i) = array(i + offset)
    }
    NativeByteBuffer
  }

  // ScalaJS specific methods

  val BYTES_PER_ELEMENT: Int = g.Int8Array.BYTES_PER_ELEMENT.asInstanceOf[js.Number].intValue
}