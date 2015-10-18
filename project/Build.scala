


import sbt._
import Keys._
import Process._
import java.io._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import org.stormenroute.mecha._



object MacroGLBuild extends MechaRepoBuild {

  def repoName = "macrogl"

  /* macro-gl */

  val frameworkVersion = Def.setting {
    ConfigParsers.versionFromFile(
      (baseDirectory in macrogl).value / "version.conf",
      List("macrogl_major", "macrogl_minor"))
  }

  val macroglScalaVersion = "2.11.4"

  val lwjglVersion = "2.9.0"

  val macroglSettings = Defaults.defaultSettings ++
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
      mechaNightlyKey.dependsOn(mechaNightlyKey in backendExamplesWebgl)
  )
  
  lazy val macrogl: Project = Project(
    "macrogl",
    file("."),
    settings = macroglSettings
  )
  
  /* macro-gl with WebGL back-end */
  
  val macroglWebglSettings = Defaults.defaultSettings ++ scalaJSSettings ++ Seq(
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
      "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" %
        scalaJSVersion % "test",
      "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"
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
      "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" %
        scalaJSVersion % "test",
      "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"
    )
  )
  
  lazy val macroglBuffer = Project(
    "macroglBuffer",
    file("macrogl-buffer"),
    settings = macroglBufferSettings
  )

  /* examples */
  
  val macroglExamplesSettings = Defaults.defaultSettings ++ LWJGLPlugin.lwjglSettings ++
    Seq(
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
    scalaSource in Compile := baseDirectory.value / ".." / "src" / "test" / "scala",
    resourceDirectory in Compile :=
      baseDirectory.value / ".." / "src" / "test" / "resources"
  )
  
  lazy val macroglExample = Project(
    "macroglExamples",
    file("macrogl-examples"),
    settings = macroglExamplesSettings
  ) dependsOn(macrogl)

  /* back-end examples */

  /* back-end examples using WebGL */

  val backendExamplesWebglSettings = Defaults.defaultSettings ++ scalaJSSettings ++
    MechaRepoPlugin.defaultSettings ++ Seq(
    name := "backend-examples-webgl",
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
    scalaSource in Compile :=
      baseDirectory.value / ".." / "src" / "backend-examples" / "webgl" / "scala",
    unmanagedSourceDirectories in Compile +=
      baseDirectory.value / ".." / "src" / "backend-examples" / "common" / "scala",
    resourceDirectory in Compile :=
      baseDirectory.value / ".." / "src" / "backend-examples" / "common" / "resources",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" %
        scalaJSVersion % "test",
      "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"
    ),
    mechaBuildOutputRepoKey := "git@github.com:storm-enroute/builds.git",
    mechaBuildOutputBranchKey := "gh-pages",
    mechaBuildOutputPathKey := "macrogl",
    mechaBuildOutputExpirationDaysKey := 7,
    mechaBuildOutputSrcPathKey :=
      (baseDirectory.value).toString,
    mechaPublishBuildOutputKey <<=
      //mechaPublishBuildOutputKey.dependsOn(fastOptJS in Compile, fullOptJS in Compile)
      mechaPublishBuildOutputKey.dependsOn(fastOptJS in Compile)
  )

  lazy val backendExamplesWebgl = Project(
    "backendExamplesWebgl",
    file("backend-examples-webgl"),
    settings = backendExamplesWebglSettings
  ) dependsOn (macroglWebgl)

  /* back-end examples using LWJGL */

  val backendExamplesLwjglSettings = Defaults.defaultSettings ++
    LWJGLPlugin.lwjglSettings ++ Seq(
    name := "backend-examples-lwjgl",
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
    scalaSource in Compile :=
      baseDirectory.value / ".." / "src" / "backend-examples" / "lwjgl" / "scala",
    unmanagedSourceDirectories in Compile +=
      baseDirectory.value / ".." / "src" / "backend-examples" / "common" / "scala",
    resourceDirectory in Compile :=
      baseDirectory.value / ".." / "src" / "backend-examples" / "common" / "resources"
  )

  lazy val backendExamplesLwjgl = Project(
    "backendExamplesLwjgl",
    file("backend-examples-lwjgl"),
    settings = backendExamplesLwjglSettings
  ) dependsOn (macrogl)
  
}
