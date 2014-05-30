package org.macrogl.math

object Utils {
  val degToRadFactor: Float = (Math.PI / 180.0).toFloat
  val radToDegFactor: Float = (180.0 / Math.PI).toFloat

  def degToRad(deg: Float): Float = {
    deg * degToRadFactor
  }
  
  def radToDeg(rad: Float): Float = {
    rad * radToDegFactor
  }

}