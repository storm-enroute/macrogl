package org.scalajs.nio

class ReadOnlyIntBuffer(protected val internalBuffer: IntBuffer) extends IntBuffer
  with ReadOnlyTypedBufferBehaviour[Int, IntBuffer] {
  def asReadOnlyBuffer(): IntBuffer = this.duplicate
  def duplicate(): IntBuffer = new ReadOnlyIntBuffer(internalBuffer.duplicate)
  def slice(): IntBuffer = new ReadOnlyIntBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyIntBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}