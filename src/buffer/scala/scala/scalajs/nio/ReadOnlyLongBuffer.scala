package scala.scalajs.nio

class ReadOnlyLongBuffer(protected val internalBuffer: LongBuffer) extends LongBuffer
with ReadOnlyTypedBufferBehaviour[Long, LongBuffer] {
  def asReadOnlyBuffer(): LongBuffer = this.duplicate
  def duplicate(): LongBuffer = new ReadOnlyLongBuffer(internalBuffer.duplicate)
  def slice(): LongBuffer = new ReadOnlyLongBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyLongBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}