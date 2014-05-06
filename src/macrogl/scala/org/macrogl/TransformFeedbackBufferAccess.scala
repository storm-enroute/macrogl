package org.macrogl

trait TransformFeedbackBufferAccess extends BufferAccess {
  this: Buffer =>

  object transformFeedbackBufferAccess extends TransformFeedbackBufferAccessInner

  trait TransformFeedbackBufferAccessInner extends BufferAccessInner {
    val target = Macrogl.TRANSFORM_FEEDBACK_BUFFER
  }
}
