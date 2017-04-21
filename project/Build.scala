


import sbt._
import Keys._
import Process._
import java.io._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.stormenroute.mecha._



object MacroGLBuild extends MechaRepoBuild {

  def repoName = "macrogl"

  /* macro-gl */

  val frameworkVersion = Def.setting {
    ConfigParsers.versionFromFile(
      (baseDirectory in macrogl).value / "version.conf",
      List("macrogl_major", "macrogl_minor"))
  }

  val macroglScalaVersion = "2.12.2"

  val lwjglVersion = "2.9.3"

  val macroglSettings =
    MechaRepoPlugin.defaultSettings ++ LWJGLPlugin.lwjglSettings ++ Seq(
      name := "macrogl",
      organization := "com.storm-enroute",
      version <<= frameworkVersion,
      scalaVersion := macroglScalaVersion,
      LWJGLPlugin.lwjgl.version := lwjglVersion,
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-Xexperimental",
        "-optimise",
        "-feature"
      ),
      scalaSource in Compile := baseDirectory.value / "src" / "macrogl" / "scala",
      unmanagedSourceDirectories in Compile +=
        baseDirectory.value / "src" / "api-opengl" / "scala",
      unmanagedSourceDirectories in Compile +=
        baseDirectory.value / "src" / "macroglex" / "scala",
      libraryDependencies <+= scalaVersion { sv =>
        "org.scala-lang" % "scala-compiler" % sv
      },
      resolvers ++= Seq(
        "Sonatype OSS Snapshots" at
          "https://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype OSS Releases" at
          "https://oss.sonatype.org/content/repositories/releases"
      ),
      ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet,
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
        </developers>,
      mechaPublishKey := { publish.value },
      mechaDocsRepoKey := "git@github.com:storm-enroute/apidocs.git",
      mechaDocsBranchKey := "gh-pages",
      mechaDocsPathKey := "macrogl",
      mechaNightlyKey <<=
        mechaNightlyKey.dependsOn(compile in Compile)
    )
  
  lazy val macrogl: Project = Project(
    "macrogl",
    file("."),
    settings = macroglSettings
  )
  
  /* macro-gl with WebGL back-end */
  
  val macroglWebglSettings = Seq(
    name := "macrogl-webgl",
    version <<= frameworkVersion,
    scalaVersion := macroglScalaVersion,
    LWJGLPlugin.lwjgl.version := lwjglVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "macrogl" / "scala",
    unmanagedSourceDirectories in Compile +=
      baseDirectory.value / ".." / "src" / "api-webgl" / "scala",
    libraryDependencies <+= scalaVersion { sv =>
      "org.scala-lang" % "scala-compiler" % sv
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    ),
    ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet
  )
  
  lazy val macroglWebgl = Project(
    "macroglWebgl",
    file("macrogl-webgl"),
    settings = macroglWebglSettings
  ) dependsOn(macroglBuffer) enablePlugins(ScalaJSPlugin)

  /* java.nio.Buffer's variant for macro-gl */

  val macroglBufferSettings = Seq(
    name := "macrogl-buffer",
    version <<= frameworkVersion,
    scalaVersion := macroglScalaVersion,
    LWJGLPlugin.lwjgl.version := lwjglVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "buffer" / "scala",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    ),
    ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet
  )
  
  lazy val macroglBuffer = Project(
    "macroglBuffer",
    file("macrogl-buffer"),
    settings = macroglBufferSettings
  ) enablePlugins(ScalaJSPlugin)

  /* examples */
  
  val macroglExamplesSettings = LWJGLPlugin.lwjglSettings ++ Seq(
    name := "macrogl-examples",
    version <<= frameworkVersion,
    scalaVersion := macroglScalaVersion,
    LWJGLPlugin.lwjgl.version := lwjglVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xexperimental",
      "-optimise",
      "-feature"
    ),
    libraryDependencies ++= Seq(
      "org.lwjgl.lwjgl" % "lwjgl-platform" % lwjglVersion,
      "org.lwjgl.lwjgl" % "lwjgl_util" % lwjglVersion //FIXME; shouldn't need
    ),
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "test" / "scala",
    resourceDirectory in Compile :=
      baseDirectory.value / ".." / "src" / "test" / "resources"
  )
  
  lazy val macroglExample = Project(
    "macroglExamples",
    file("macrogl-examples"),
    settings = macroglExamplesSettings
  ) dependsOn(macrogl)

}
