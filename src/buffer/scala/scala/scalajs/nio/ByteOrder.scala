package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

case class ByteOrder(order: String) extends Object {
  override def toString(): String = order
}

object ByteOrder {
  val BIG_ENDIAN = new ByteOrder("BIG_ENDIAN")
  val LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN")

  private lazy val byteOrder: ByteOrder = {
    val buffer = g.ArrayBuffer(2)

    // the cast is a small hack to access the array operator "ar[id] = val"
    val byteView = g.Int8Array(buffer).asInstanceOf[js.Array[js.Number]]
    val shortView = g.Int16Array(buffer).asInstanceOf[js.Array[js.Number]]

    byteView(0) = 0x0F
    byteView(1) = 0x00

    shortView(0).shortValue match {
      case 0x0F00 => BIG_ENDIAN
      case 0x000F => LITTLE_ENDIAN
      case _ => throw new RuntimeException("Could not determine endianness")
    }
  }

  def nativeOrder(): ByteOrder = byteOrder
}