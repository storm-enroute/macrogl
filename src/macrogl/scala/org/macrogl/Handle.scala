package org.macrogl






trait Handle {
  def acquire(): Unit
  def release(): Unit
}

class HandleStorage {
  val handles = new scala.collection.mutable.Stack[Handle]()

  def manage[T <: Handle](handle: T): T = {
    println("acquiring " + handle)
    handle.acquire()
    handles.push(handle)
    handle
  }

  def clear(): Unit = {
    for (h <- handles) {
      println("releasing " + h)
      h.release()
    }
    handles.clear()
  }
}

object HandleStorage {
  def foreach(f: HandleStorage => Unit): Unit = {
    val storage = new HandleStorage
    f(storage)
    storage.clear()
  }
}
