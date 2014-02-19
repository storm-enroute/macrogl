import sbt._
import Keys._
import Process._
import java.io.File



object MacroGLBuild extends Build {

  /* macro-gl */

  val lwjglVersion = "2.9.0"

  val macroglSettings = Defaults.defaultSettings ++ Seq (
    name := "macrogl",
    organization := "org.stormenroute",
    version := "0.0.1",
    scalaVersion := "2.10.2",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise"
    ),
    libraryDependencies <+= scalaVersion { sv =>
      "org.scala-lang" % "scala-compiler" % sv
    },
    libraryDependencies ++= Seq(
      "org.lwjgl.lwjgl" % "lwjgl" % lwjglVersion,
      "org.lwjgl.lwjgl" % "lwjgl_util" % lwjglVersion
    )
  )

  lazy val macrogl = Project(
    "macrogl",
    file("."),
    settings = macroglSettings
  )

}
