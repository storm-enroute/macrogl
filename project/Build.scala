import sbt._
import Keys._
import Process._
import java.io.File

import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._


object MacroGLBuild extends Build {

  /* macro-gl */

  val publishUser = "SONATYPE_USER"
  
  val publishPass = "SONATYPE_PASS"
  
  val macroglVersion = "0.4-SNAPSHOT"
  val macroglScalaVersion = "2.11.0"
  
  val userPass = for {
    user <- sys.env.get(publishUser)
    pass <- sys.env.get(publishPass)
  } yield (user, pass)

  val publishCreds: Seq[Setting[_]] = Seq(userPass match {
    case Some((user, pass)) =>
      credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
    case None =>
      // prevent publishing
      publish <<= streams.map(_.log.info("Publishing to Sonatype is disabled since the \"" + publishUser + "\" and/or \"" + publishPass + "\" environment variables are not set."))
  })

  val macroglSettings = Defaults.defaultSettings ++ publishCreds ++ LWJGLPlugin.lwjglSettings ++ Seq(
    name := "macrogl",
    organization := "com.storm-enroute",
    version := macroglVersion,
    scalaVersion := macroglScalaVersion,

    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / "src" / "macrogl" / "scala",
    unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "api-opengl" / "scala",
    unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "macroglex" / "scala",
    libraryDependencies <+= scalaVersion { sv =>
      "org.scala-lang" % "scala-compiler" % sv
    },
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <url>http://storm-enroute.com/</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://opensource.org/licenses/BSD-3-Clause</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:storm-enroute/macrogl.git</url>
        <connection>scm:git:git@github.com:storm-enroute/macrogl.git</connection>
      </scm>
      <developers>
        <developer>
          <id>axel22</id>
          <name>Aleksandar Prokopec</name>
          <url>http://axel22.github.com/</url>
        </developer>
      </developers>
  )
  
  lazy val macrogl = Project(
    "macrogl",
    file("."),
    settings = macroglSettings
  )
  
  /* macro-gl with WebGL back-end */
  
  val macroglWebglSettings = Defaults.defaultSettings ++ scalaJSSettings ++ Seq(
    name := "macrogl-webgl",
    version := macroglVersion,
    scalaVersion := macroglScalaVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "macrogl" / "scala",
    unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "src" / "api-webgl" / "scala",
    libraryDependencies <+= scalaVersion { sv =>
      "org.scala-lang" % "scala-compiler" % sv
    },
    libraryDependencies ++= Seq(
      "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test",
      "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.4"
    )
  )
  
  lazy val macroglWebgl = Project(
    "macroglWebgl",
    file("macrogl-webgl"),
    settings = macroglWebglSettings
  ) dependsOn(macroglBuffer)

  /* java.nio.Buffer's variant for macro-gl */

  val macroglBufferSettings = Defaults.defaultSettings ++ scalaJSSettings ++ Seq(
    name := "macrogl-buffer",
    version := macroglVersion,
    scalaVersion := macroglScalaVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "buffer" / "scala",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test",
      "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.4"
    )
  )
  
  lazy val macroglBuffer = Project(
    "macroglBuffer",
    file("macrogl-buffer"),
    settings = macroglBufferSettings
  )

  /* examples */
  
  val macroglExamplesSettings = Defaults.defaultSettings ++ LWJGLPlugin.lwjglSettings ++ Seq(
    name := "macrogl-examples",
    version := macroglVersion,
    scalaVersion := macroglScalaVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "test" / "scala"
  )
  
  lazy val macroglExample = Project(
    "macroglExamples",
    file("macrogl-examples"),
    settings = macroglExamplesSettings
  ) dependsOn(macrogl)
}


