package org.macrogl



import scala.language.dynamics
import scala.collection._
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
import org.lwjgl.opengl.GL43._
import org.lwjgl.BufferUtils._



final class Program(val name: String)(
  val shaders: Program.Shader*
) extends Handle {
  private var pindex = -1
  private val result = new Array[Int](1)
  private val floatArray = createFloatBuffer(16)

  object uniform extends Dynamic {
    def location(varname: String) = {
      val loc = glGetUniformLocation(index, varname)
      if (loc == -1) throw new Program.Exception("could not send uniform: " + varname)
      loc
    }
    def updateDynamic(varname: String)(v: Any) = {
      val l = location(varname)
      v match {
        case v: Float => glUniform1f(l, v)
        case (x: Float, y: Float) => glUniform2f(l, x, y)
        case (x: Float, y: Float, z: Float) => glUniform3f(l, x, y, z)
        case v: Vec3 => glUniform3f(l, v.x, v.y, v.z)
        case (x: Float, y: Float, z: Float, w: Float) => glUniform4f(l, x, y, z, w)
        case v: Double => glUniform1f(l, v.toFloat)
        case (x: Double, y: Double) => glUniform2f(l, x.toFloat, y.toFloat)
        case (x: Double, y: Double, z: Double) => glUniform3f(l, x.toFloat, y.toFloat, z.toFloat)
        case (x: Double, y: Double, z: Double, w: Double) => glUniform4f(l, x.toFloat, y.toFloat, z.toFloat, w.toFloat)
        case v: Int => glUniform1i(l, v)
        case (x: Int, y: Int) => glUniform2i(l, x, y)
        case (x: Int, y: Int, z: Int) => glUniform3i(l, x, y, z)
        case (x: Int, y: Int, z: Int, w: Int) => glUniform4i(l, x, y, z, w)
        case m: Matrix =>
          var i = 0
          while (i < 16) {
            floatArray.put(i, m.array(i).toFloat)
            i += 1
          }
          floatArray.clear()
          glUniformMatrix4(l, false, floatArray)
      }
    }
  }

  def index = pindex

  def dispatch(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) {
    assert(shaders.length == 1, "Only a single compute shader allowed.")
    shaders.head match {
      case Program.Shader.Compute(_, _) =>
        val opidx = GL11.glGetInteger(GL_CURRENT_PROGRAM)
        if (opidx != index) glUseProgram(pindex)
        try glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
        finally if (opidx != index) glUseProgram(opidx)
      case _ =>
        throw new Program.Exception("Can only dispatch a compute shader.")
    }
  }

  def acquire() {
    release()
    pindex = glCreateProgram()
    for (s <- shaders) s.attach(pindex, name)
    status.check()
  }

  def release() {
    if (pindex != -1) {
      glDeleteProgram(pindex)
      pindex = -1
      for (s <- shaders) s.detach()
    }
  }

}


object Program {
  case class Exception(msg: String) extends java.lang.Exception(msg)

  sealed trait Shader {
    private var sindex = -1
    private val srcarray = Array[CharSequence](source)
    private val result = new Array[Int](1)

    private def processShaderErrors(shname: String, flag: Int, phase: String, shindex: Int, pname: String) {
      val res = glGetShaderi(shindex, flag)
      if (res == GL_FALSE) {
        val errormsg = glGetShaderInfoLog(shindex, 512)
        throw new Program.Exception("error %s %s in shader %s\n%s".format(phase, shname, pname, errormsg))
      }
    }

    private def processProgramErrors(pindex: Int, flag: Int, phase: String, pname: String) {
      val res = glGetProgrami(pindex, flag)
      if (res == GL_FALSE) {
        val errormsg = glGetProgramInfoLog(pindex, 512)
        throw new Program.Exception("error %s program %s\n%s".format(phase, pname, errormsg))
      }
    }

    def name: String

    def mode: Int

    def source: String

    def afterAttach: Int => Unit

    def attach(pindex: Int, pname: String) {
      val s = glCreateShader(mode)
      glShaderSource(s, srcarray)
      glCompileShader(s)
      processShaderErrors(name, GL_COMPILE_STATUS, "compiling", s, pname)
      glAttachShader(pindex, s)
      afterAttach(pindex)
      glLinkProgram(pindex)
      processProgramErrors(pindex, GL_LINK_STATUS, "linking", pname)
      glValidateProgram(pindex)
      processProgramErrors(pindex, GL_VALIDATE_STATUS, "validating", pname)
      status.check()
    }

    def detach() {
      if (sindex != -1) {
        glDeleteShader(sindex)
        sindex = -1
      }
    }
  }

  object Shader {

    case class Vertex(source: String, afterAttach: Int => Unit = x => {}) extends Shader {
      def name = "Vertex shader"
      def mode = GL_VERTEX_SHADER
    }
  
    case class Geometry(source: String, afterAttach: Int => Unit = x => {}) extends Shader {
      def name = "Geometry shader"
      def mode = GL_GEOMETRY_SHADER
    }
  
    case class Fragment(source: String, afterAttach: Int => Unit = x => {}) extends Shader {
      def name = "Fragment shader"
      def mode = GL_FRAGMENT_SHADER
    }

    case class Compute(source: String, afterAttach: Int => Unit = x => {}) extends Shader {
      def name = "Compute shader"
      def mode = GL_COMPUTE_SHADER
    }

  }

}




