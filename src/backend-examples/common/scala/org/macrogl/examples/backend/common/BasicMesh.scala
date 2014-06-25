package org.macrogl.examples.backend.common

import org.macrogl
import org.macrogl.{ Macrogl => GL }
import org.macrogl.using

import org.macrogl.math._

/**
 * Basic example to try the package org.macrogl.math
 */
class BasicMesh(width: Int, height: Int, print: String => Unit, systemUpdate: () => Boolean,
  systemInit: () => macrogl.Macrogl, systemClose: () => Unit)
  extends DemoRenderable {

  class BasicMeshListener extends org.macrogl.FrameListener {
    // (continue, render, close)
    var funcs: Option[(() => Boolean, org.macrogl.FrameEvent => Unit, () => Unit)] = None

    def init(): Unit = {
      print("Basic Mesh: init")

      implicit val mgl = systemInit()

      val vertexSource = """
        uniform mat4 projection;
        uniform mat4 transform;
        
        attribute vec3 position;
        attribute vec3 color;
  
        varying vec3 vColor;
        
        void main(void) {
          gl_Position = projection * transform * vec4(position, 1.0);
          vColor = color;
        }
        """

      val fragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        varying vec3 vColor;
  
        void main(void) {
          gl_FragColor = vec4(vColor, 1.0);
        }
        """

      val resourcesDir = "/org/macrogl/examples/backend/common/"
      val objFileName = "sphere.obj"
      val mtlFileName = "sphere.mtl"

      macrogl.utils.TextResources.get(resourcesDir + objFileName, resourcesDir + mtlFileName) {
        case Array(objFileContent, mtlFileContent) =>

          /*macrogl.Utils.out.println("Obj: " + objFileContent(0))
          macrogl.Utils.out.println("Mtl: " + mtlFileContent(0))*/

          val objs = macrogl.utils.SimpleOBJParser.parseOBJ(objFileContent, Map(mtlFileName -> mtlFileContent))
          
          macrogl.Utils.out.println("OBJ contains "+objs.size+" object(s)")
          
          objs.foreach { case (name, obj) =>
            
          }

          val indicesBuffer = mgl.createBuffer

          // position: 1 face (4 vertices)
          val vertexBufferData = macrogl.Macrogl.createFloatData(4 * 3)
          vertexBufferData.put(-1f).put(-0.5f).put(0f)
          vertexBufferData.put(1f).put(-0.5f).put(0f)
          vertexBufferData.put(1f).put(0.5f).put(0f)
          vertexBufferData.put(-1f).put(0.5f).put(0f)
          vertexBufferData.rewind

          // color: 1 face (4 vertices)
          val colorBufferData = macrogl.Macrogl.createFloatData(4 * 3)
          colorBufferData.put(1f).put(0f).put(0f)
          colorBufferData.put(0f).put(1f).put(0f)
          colorBufferData.put(1f).put(1f).put(0f)
          colorBufferData.put(0f).put(0f).put(1f)
          colorBufferData.rewind

          // 1 faces = 2 triangles
          val indicesBufferData = macrogl.Macrogl.createShortData(2 * 3)
          indicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
          indicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
          indicesBufferData.rewind

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)

          mgl.viewport(0, 0, width, height)
          mgl.clearColor(0.5f, 0.5f, 0.5f, 1)

          // Setup matrices
          val projection = Matrix4f.perspective3D(70f, width.toFloat / height.toFloat, 0.01f, 10f)
          val transformStack = new MatrixStack(new Matrix4f)

          val pp = new macrogl.Program("BasicProjection3D")(
            macrogl.Program.Shader.Vertex(vertexSource),
            macrogl.Program.Shader.Fragment(fragmentSource))
          pp.acquire()

          val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
          vertexBuffer.acquire()
          vertexBuffer.send(0, vertexBufferData)
          for (_ <- using attributebuffer (vertexBuffer)) {
            val vertexAttrsLocs = Array(mgl.getAttribLocation(pp.token, "position"))
            val vertexAttrsCfg = Array((0, 3))

            vertexBuffer.locations = vertexAttrsLocs
            vertexBuffer.attribs = vertexAttrsCfg

            vertexBuffer.setAttributePointers()
          }

          val colorBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, colorBufferData.remaining() / 3, 3)
          colorBuffer.acquire()
          colorBuffer.send(0, colorBufferData)
          for (_ <- using attributebuffer (colorBuffer)) {
            val colorAttrsLocs = Array(mgl.getAttribLocation(pp.token, "color"))
            val colorAttrsCfg = Array((0, 3))

            colorBuffer.locations = colorAttrsLocs
            colorBuffer.attribs = colorAttrsCfg

            colorBuffer.setAttributePointers()
          }

          print("Basic Mesh: ready")

          var continueCondition: Boolean = true

          def continue(): Boolean = {
            continueCondition
          }

          var currentRotation: Float = 0f
          val rotationVelocity: Float = 90f

          def render(fe: org.macrogl.FrameEvent): Unit = {
            transformStack.push // Save the current transformation matrix

            // Anime the rotation using the data from the FrameEvent
            currentRotation += rotationVelocity * fe.elapsedTime

            transformStack.current = Matrix4f.translate3D(new Vector3f(0, 0, -3)) *
              Matrix4f.rotation3D(currentRotation, new Vector3f(0, 1, 0))

            for {
              _ <- using program (pp)
            } {
              mgl.clear(GL.COLOR_BUFFER_BIT)

              vertexBuffer.enableAttributeArrays()
              colorBuffer.enableAttributeArrays()

              pp.uniform.projection = projection
              pp.uniform.transform = transformStack.current

              mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
              mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

              colorBuffer.disableAttributeArrays()
              vertexBuffer.disableAttributeArrays()
            }

            transformStack.pop // Restore the transformation matrix 
            continueCondition = systemUpdate()
          }

          def close(): Unit = {
            print("Basic Mesh: closing")

            mgl.deleteBuffer(indicesBuffer)

            colorBuffer.release()
            vertexBuffer.release()
            pp.release()

            systemClose()

            print("Basic Mesh: closed")
          }

          funcs = Some(continue, render, close)
      }
    }

    val errMsg = "Basic Mesh: not ready"

    def continue(): Boolean = {
      funcs match {
        case Some((continueFunc, _, _)) => continueFunc()
        case None => true
      }
    }
    def render(fe: org.macrogl.FrameEvent): Unit = {
      funcs match {
        case Some((_, renderFunc, _)) => renderFunc(fe)
        case None => // nothing to do, wait
      }
    }
    def close(): Unit = {
      funcs match {
        case Some((_, _, closeFunc)) => closeFunc()
        case None => throw new RuntimeException(errMsg)
      }

      funcs = None
    }
  }

  def start(): Unit = {
    macrogl.Utils.startFrameListener(new BasicMeshListener)
  }
}