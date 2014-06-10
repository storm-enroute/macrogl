package org.scalajs.nio

class ReadOnlyFloatBuffer(protected val internalBuffer: FloatBuffer) extends FloatBuffer
  with ReadOnlyTypedBufferBehaviour[Float, FloatBuffer] {
  def asReadOnlyBuffer(): FloatBuffer = this.duplicate
  def duplicate(): FloatBuffer = new ReadOnlyFloatBuffer(internalBuffer.duplicate)
  def slice(): FloatBuffer = new ReadOnlyFloatBuffer(internalBuffer.slice)

  override def toString = "ReadOnlyFloatBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}