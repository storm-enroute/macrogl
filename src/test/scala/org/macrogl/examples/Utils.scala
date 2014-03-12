package org.macrogl.examples

import org.macrogl._

object Utils {
  def perspectiveProjection(fovDegrees: Double, aspectRatio: Double, nearZ: Double, farZ: Double): Matrix.Projection = {
    val rad = fovDegrees * scala.math.Pi / 180.0f

    val y = 1.0 / scala.math.tan(rad / 2.0)
    val x = y / aspectRatio
    val z = -(farZ + nearZ) / (farZ - nearZ)
    val t = -2 * farZ * nearZ / (farZ - nearZ)

    val arr = Array(
      x, 0, 0,  0,
      0, y, 0,  0,
      0, 0, z, -1,
      0, 0, t,  0
    )

    new Matrix.Projection(arr)
  }

  def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString
}
