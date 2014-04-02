package scala.scalajs.nio

class ReadOnlyByteBuffer(protected val internalBuffer: ByteBuffer) extends ByteBuffer
with ReadOnlyTypedBufferBehaviour[Byte, ByteBuffer] {
  def asReadOnlyBuffer(): ByteBuffer = this.duplicate
  def duplicate(): ByteBuffer = new ReadOnlyByteBuffer(internalBuffer.duplicate)
  def slice(): ByteBuffer = new ReadOnlyByteBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyByteBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"

  def asCharBuffer(): Nothing = throw new NotImplementedError // CharBuffer not implemented
  def asShortBuffer(): ShortBuffer = new ReadOnlyShortBuffer(internalBuffer.asShortBuffer)
  def asIntBuffer(): IntBuffer = new ReadOnlyIntBuffer(internalBuffer.asIntBuffer)
  def asLongBuffer(): LongBuffer = new ReadOnlyLongBuffer(internalBuffer.asLongBuffer)
  def asFloatBuffer(): FloatBuffer = new ReadOnlyFloatBuffer(internalBuffer.asFloatBuffer)
  def asDoubleBuffer(): DoubleBuffer = new ReadOnlyDoubleBuffer(internalBuffer.asDoubleBuffer)

  def order(bo: ByteOrder): ByteBuffer = internalBuffer.order(bo)

  def getChar(): Char = internalBuffer.getChar
  def getChar(index: Int): Char = internalBuffer.getChar(index)
  def getShort(): Short = internalBuffer.getShort
  def getShort(index: Int): Short = internalBuffer.getShort(index)
  def getInt(): Int = internalBuffer.getInt
  def getInt(index: Int): Int = internalBuffer.getInt(index)
  def getLong(): Long = internalBuffer.getLong
  def getLong(index: Int): Long = internalBuffer.getLong(index)
  def getFloat(): Float = internalBuffer.getFloat
  def getFloat(index: Int): Float = internalBuffer.getFloat(index)
  def getDouble(): Double = internalBuffer.getDouble
  def getDouble(index: Int): Double = internalBuffer.getDouble(index)

  def putChar(value: Char): ByteBuffer = throw new ReadOnlyBufferException
  def putChar(index: Int, value: Char): ByteBuffer = throw new ReadOnlyBufferException
  def putShort(value: Short): ByteBuffer = throw new ReadOnlyBufferException
  def putShort(index: Int, value: Short): ByteBuffer = throw new ReadOnlyBufferException
  def putInt(value: Int): ByteBuffer = throw new ReadOnlyBufferException
  def putInt(index: Int, value: Int): ByteBuffer = throw new ReadOnlyBufferException
  def putLong(value: Long): ByteBuffer = throw new ReadOnlyBufferException
  def putLong(index: Int, value: Long): ByteBuffer = throw new ReadOnlyBufferException
  def putFloat(value: Float): ByteBuffer = throw new ReadOnlyBufferException
  def putFloat(index: Int, value: Float): ByteBuffer = throw new ReadOnlyBufferException
  def putDouble(value: Double): ByteBuffer = throw new ReadOnlyBufferException
  def putDouble(index: Int, value: Double): ByteBuffer = throw new ReadOnlyBufferException
}