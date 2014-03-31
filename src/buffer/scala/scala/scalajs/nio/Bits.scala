package scala.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

class Bits {

}

object Bits {
  // Public methods
  
  // java.nio.Bits shared methods
  
  def swap(x:Short):Short = {
    viewShort(0) = x
    swap2()
    viewShort(0).shortValue
  }
  
  def swap(x:Int):Int = {
    viewInt(0) = x
    swap4()
    viewInt(0).intValue
  }
  
  def swap(x:Long):Long = {
    val (upper, lower) = longToPairInt(x)
    
    // Endianness irrelevant
    viewInt(0) = lower
    viewInt(1) = upper
    
    swap8()
    
    val lo = viewInt(0).intValue
    val up = viewInt(1).intValue
    
    pairIntToLong(up, lo)
  }
  
  def swap(x:Float):Float = {
    viewFloat(0) = x
    swap4()
    viewFloat(0).floatValue
  }
  
  def swap(x:Double):Double = {
    viewDouble(0) = x
    swap8()
    viewDouble(0).doubleValue
  }
  
  // ScalaJS specific methods
  
  def intBitsToFloat(x:Int):Float = {
    viewInt(0) = x
    viewFloat(0).floatValue
  }
  
  def floatToIntBits(x:Float):Int = {
    viewFloat(0) = x
    viewInt(0).intValue
  }
  
  /**
   * Output format: (Upper bits, lower bits)
   */
  def longToPairInt(x:Long):(Int, Int) = {
    val lower:Int = (x & 0xFFFFFFFFL).asInstanceOf[Int]
    val upper:Int = ((x >> 32) & 0xFFFFFFFFL).asInstanceOf[Int]
    
    (upper, lower)
  }
  
  /**
   * Argument format: (Upper bits, lower bits)
   */
  def pairIntToLong(x:(Int, Int)):Long = pairIntToLong(x._1, x._2)
  def pairIntToLong(upper:Int, lower:Int):Long = {
    (upper.asInstanceOf[Long] << 32) | (lower.asInstanceOf[Long])
  }
  
  def longBitsToDouble(x:Long):Double = {
    val (upper, lower) = longToPairInt(x)
    
    ByteOrder.nativeOrder match {
      case ByteOrder.LITTLE_ENDIAN => {
        viewInt(0) = lower
        viewInt(1) = upper
      }
      case ByteOrder.BIG_ENDIAN => {
        viewInt(1) = lower
        viewInt(0) = upper
      }
      case other => {
        throw new RuntimeException("Endianness not recognized: " + other)
      }
    }
    
    viewDouble(0).doubleValue
  }
  
  def doubleToLongBits(x:Double):Long = {
    viewDouble(0) = x
    
    val (upper, lower) = ByteOrder.nativeOrder match {
      case ByteOrder.LITTLE_ENDIAN => (viewInt(1).intValue, viewInt(0).intValue)
      case ByteOrder.BIG_ENDIAN => (viewInt(0).intValue, viewInt(1).intValue)
      case other => throw new RuntimeException("Endianness not recognized: " + other)
    }
    
    pairIntToLong(upper, lower)
  }
  
  // Private internal components
  // Binary buffer to keep the temporary elements
  private val buffer = g.ArrayBuffer(8)
  // Accessors to the buffer
  private val viewByte = g.Int8Array(buffer).asInstanceOf[js.Array[js.Number]]
  private val viewShort = g.Int16Array(buffer).asInstanceOf[js.Array[js.Number]]
  private val viewInt = g.Int32Array(buffer).asInstanceOf[js.Array[js.Number]]
  private val viewFloat = g.Float32Array(buffer).asInstanceOf[js.Array[js.Number]]
  private val viewDouble = g.Float64Array(buffer).asInstanceOf[js.Array[js.Number]]
  
  private def swap2():Unit = {
    val v0 = viewByte(0)
    val v1 = viewByte(1)
    
    viewByte(0) = v1
    viewByte(1) = v0
  }
  
  private def swap4():Unit = {
    val v0 = viewByte(0)
    val v1 = viewByte(1)
    val v2 = viewByte(2)
    val v3 = viewByte(3)
    
    viewByte(0) = v3
    viewByte(1) = v2
    viewByte(2) = v1
    viewByte(3) = v0
  }
  
  private def swap8():Unit = {
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