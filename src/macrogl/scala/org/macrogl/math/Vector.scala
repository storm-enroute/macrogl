package org.macrogl.math

abstract class Vector {
  def apply(pos: Int): Float
  def update(pos: Int, v: Float): Unit
  
  def load(src: org.macrogl.Data.Float): Vector
  def store(dst: org.macrogl.Data.Float): Vector

  def normalise(): Vector
  def normalizedCopy(): Vector

  def negate(): Vector
  def negatedCopy(): Vector

  def lengthSquared(): Float
  def length(): Float
  
  def copy(): Vector
}