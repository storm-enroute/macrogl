package org.macrogl



import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.collection._



package object ex {

  object computing {
    object MeshBufferObject {
      def foreach[U](f: Unit => U)(implicit glex: Macroglex): Unit = macro ex.MeshBuffer.computing[U]
    }
    object AttributeBufferObject {
      def foreach[U](f: Unit => U)(implicit glex: Macroglex): Unit = macro ex.AttributeBuffer.computing[U]
    }

    def meshbuffer(layoutIndex: Int, mesh: MeshBuffer) = MeshBufferObject
    def attributebuffer(layoutIndex: Int, mesh: AttributeBuffer) = AttributeBufferObject
  }

}
