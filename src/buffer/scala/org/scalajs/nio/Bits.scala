package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom

object Bits {
  // Public methods

  // java.nio.Bits shared methods

  def swap(x: Short): Short = {
    viewShort(0) = x
    swap2()
    viewShort(0).toShort
  }

  def swap(x: Int): Int = {
    viewInt(0) = x
    swap4()
    viewInt(0).toInt
  }

  def swap(x: Long): Long = {
    val (upper, lower) = longToPairInt(x)

    // Endianness irrelevant
    viewInt(0) = lower
    viewInt(1) = upper

    swap8()

    val lo = viewInt(0).toInt
    val up = viewInt(1).toInt

    pairIntToLong(up, lo)
  }

  def swap(x: Float): Float = {
    viewFloat(0) = x
    swap4()
    viewFloat(0).toFloat
  }

  def swap(x: Double): Double = {
    viewDouble(0) = x
    swap8()
    viewDouble(0).toDouble
  }

  // ScalaJS specific methods

  def intBitsToFloat(x: Int): Float = {
    viewInt(0) = x
    viewFloat(0).toFloat
  }

  def floatToIntBits(x: Float): Int = {
    viewFloat(0) = x
    viewInt(0).toInt
  }

  /**
   * Output format: (Upper bits, lower bits)
   */
  def longToPairInt(x: Long): (Int, Int) = {
    val lower: Int = x.toInt
    val upper: Int = (x >> 32).toInt

    (upper, lower)
  }

  /**
   * Argument format: (Upper bits, lower bits)
   */
  def pairIntToLong(x: (Int, Int)): Long = pairIntToLong(x._1, x._2)
  def pairIntToLong(upper: Int, lower: Int): Long = {
    (upper.toLong << 32) | (lower.toLong & 0xFFFFFFFFL)
  }

  def longBitsToDouble(x: Long): Double = {
    val (upper, lower) = longToPairInt(x)

    ByteOrder.nativeOrder match {
      case LittleEndian =>
        viewInt(0) = lower
        viewInt(1) = upper
      case BigEndian =>
        viewInt(1) = lower
        viewInt(0) = upper
    }

    viewDouble(0).toDouble
  }

  def doubleToLongBits(x: Double): Long = {
    viewDouble(0) = x

    val (upper, lower) = ByteOrder.nativeOrder match {
      case LittleEndian => (viewInt(1).toInt, viewInt(0).toInt)
      case BigEndian => (viewInt(0).toInt, viewInt(1).toInt)
    }

    pairIntToLong(upper, lower)
  }

  // Private internal components
  // Binary buffer to keep the temporary elements
  private val buffer = js.Dynamic.newInstance(g.ArrayBuffer)(8).asInstanceOf[dom.ArrayBuffer]
  // Accessors to the buffer
  private val viewByte = new dom.Int8Array(buffer)
  private val viewShort = new dom.Int16Array(buffer)
  private val viewInt = new dom.Int32Array(buffer)
  private val viewFloat = new dom.Float32Array(buffer)
  private val viewDouble = new dom.Float64Array(buffer)

  private def swap2(): Unit = {
    val v0 = viewByte(0)
    val v1 = viewByte(1)

    viewByte(0) = v1
    viewByte(1) = v0
  }

  private def swap4(): Unit = {
    val v0 = viewByte(0)
    val v1 = viewByte(1)
    val v2 = viewByte(2)
    val v3 = viewByte(3)

    viewByte(0) = v3
    viewByte(1) = v2
    viewByte(2) = v1
    viewByte(3) = v0
  }

  private def swap8(): Unit = {
    val v0 = viewByte(0)
    val v1 = viewByte(1)
    val v2 = viewByte(2)
    val v3 = viewByte(3)
    val v4 = viewByte(4)
    val v5 = viewByte(5)
    val v6 = viewByte(6)
    val v7 = viewByte(7)

    viewByte(0) = v7
    viewByte(1) = v6
    viewByte(2) = v5
    viewByte(3) = v4
    viewByte(4) = v3
    viewByte(5) = v2
    viewByte(6) = v1
    viewByte(7) = v0
  }
}