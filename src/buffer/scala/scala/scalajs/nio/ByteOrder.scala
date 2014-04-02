package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

abstract sealed class ByteOrder

case object BigEndian extends ByteOrder {
  override def toString = "BIG_ENDIAN"
}

case object LittleEndian extends ByteOrder {
  override def toString = "LITTLE_ENDIAN"
}

object ByteOrder {

  val BIG_ENDIAN: ByteOrder = BigEndian
  val LITTLE_ENDIAN: ByteOrder = LittleEndian

  private lazy val byteOrder: ByteOrder = {
    val buffer = g.ArrayBuffer(2)

    // the cast is a small hack to access the array operator "ar[id] = val"
    val byteView = g.Int8Array(buffer).asInstanceOf[js.Array[js.Number]]
    val shortView = g.Int16Array(buffer).asInstanceOf[js.Array[js.Number]]

    byteView(0) = 0x0F
    byteView(1) = 0x00

    shortView(0).toShort match {
      case 0x0F00 => BigEndian
      case 0x000F => LittleEndian
      case _ => throw new RuntimeException("Could not determine endianness")
    }
  }

  def nativeOrder(): ByteOrder = byteOrder
}