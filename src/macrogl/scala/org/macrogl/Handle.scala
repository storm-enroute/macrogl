package org.macrogl

trait Handle {
  def acquire(): Unit
  def release(): Unit
}