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
      val objFileName = "test.obj"
      val mtlFileName = "test.mtl"

      macrogl.utils.TextResources.get(resourcesDir + objFileName, resourcesDir + mtlFileName) {
        case Array(objFileContent, mtlFileContent) =>

          val pp = new macrogl.Program("BasicProjection3D")(
            macrogl.Program.Shader.Vertex(vertexSource),
            macrogl.Program.Shader.Fragment(fragmentSource))
          pp.acquire()

          mgl.useProgram(pp.token)

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
          val mesh = meshes("Test")

          val vertexBufferData = macrogl.Macrogl.createFloatData(mesh.vertices.size * 3) // 3 components per vertex
          mesh.vertices.foreach { v =>
            v.store(vertexBufferData)
          }
          vertexBufferData.rewind()

          val indicesBuffersData = mesh.submeshes.map { submesh =>
            val data = macrogl.Macrogl.createShortData(submesh.tris.size * 3) // 3 vertices per triangle

            submesh.tris.foreach {
              case (v0, v1, v2) =>
                data.put(v0.toShort)
                data.put(v1.toShort)
                data.put(v2.toShort)
            }

            data.rewind()
            data
          }

          val positionAttribLoc = mgl.getAttribLocation(pp.token, "position")
          val colorUniLoc = mgl.getUniformLocation(pp.token, "color")
          val projectionUniLoc = mgl.getUniformLocation(pp.token, "projection")
          val transformUniLoc = mgl.getUniformLocation(pp.token, "transform")

          val vertexBuffer = mgl.createBuffer
          mgl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
          mgl.bufferData(GL.ARRAY_BUFFER, vertexBufferData, GL.STATIC_DRAW)
          mgl.vertexAttribPointer(positionAttribLoc, 3, GL.FLOAT, false, 0, 0)

          val indicesBuffers = indicesBuffersData.map { indicesBufferData =>
            val indicesBuffer = mgl.createBuffer
            mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
            mgl.bufferData(GL.ELEMENT_ARRAY_BUFFER, indicesBufferData, GL.STATIC_DRAW)
            indicesBuffer
          }

          // Setup matrices
          val projection = Matrix4f.perspective3D(70f, width.toFloat / height.toFloat, 0.01f, 10f)
          val transformStack = new MatrixStack(new Matrix4f)

          // Viewport and gray background
          mgl.viewport(0, 0, width, height)
          mgl.clearColor(0.5f, 0.5f, 0.5f, 1)

          // Enable depth test
          mgl.enable(GL.DEPTH_TEST)
          mgl.depthFunc(GL.LESS)

          mgl.enableVertexAttribArray(positionAttribLoc)

          print("Basic Mesh: ready")

          var continueCondition: Boolean = true

          def continue(): Boolean = {
            continueCondition
          }

          var currentRotation: Float = 0f
          val rotationVelocity: Float = 90f

          def render(fe: org.macrogl.FrameEvent): Unit = {
            transformStack.push // Save the current transformation matrix

            mgl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT)

            // Anime the rotation using the data from the FrameEvent
            currentRotation += rotationVelocity * fe.elapsedTime

            transformStack.current = Matrix4f.translate3D(new Vector3f(0, 0, -3)) *
              Matrix4f.rotation3D(30, new Vector3f(1, 0, 0)) *
              Matrix4f.rotation3D(currentRotation, new Vector3f(0, 1, 0))

            mgl.uniformMatrix4f(projectionUniLoc, projection)
            mgl.uniformMatrix4f(transformUniLoc, transformStack.current)

            var i = 0
            while (i < mesh.submeshes.size) {
              val submesh = mesh.submeshes(i)
              val indicesBuffer = indicesBuffers(i)
              val indicesBufferData = indicesBuffersData(i)
              
              mgl.uniform3f(colorUniLoc, submesh.material.get.diffuseColor.get)
              mgl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indicesBuffer)
              mgl.drawElements(GL.TRIANGLES, indicesBufferData.remaining, GL.UNSIGNED_SHORT, 0)

              i += 1
            }

            transformStack.pop // Restore the transformation matrix 
            continueCondition = systemUpdate()
          }

          def close(): Unit = {
            print("Basic Mesh: closing")

            mgl.disableVertexAttribArray(positionAttribLoc)

            indicesBuffers.foreach { b =>
              mgl.deleteBuffer(b)
            }

            mgl.deleteBuffer(vertexBuffer)

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