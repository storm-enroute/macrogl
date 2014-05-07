package org.macrogl

trait BufferAccess {
  this: Buffer =>

  val capacity: Int

  trait BufferAccessInner {
    val target: Int

    def allocate(usage: Int) {
      gl.bufferData(target, capacity, usage)
    }
    def send(offset: Long, data: Data.Byte): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Int): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Float): Unit = {
      gl.bufferSubData(target, offset, data)
    }
    def send(offset: Long, data: Data.Double): Unit = {
      gl.bufferSubData(target, offset, data)
    }
  }
}

