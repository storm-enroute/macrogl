package org.macrogl.math

class MatrixStack[T <: Matrix](var current: T) {
  private var stack: List[T] = Nil

  def push: Unit = {
    stack = current.copy.asInstanceOf[T] :: stack
  }

  def pop: T = stack match {
    case x :: xs =>
      current = x
      stack = xs
      x
    case Nil =>
      throw new RuntimeException("Stack empty")
  }

  def empty: Boolean = stack != Nil
}