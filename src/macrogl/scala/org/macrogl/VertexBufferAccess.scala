package org.macrogl

trait VertexBufferAccess extends BufferAccess {
  this: Buffer =>

  val vertexCount: Int
  val attributeCount: Int
  
  override lazy val capacity = vertexCount * attributeCount * gl.bytesPerFloat

  object vertexBufferAccess extends VertexBufferAccessInner

  trait VertexBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.ARRAY_BUFFER

    def enableAttributeArrays(attribs: Array[(Int, Int)]) {
      var i = 0
      while (i < attribs.length) {
        gl.enableVertexAttribArray(i)
        i += 1
      }
    }

    def disableAttributeArrays(attribs: Array[(Int, Int)]) {
      var i = 0
      while (i < attribs.length) {
        gl.disableVertexAttribArray(i)
        i += 1
      }
    }

    def setAttributePointers(attribs: Array[(Int, Int)]) {
      val stride = attributeCount * gl.bytesPerFloat
      var i = 0
      while (i < attribs.length) {
        val byteOffset = attribs(i)._1 * gl.bytesPerFloat
        val num = attribs(i)._2
        gl.vertexAttribPointer(i, num, Macrogl.FLOAT, false, stride, byteOffset)
        i += 1
      }
    }

    def render(mode: Int, attribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(attribs)
        setAttributePointers(attribs)
        gl.drawArrays(mode, 0, vertexCount)
      } finally {
        disableAttributeArrays(attribs)
      }
    }
  }
}
