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
        
        void main(void) {
          gl_Position = projection * transform * vec4(position, 1.0);
        }
        """

      val fragmentSource = """
        #ifdef GL_ES
        precision mediump float;
        #endif
 
        uniform vec3 color;
  
        void main(void) {
          gl_FragColor = vec4(color, 1.0);
        }
        """

      val resourcesDir = "/org/macrogl/examples/backend/common/"
      val objFileName = "cube.obj"
      val mtlFileName = "cube.mtl"

      macrogl.utils.TextResources.get(resourcesDir + objFileName, resourcesDir + mtlFileName) {
        case Array(objFileContent, mtlFileContent) =>

          val objs = macrogl.utils.SimpleOBJParser.parseOBJ(objFileContent, Map(mtlFileName -> mtlFileContent))
          val meshes = macrogl.utils.SimpleOBJParser.convOBJObjectToTriMesh(objs)

          macrogl.Utils.out.println("OBJ file contains " + objs.size + " object(s)")
          objs.foreach {
            case (name, obj) =>
              macrogl.Utils.out.println("  " + obj + " of " + obj.vertices.size + " vertices, " + obj.texCoordinates.size + " tex and " + obj.normals.size + " normals in " + obj.groups.size + " group(s)")
              obj.groups.foreach { group =>
                macrogl.Utils.out.println("    " + group + " of " + group.parts.size + " part(s)")
                group.parts.foreach { part =>
                  macrogl.Utils.out.println("      " + part + " of " + part.faces.size + " face(s)")
                }
              }
          }

          def yesNo(opt: Option[_]): String = opt match {
            case Some(_) => "yes"
            case None => "no"
          }

          macrogl.Utils.out.println("After conversion, there are " + meshes.size + " mesh(es)")
          meshes.foreach {
            case (name, mesh) =>
              macrogl.Utils.out.println("  " + mesh + " of " + mesh.vertices.size + " vertices, using tex:" + yesNo(mesh.texCoordinates) + ", using normals:" + yesNo(mesh.normals))
              macrogl.Utils.out.println("    Vertex positions:")
              mesh.vertices.foreach { v =>
                macrogl.Utils.out.println("      " + v)
              }
              mesh.texCoordinates match {
                case Some(texs) => {
                  macrogl.Utils.out.println("    Vertex texture coordinates:")
                  texs.foreach { v =>
                    macrogl.Utils.out.println("      " + v)
                  }
                }
                case None =>
              }
              mesh.normals match {
                case Some(norms) => {
                  macrogl.Utils.out.println("    Vertex normals:")
                  norms.foreach { v =>
                    macrogl.Utils.out.println("      " + v)
                  }
                }
                case None =>
              }
              macrogl.Utils.out.println("    Parts:")
              mesh.submeshes.foreach { submesh =>
                macrogl.Utils.out.println("      " + submesh + " of " + submesh.tris.size + " triangle(s)")
                submesh.tris.foreach {
                  case (v0, v1, v2) =>
                    macrogl.Utils.out.println("          (" + v0 + ", " + v1 + ", " + v2 + ")")
                }
              }
          }

          // Inject TriMesh into Buffers
          val mesh = meshes("Cube")

          val vertexBuffer = mgl.createBuffer
          val indicesBuffers = mesh.submeshes.map { submesh => mgl.createBuffer }
          
          //val indicesBuffers = new scala.Array[scala.scalajs.js.Object](2)
          //val retArray = new Array[org.scalajs.dom.WebGLBuffer](2)
          //val indicesBuffers = new scala.Array[org.scalajs.nio.Buffer](2)

          val vertexBufferData = macrogl.Macrogl.createFloatData(mesh.vertices.size * 3) // 3 components per vertex
          mesh.vertices.foreach { v =>
            v.store(vertexBufferData)
          }
          vertexBufferData.rewind()

          val indicesBuffersData = mesh.submeshes.map { submesh =>
            val data = macrogl.Macrogl.createShortData(submesh.tris.size * 3) // 3 vertices per triangle
            
            submesh.tris.foreach { case (v0, v1, v2) =>
              data.put(v0.toShort)
              data.put(v1.toShort)
              data.put(v2.toShort)
            }
            
            data.rewind()
            data
          }

          // position: 1 face (4 vertices)
          /*val vertexBufferData = macrogl.Macrogl.createFloatData(4 * 3)
          vertexBufferData.put(-1f).put(-0.5f).put(0f)
          vertexBufferData.put(1f).put(-0.5f).put(0f)
          vertexBufferData.put(1f).put(0.5f).put(0f)
          vertexBufferData.put(-1f).put(0.5f).put(0f)
          vertexBufferData.rewind

          // 1 faces = 2 triangles
          val indicesBufferData = macrogl.Macrogl.createShortData(2 * 3)
          indicesBufferData.put(0.toShort).put(1.toShort).put(3.toShort)
          indicesBufferData.put(1.toShort).put(2.toShort).put(3.toShort)
          indicesBufferData.rewind

          mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
          mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)*/

          /*mgl.viewport(0, 0, width, height)
          mgl.clearColor(0.5f, 0.5f, 0.5f, 1)*/

          // Setup matrices
          val projection = Matrix4f.perspective3D(70f, width.toFloat / height.toFloat, 0.01f, 10f)
          val transformStack = new MatrixStack(new Matrix4f)

          /*val pp = new macrogl.Program("BasicProjection3D")(
            macrogl.Program.Shader.Vertex(vertexSource),
            macrogl.Program.Shader.Fragment(fragmentSource))
          pp.acquire()*/

          /*val vertexBuffer = new macrogl.AttributeBuffer(GL.STATIC_DRAW, vertexBufferData.remaining() / 3, 3)
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
          }*/

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

            /*for {
              _ <- using program (pp)
            } {*/
              //mgl.clear(GL.COLOR_BUFFER_BIT)

              /*vertexBuffer.enableAttributeArrays()
              colorBuffer.enableAttributeArrays()*/

              /*pp.uniform.projection = projection
              pp.uniform.transform = transformStack.current*/

              //mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
              //mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

              /*colorBuffer.disableAttributeArrays()
              vertexBuffer.disableAttributeArrays()*/
            //}

            transformStack.pop // Restore the transformation matrix 
            continueCondition = systemUpdate()
          }

          def close(): Unit = {
            print("Basic Mesh: closing")

            /*mgl.deleteBuffer(indicesBuffer)

            colorBuffer.release()
            vertexBuffer.release()*/
            
            
            //pp.release()

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