package org.macrogl.examples.backend.common

abstract class DemoRenderable {
	def render(elapsedTime: Float): Boolean
	def close(): Unit
}