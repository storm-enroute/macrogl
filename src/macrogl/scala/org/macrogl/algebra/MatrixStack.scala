package org.macrogl.algebra



import scala.collection.mutable.ArrayBuffer



class MatrixStack[T <: Matrix](var current: T) {
  private val stack: ArrayBuffer[T] = new ArrayBuffer[T]()

  def push: Unit = {
    stack += current.copy.asInstanceOf[T]
  }

  def pop: T = if(empty) {
    throw new RuntimeException("Stack empty")
  } else {
    stack.remove(stack.size - 1)
  }

  def empty: Boolean = stack.size == 0
}
