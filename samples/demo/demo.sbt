name := "demo"

version := Common.version

organization := Common.organization

scalaVersion := Common.scalaVersion

libraryDependencies += organization.value %% "mnemonic" % version.value

resolvers += Resolver.sonatypeRepo("snapshots")

javaOptions in Test ++= Seq("-Dmnemonic.mock=true", "-Dlogger.mnemonic=ERROR")
