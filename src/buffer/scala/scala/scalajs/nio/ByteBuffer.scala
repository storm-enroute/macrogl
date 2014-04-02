package scala.scalajs.nio

abstract class ByteBuffer extends Buffer with TypedBuffer[Byte, ByteBuffer] with Comparable[ByteBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Byte]): ByteBuffer = this.put(src, 0, src.length)

  def asCharBuffer(): Nothing // CharBuffer not implemented
  def asShortBuffer(): ShortBuffer
  def asIntBuffer(): IntBuffer
  def asLongBuffer(): LongBuffer
  def asFloatBuffer(): FloatBuffer
  def asDoubleBuffer(): DoubleBuffer

  def order(bo: ByteOrder): ByteBuffer

  def getChar(): Char
  def getChar(index: Int): Char
  def getShort(): Short
  def getShort(index: Int): Short
  def getInt(): Int
  def getInt(index: Int): Int
  def getLong(): Long
  def getLong(index: Int): Long
  def getFloat(): Float
  def getFloat(index: Int): Float
  def getDouble(): Double
  def getDouble(index: Int): Double

  def putChar(value: Char): ByteBuffer
  def putChar(index: Int, value: Char): ByteBuffer
  def putShort(value: Short): ByteBuffer
  def putShort(index: Int, value: Short): ByteBuffer
  def putInt(value: Int): ByteBuffer
  def putInt(index: Int, value: Int): ByteBuffer
  def putLong(value: Long): ByteBuffer
  def putLong(index: Int, value: Long): ByteBuffer
  def putFloat(value: Float): ByteBuffer
  def putFloat(index: Int, value: Float): ByteBuffer
  def putDouble(value: Double): ByteBuffer
  def putDouble(index: Int, value: Double): ByteBuffer
}

object ByteBuffer {
  def allocate(capacity: Int): ByteBuffer = NativeByteBuffer.allocate(capacity)
  def allocateDirect(capacity: Int): ByteBuffer = NativeByteBuffer.allocateDirect(capacity)

  def wrap(array: Array[Byte]): ByteBuffer = NativeByteBuffer.wrap(array)
  def wrap(array: Array[Byte], offset: Int, length: Int): ByteBuffer = NativeByteBuffer.wrap(array, offset, length)
}