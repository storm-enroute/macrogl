package org.macrogl.utils

import org.macrogl.math._

object SimpleOBJParser {
  case class Material(name: String,
    ambientColor: Option[Vector3f] = None,
    diffuseColor: Option[Vector3f] = None,
    specularColor: Option[Vector3f] = None,
    specularCoef: Option[Float] = None,
    transparency: Option[Float] = None,
    illuminationModelIndex: Option[Int] = None,
    ambientColorTexture: Option[String] = None,
    diffuseColorTexture: Option[String] = None,
    specularColorTexture: Option[String] = None,
    specularCoefTexture: Option[String] = None,
    bumpMapTexture: Option[String] = None,
    displacementMapTexture: Option[String] = None,
    decalTexture: Option[String] = None) {
    override def toString(): String = "Material("+name+")"
  }

  case class SubMesh(material: Material, vertices: Array[Vector3f],
      textureCoordinates: Option[Array[Vector3f]], normals: Option[Array[Vector3f]],
      tris: Array[(Int, Int, Int)]) {
    override def toString(): String = "SubMesh(mat:"+material.name+")"
  }

  case class Mesh(name: String, submeshes: Array[SubMesh]) {
    override def toString(): String = "Mesh("+name+")"
  }
  
  def parse(objFile: Array[String], mtlFiles: Map[String, Array[String]]): Map[String, Mesh] = {
    ??? // Work In Progress
  }
}