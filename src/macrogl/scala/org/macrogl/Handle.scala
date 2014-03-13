package org.macrogl






trait Handle {
  def acquire(): Unit
  def release(): Unit
}

object Handle {
  def manage[T <: Handle](handle: T) = scala.util.continuations.shift { k: (T => Unit) =>
    println("acquiring " + handle)
    handle.acquire()
    k(handle)
    println("releasing " + handle)
    handle.release()
  }
}
