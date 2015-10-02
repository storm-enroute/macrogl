package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.collection._



class AttributeBuffer(
  val usage: Int,
  val capacity: Int,
  val totalAttributes: Int,
  val attributes: Array[(Int, Int)]
)(implicit gl: Macrogl) extends VertexBuffer {
  private var vtoken = Token.Buffer.invalid
  private var result = new Array[Int](1)

  def acquire() {
    release()
    vtoken = gl.createBuffer
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    gl.bufferData(Macrogl.ARRAY_BUFFER, totalBytes, usage)
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
    gl.checkError()
  }

  def release() {
    if (gl.validBuffer(vtoken)) {
      gl.deleteBuffer(vtoken)
      vtoken = Token.Buffer.invalid
    }
  }

  def view(customAttribs: Array[(Int, Int)]) = new VertexBuffer {
    def acquire() = sys.error("Views cannot be acquired.")
    def release() = sys.error("Views cannot be released.")
    def enableForRender(): Unit = {
      enableAttributeArrays(customAttribs)
      setAttributePointers(customAttribs)
    }
    def disableForRender(): Unit = {
      disableAttributeArrays(customAttribs)
    }
    def token = AttributeBuffer.this.token
    val access = new VertexBuffer.Access {
      def render(mode: Int) =
        AttributeBuffer.this.access.renderAttribs(mode, customAttribs)
    }
  }

  def token: Token.Buffer = vtoken

  def totalBytes = capacity * totalAttributes * gl.bytesPerFloat

  def send(offset: Long, data: Data.Float) {
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, vtoken)
    gl.bufferSubData(Macrogl.ARRAY_BUFFER, offset, data)
    gl.bindBuffer(Macrogl.ARRAY_BUFFER, Token.Buffer.none)
  }

  def enableAttributeArrays(): Unit = {
    enableAttributeArrays(attributes)
  }

  def disableAttributeArrays(): Unit = {
    disableAttributeArrays(attributes)
  }

  private def enableAttributeArrays(attribs: Array[(Int, Int)]): Unit = {
    var i = 0
    while (i < attribs.length) {
      gl.enableVertexAttribArray(i)
      i += 1
    }
  }

  private def disableAttributeArrays(attribs: Array[(Int, Int)]): Unit = {
    var i = 0
    while (i < attribs.length) {
      gl.disableVertexAttribArray(i)
      i += 1
    }
  }

  def setAttributePointers(): Unit = {
    setAttributePointers(attributes)
  }

  def setAttributePointers(attribs: Array[(Int, Int)]): Unit = {
    val stride = totalAttributes * gl.bytesPerFloat
    var i = 0
    while (i < attribs.length) {
      val byteOffset = attribs(i)._1 * gl.bytesPerFloat
      val num = attribs(i)._2
      gl.vertexAttribPointer(i, num, Macrogl.FLOAT, false, stride, byteOffset)
      i += 1
    }
  }

  def enableForRender(): Unit = {
    enableAttributeArrays()
    gl.checkError()
    setAttributePointers()
    gl.checkError()
  }

  def disableForRender(): Unit = {
    disableAttributeArrays()
  }

  object access extends VertexBuffer.Access {
    def render(mode: Int) {
      try {
        enableForRender()
        gl.drawArrays(mode, 0, capacity)
      } finally {
        disableForRender()
      }
    }
    def renderAttribs(mode: Int, customAttribs: Array[(Int, Int)]) {
      try {
        enableAttributeArrays(customAttribs)
        setAttributePointers(customAttribs)
        gl.drawArrays(mode, 0, capacity)
      } finally {
        disableAttributeArrays(customAttribs)
      }
    }
  }

}
