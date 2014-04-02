package scala.scalajs.nio

abstract class IntBuffer extends Buffer with TypedBuffer[Int, IntBuffer] with Comparable[IntBuffer] {
  // Defining this one here instead of TypedBuffer because of the type erasure conflict
  def put(src: Array[Int]): IntBuffer = this.put(src, 0, src.length)

  override def toString = "IntBuffer[pos=" + this.position + " lim=" + this.limit + " cap=" + this.capacity + "]"
}

object IntBuffer {
  def allocate(capacity: Int): IntBuffer = NativeIntBuffer.allocate(capacity)

  def wrap(array: Array[Int]): IntBuffer = NativeIntBuffer.wrap(array)
  def wrap(array: Array[Int], offset: Int, length: Int): IntBuffer = NativeIntBuffer.wrap(array, offset, length)
}

