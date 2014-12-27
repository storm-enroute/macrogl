package org.macrogl.algebra






/** A mutable matrix class. */
abstract class Matrix {
  def apply(row: Int, col: Int): Float
  def update(row: Int, col: Int, v: Float): Unit

  def load(src: org.macrogl.Data.Float, order: MajorOrder): Matrix
  def store(dst: org.macrogl.Data.Float, order: MajorOrder): Matrix

  def setIdentity(): Matrix
  def setZero(): Matrix

  def invert(): Matrix
  def invertedCopy(): Matrix

  def negate(): Matrix
  def negatedCopy(): Matrix

  def transpose(): Matrix
  def transposedCopy(): Matrix

  def determinant(): Float

  def copy(): Matrix
}
