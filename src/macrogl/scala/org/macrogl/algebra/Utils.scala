package org.macrogl.algebra






object Utils {
  val degToRadFactor: Float = (Math.PI / 180.0).toFloat
  val radToDegFactor: Float = (180.0 / Math.PI).toFloat

  def degToRad(deg: Float): Float = {
    deg * degToRadFactor
  }

  def radToDeg(rad: Float): Float = {
    rad * radToDegFactor
  }

  def cotan(v: Double): Double = {
    1.0 / Math.tan(v)
  }

}
