name := "mnemonic-root"

version := Common.version

organization := Common.organization

scalaVersion := Common.scalaVersion

publishArtifact := false

publishTo := Some(Resolver.file("Unused transient repository", file("target")))

lazy val core = project.in( file("core") ).enablePlugins(PlayScala)

lazy val demo = project.in( file("samples/demo") ).enablePlugins(PlayScala).dependsOn(core)

lazy val root = project.in( file(".") ).aggregate(core, demo).settings(
     aggregate in update := false
)