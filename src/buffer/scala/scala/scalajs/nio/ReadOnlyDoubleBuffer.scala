package scala.scalajs.nio

class ReadOnlyDoubleBuffer(protected val internalBuffer: DoubleBuffer) extends DoubleBuffer
with ReadOnlyTypedBufferBehaviour[Double, DoubleBuffer] {
  def asReadOnlyBuffer(): DoubleBuffer = this.duplicate
  def duplicate(): DoubleBuffer = new ReadOnlyDoubleBuffer(internalBuffer.duplicate)
  def slice(): DoubleBuffer = new ReadOnlyDoubleBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyDoubleBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}