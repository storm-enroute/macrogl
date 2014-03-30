package scala.scalajs.nio

abstract class ByteBuffer(cMark: Int, cPosition: Int, cLimit: Int, cCapacity: Int) extends Buffer(cMark, cPosition, cLimit, cCapacity) with Comparable[ByteBuffer] {
  def asCharBuffer(): Nothing // CharBuffer not implemented
  def asDoubleBuffer(): DoubleBuffer
  def asFloatBuffer(): FloatBuffer
  def asIntBuffer(): IntBuffer
  def asLongBuffer(): LongBuffer
  def asReadOnlyBuffer(): ByteBuffer
  def asShortBuffer(): ShortBuffer
  def compact(): ByteBuffer
  def compareTo(that: ByteBuffer): Int
  def duplicate(): ByteBuffer
  def equals(ob: Any): Boolean

  def get(): Byte
  def get(dst: Array[Byte]): ByteBuffer
  def get(dst: Array[Byte], offset: Int, length: Int): ByteBuffer
  def get(index: Int): Byte
  def getChar(): Char // Char?
  def getChar(index: Int): Char // Char?
  def getDouble(): Double
  def getDouble(index: Int): Double
  def getFloat(): Float
  def getFloat(index: Int): Float
  def getInt(): Int
  def getInt(index: Int): Int
  def getLong(): Long
  def getLong(index: Int): Long
  def getShort(): Short
  def getShort(index: Int): Short
  def hashCode(): Int
  def order(bo: ByteOrder): ByteBuffer
  def put(b: Byte): ByteBuffer
  def put(src: Array[Byte]): ByteBuffer 
  def put(src: Array[Byte], offset: Int, length: Int): ByteBuffer
  def put(src: ByteBuffer): ByteBuffer
  def put(index: Int, b: Byte): ByteBuffer
  def putChar(value: Char): ByteBuffer // Char?
  def putChar(index: Int, value: Char): ByteBuffer // Char?
  def putDouble(value: Double): ByteBuffer
  def putDouble(index: Int, value: Double): ByteBuffer
  def putFloat(value: Float): ByteBuffer
  def putFloat(index: Int, value: Float): ByteBuffer
  def putInt(value: Int): ByteBuffer
  def putInt(index: Int, value: Int): ByteBuffer
  def putLong(value: Long): ByteBuffer
  def putLong(index: Int, value: Long): ByteBuffer
  def putShort(value: Short): ByteBuffer
  def putShort(index: Int, value: Short): ByteBuffer
  def slice(): ByteBuffer
}

object ByteBuffer {
  def allocate(capacity: Int): ByteBuffer = NativeByteBuffer.allocate(capacity)
  def allocateDirect(capacity: Int): ByteBuffer = NativeByteBuffer.allocateDirect(capacity)

  def wrap(array: Array[Byte]): ByteBuffer = NativeByteBuffer.wrap(array)
  def wrap(array: Array[Byte], offset: Int, length: Int): ByteBuffer = NativeByteBuffer.wrap(array, offset, length)
}