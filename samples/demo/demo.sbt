name := "demo"

version := Common.version

organization := Common.organization

scalaVersion := Common.scalaVersion

publishArtifact := false

publishTo := Some(Resolver.file("Unused transient repository", file("target")))

libraryDependencies += organization.value %% "mnemonic" % version.value

resolvers += Resolver.sonatypeRepo("snapshots")

javaOptions in Test ++= Seq("-Dmnemonic.mock=true", "-Dlogger.mnemonic=ERROR")
