package scala.scalajs.nio

class ReadOnlyShortBuffer(protected val internalBuffer: ShortBuffer) extends ShortBuffer
with ReadOnlyTypedBufferBehaviour[Short, ShortBuffer] {
  def asReadOnlyBuffer(): ShortBuffer = this.duplicate
  def duplicate(): ShortBuffer = new ReadOnlyShortBuffer(internalBuffer.duplicate)
  def slice(): ShortBuffer = new ReadOnlyShortBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyShortBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}