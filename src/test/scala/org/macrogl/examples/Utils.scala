package org.macrogl.examples

import org.macrogl._

object Utils {
  def readResource(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

  def perspectiveProjection(fovDegrees: Double, aspectRatio: Double, nearZ: Double, farZ: Double): Matrix.Plain = {
    val rad = scala.math.toRadians(fovDegrees)

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

    new Matrix.Plain(arr)
  }

  class Camera(x: Double, y: Double, z: Double) {
    val position = Array(x, y, z)
    var horizontalAngle = 0.0
    var verticalAngle   = 0.0

    def orientation = {
      import scala.math.{sin, cos, toRadians => r}

      val cv = cos(r(verticalAngle))
      val sv = sin(r(verticalAngle))
      val vRotate = Array[Double](
          1,  0,  0,  0,
          0,  cv, sv, 0,
          0, -sv, cv, 0,
          0,  0,  0,  1
        )

      val ch = cos(r(horizontalAngle))
      val sh = sin(r(horizontalAngle))
      val hRotate = Array[Double](
           ch, 0, sh, 0,
           0,  1, 0,  0,
          -sh, 0, ch, 0,
           0,  0, 0,  1
        )

      Matrix.multiply[Matrix.Plain](new Matrix.Plain(vRotate), new Matrix.Plain(hRotate))
    }

    def invertedOrientation = new Matrix.Plain(orientation.array.grouped(4).toArray.transpose.flatten)

    def forwardDirection = {
      val inv = invertedOrientation
      Array(
        inv(0, 3) - inv(0, 2),
        inv(1, 3) - inv(1, 2),
        inv(2, 3) - inv(2, 2)
      )
    }

    def rightDirection = {
      val inv = invertedOrientation
      Array(
        inv(0, 3) + inv(0, 0),
        inv(1, 3) + inv(1, 0),
        inv(2, 3) + inv(2, 0)
      )
    }

    def moveRight(distance: Double): Unit = {
      val rd = rightDirection
      position(0) += rd(0) * distance
      position(1) += rd(1) * distance
      position(2) += rd(2) * distance
    }
    def moveLeft(distance: Double): Unit = moveRight(-distance)

    def moveForward(distance: Double): Unit = {
      val fd = forwardDirection
      position(0) += fd(0) * distance
      position(1) += fd(1) * distance
      position(2) += fd(2) * distance
    }
    def moveBackward(distance: Double): Unit = moveForward(-distance)

    def transform = {
      val translation = new Matrix.Plain(
        Array[Double](
          1, 0, 0, 0,
          0, 1, 0, 0,
          0, 0, 1, 0,
          -position(0), -position(1), -position(2), 1
        )
      )
      Matrix.multiply[Matrix.Plain](orientation, translation)
    }

    def offsetOrientation(h: Double, v: Double): Unit = {
      horizontalAngle += h
      verticalAngle   += v
      normalizeAngles()
    }

    private def normalizeAngles(): Unit = {
      horizontalAngle %= 360.0
      verticalAngle   %= 360.0

      if (horizontalAngle < 0) horizontalAngle += 360.0

      val maxVerticalAngle = 85.0
      if (verticalAngle >  maxVerticalAngle) verticalAngle =  maxVerticalAngle
      if (verticalAngle < -maxVerticalAngle) verticalAngle = -maxVerticalAngle
    }
  }
}
